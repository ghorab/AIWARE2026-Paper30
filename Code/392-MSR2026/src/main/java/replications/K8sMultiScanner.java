package replications;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class K8sMultiScanner {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private K8sMultiScanner() {}

    // ============================================================
    // PUBLIC API
    // ============================================================

    /** Retourne les findings des 3 outils (dédupliqués). */
    public static List<Misconfiguration> scanMisconfigurations(Path yaml) {
        Objects.requireNonNull(yaml, "yaml");
        Path file = yaml.toAbsolutePath().normalize();

        if (!Files.exists(file) || !Files.isRegularFile(file)) {
            throw new IllegalArgumentException("File not found: " + file);
        }

        ScanResult snyk = runSnykIac(file);
        ScanResult datree = runDatree(file);
        ScanResult kubeScore = runKubeScore(file);

        List<Misconfiguration> all = new ArrayList<>();
        all.addAll(snyk.findings);
        all.addAll(datree.findings);
        all.addAll(kubeScore.findings);

        return dedupKeepOrder(all);
    }

    /** Si tu veux seulement les labels (tool:ruleId). */
    public static List<String> scanMisconfigurationLabels(Path yaml) {
        List<Misconfiguration> ms = scanMisconfigurations(yaml);
        List<String> labels = new ArrayList<>(ms.size());
        for (Misconfiguration m : ms) {
            labels.add(m.getTool() + ":" + m.getRuleId());
        }
        return labels;
    }

    /** Si tu veux seulement les ruleId (sans tool). */
    public static List<String> scanRuleIdsOnly(Path yaml) {
        List<Misconfiguration> ms = scanMisconfigurations(yaml);
        List<String> ids = new ArrayList<>(ms.size());
        for (Misconfiguration m : ms) {
            ids.add(m.getRuleId());
        }
        return ids;
    }

    // ============================================================
    // TOOL RUNNERS
    // ============================================================

    private static ScanResult runSnykIac(Path yaml) {
        List<String> cmd = Arrays.asList("snyk", "iac", "test", yaml.toString(), "--json");
        return runJsonTool("snyk", cmd, new Extractor() {
            @Override public List<Misconfiguration> extract(JsonNode root) {
                return extractSnykIssues(root);
            }
        });
    }

    private static ScanResult runDatree(Path yaml) {
        List<String> cmd = Arrays.asList("datree", "test", yaml.toString(), "--output", "json");
        return runJsonTool("datree", cmd, new Extractor() {
            @Override public List<Misconfiguration> extract(JsonNode root) {
                return extractDatreeViolations(root);
            }
        });
    }

    private static ScanResult runKubeScore(Path yaml) {
        List<String> cmd = Arrays.asList("kube-score", "score", yaml.toString(), "--output-format", "json");
        return runJsonTool("kube-score", cmd, new Extractor() {
            @Override public List<Misconfiguration> extract(JsonNode root) {
                return extractKubeScoreFindings(root);
            }
        });
    }

    // ============================================================
    // GENERIC JSON TOOL RUNNER
    // ============================================================

    private static ScanResult runJsonTool(String toolName, List<String> cmd, Extractor extractor) {
        try {
            ExecOut out = exec(cmd, Duration.ofSeconds(120));
            String stdout = out.stdout == null ? "" : out.stdout.trim();

            if (stdout.isEmpty()) return new ScanResult(toolName, Collections.<Misconfiguration>emptyList());

            JsonNode root = MAPPER.readTree(stdout);
            List<Misconfiguration> findings = extractor.extract(root);
            return new ScanResult(toolName, findings);

        } catch (Exception e) {
            System.err.println("[" + toolName + "] failed: " + e.getMessage());
            return new ScanResult(toolName, Collections.<Misconfiguration>emptyList());
        }
    }

    private static ExecOut exec(List<String> cmd, Duration timeout) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(false);
        Process p = pb.start();

        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();

        Thread tOut = streamTo(p.getInputStream(), stdout);
        Thread tErr = streamTo(p.getErrorStream(), stderr);

        boolean finished = p.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
        if (!finished) {
            p.destroyForcibly();
            throw new IOException("Timeout running: " + String.join(" ", cmd));
        }

        tOut.join(2000);
        tErr.join(2000);

        return new ExecOut(
                p.exitValue(),
                stdout.toString(StandardCharsets.UTF_8.name()),
                stderr.toString(StandardCharsets.UTF_8.name())
        );
    }

    private static Thread streamTo(final InputStream in, final OutputStream out) {
        Thread t = new Thread(new Runnable() {
            @Override public void run() {
                try {
                    // Java 8 compatible copy
                    byte[] buf = new byte[8192];
                    int r;
                    while ((r = in.read(buf)) != -1) {
                        out.write(buf, 0, r);
                    }
                } catch (IOException e) {
                    // ignore or log
                } finally {
                    try { in.close(); } catch (IOException ignored) {}
                }
            }
        });
        t.setDaemon(true);
        t.start();
        return t;
    }

    // ============================================================
    // EXTRACTORS
    // ============================================================

    private static List<Misconfiguration> extractSnykIssues(JsonNode root) {
        final List<Misconfiguration> rows = new ArrayList<>();

        walk(root, new Consumer<JsonNode>() {
            @Override public void accept(JsonNode node) {
                if (!node.isObject()) return;

                JsonNode id = node.get("id");
                JsonNode title = node.get("title");
                JsonNode severity = node.get("severity");

                if (isText(id) && (isText(title) || isText(node.get("message"))) && (isText(severity) || node.has("issue"))) {
                    String ruleId = id.asText();
                    String details = isText(title) ? title.asText() : safeText(node.get("message"));
                    rows.add(new Misconfiguration("snyk", ruleId, details));
                }
            }
        });

        return dedupKeepOrder(rows);
    }

    private static List<Misconfiguration> extractDatreeViolations(JsonNode root) {
        final List<Misconfiguration> rows = new ArrayList<>();

        walk(root, new Consumer<JsonNode>() {
            @Override public void accept(JsonNode node) {
                if (!node.isObject()) return;

                String id = firstText(node, Arrays.asList("identifier", "ruleName", "name"));
                if (id == null) return;

                boolean looksLikeRule =
                        node.has("occurrences") || node.has("failed") || node.has("status")
                                || node.has("violations") || node.has("ruleResults");

                if (looksLikeRule) {
                    rows.add(new Misconfiguration("datree", id, null));
                }
            }
        });

        return dedupKeepOrder(rows);
    }

    private static List<Misconfiguration> extractKubeScoreFindings(JsonNode root) {
        final List<Misconfiguration> rows = new ArrayList<>();

        walk(root, new Consumer<JsonNode>() {
            @Override public void accept(JsonNode node) {
                if (!node.isObject()) return;

                JsonNode check = node.get("check");
                if (check != null && check.isObject()) {
                    String checkId = safeText(check.get("id"));
                    String checkName = safeText(check.get("name"));
                    String grade = safeText(node.get("grade"));

                    boolean isFinding = (grade == null) || (!"A".equalsIgnoreCase(grade));
                    if (isFinding && (checkId != null || checkName != null)) {
                        String ruleId = (checkId != null) ? checkId : checkName;
                        rows.add(new Misconfiguration("kube-score", ruleId, checkName));
                    }
                }
            }
        });

        return dedupKeepOrder(rows);
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private static void walk(JsonNode node, Consumer<JsonNode> f) {
        f.accept(node);
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> it = node.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> e = it.next();
                walk(e.getValue(), f);
            }
        } else if (node.isArray()) {
            for (JsonNode n : node) walk(n, f);
        }
    }

    private static boolean isText(JsonNode n) {
        return n != null && n.isTextual() && !n.asText().trim().isEmpty();
    }

    private static String safeText(JsonNode n) {
        return isText(n) ? n.asText() : null;
    }

    private static String firstText(JsonNode obj, List<String> keys) {
        for (String k : keys) {
            String v = safeText(obj.get(k));
            if (v != null) return v;
        }
        return null;
    }

    private static List<Misconfiguration> dedupKeepOrder(List<Misconfiguration> rows) {
        LinkedHashMap<String, Misconfiguration> m = new LinkedHashMap<>();
        for (Misconfiguration r : rows) {
            if (r.getRuleId() == null || r.getRuleId().trim().isEmpty()) continue;
            m.put(r.getTool() + "||" + r.getRuleId(), r);
        }
        return new ArrayList<>(m.values());
    }

    // ============================================================
    // TYPES
    // ============================================================

    private interface Extractor {
        List<Misconfiguration> extract(JsonNode root);
    }

    private static final class ExecOut {
        final int exitCode;
        final String stdout;
        final String stderr;

        ExecOut(int exitCode, String stdout, String stderr) {
            this.exitCode = exitCode;
            this.stdout = stdout;
            this.stderr = stderr;
        }
    }

    private static final class ScanResult {
        final String tool;
        final List<Misconfiguration> findings;

        ScanResult(String tool, List<Misconfiguration> findings) {
            this.tool = tool;
            this.findings = findings;
        }
    }

    public static final class Misconfiguration {
        private final String tool;
        private final String ruleId;
        private final String details;

        public Misconfiguration(String tool, String ruleId, String details) {
            this.tool = tool;
            this.ruleId = ruleId;
            this.details = details;
        }

        public String getTool() { return tool; }
        public String getRuleId() { return ruleId; }
        public String getDetails() { return details; }
    }
}
