package LLM;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class KubevalRunner {

    public static final class KubevalResult {
        public final int exitCode;
        public final String stdout;
        public final String stderr;

        public KubevalResult(int exitCode, String stdout, String stderr) {
            this.exitCode = exitCode;
            this.stdout = stdout;
            this.stderr = stderr;
        }

        public boolean isValid() {
            return exitCode == 0;
        }
    }

    /** Valide un YAML Kubernetes via kubeval (kubeval doit être installé et dans PATH). */
    public static KubevalResult validateWithKubeval(Path yamlFile, Duration timeout)
            throws IOException, InterruptedException {

        List<String> cmd = new ArrayList<>();
        cmd.add("kubeval");
        cmd.add(yamlFile.toAbsolutePath().normalize().toString());
        cmd.add("--strict");     // optionnel: validation stricte
        cmd.add("--ignore-missing-schemas"); // optionnel: évite l'échec si schéma manquant

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(false);

        Process p = pb.start();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        Thread t1 = pump(p.getInputStream(), out);
        Thread t2 = pump(p.getErrorStream(), err);

        boolean finished = p.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
        if (!finished) {
            p.destroyForcibly();
            throw new IOException("kubeval timed out after " + timeout);
        }

        t1.join();
        t2.join();

        int code = p.exitValue();
        return new KubevalResult(
                code,
                out.toString(StandardCharsets.UTF_8),
                err.toString(StandardCharsets.UTF_8)
        );
    }

    private static Thread pump(InputStream in, OutputStream out) {
        Thread t = new Thread(() -> {
            try (in; out) { in.transferTo(out); } catch (IOException ignored) {}
        });
        t.setDaemon(true);
        t.start();
        return t;
    }
}

