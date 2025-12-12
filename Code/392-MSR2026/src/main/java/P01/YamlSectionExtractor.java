package P01;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Corrige les clés YAML top-level en les rapprochant des clés attendues
 * (ex: apiVersion, kind, metadata, spec) via un script d'édition
 * utilisant: SHIFT_LEFT, SHIFT_RIGHT, INSERT, DELETE.
 */
public class YamlSectionExtractor {

    // Clés top-level attendues pour un manifest Kubernetes (tu peux ajouter d'autres clés si besoin)
    private static final List<String> EXPECTED_TOP_LEVEL_KEYS = List.of(
            "apiVersion", "kind", "metadata", "spec"
    );

    // Seuil de correction : si la distance d'édition <= seuil, on renomme
    private static final int MAX_EDIT_DISTANCE_TO_FIX = 2;

    // ---------- Ops "éditeur" ----------
    public enum OpType { SHIFT_LEFT, SHIFT_RIGHT, INSERT, DELETE }

    public static final class Op {
        public final OpType type;
        public final Character ch; // utilisé seulement pour INSERT

        private Op(OpType type, Character ch) {
            this.type = type;
            this.ch = ch;
        }
        public static Op left()  { return new Op(OpType.SHIFT_LEFT, null); }
        public static Op right() { return new Op(OpType.SHIFT_RIGHT, null); }
        public static Op del()   { return new Op(OpType.DELETE, null); }
        public static Op ins(char c) { return new Op(OpType.INSERT, c); }

        @Override public String toString() {
            return (type == OpType.INSERT) ? "INSERT('" + ch + "')" : type.name();
        }
    }

    // ---------- Lecture YAML ----------
    @SuppressWarnings("unchecked")
    public static Map<String, Object> extraireSectionsPrincipales(String cheminFichier) throws IOException {
        Yaml yaml = new Yaml();
        try (InputStream in = Files.newInputStream(Path.of(cheminFichier))) {
            Object data = yaml.load(in);
            if (!(data instanceof Map)) {
                throw new IllegalArgumentException("Le document YAML doit être un dictionnaire à la racine.");
            }
            return (Map<String, Object>) data;
        }
    }

    // ---------- Distance d'édition + script (SHIFT/INS/DEL) ----------
    /**
     * Construit un script d'édition minimal pour transformer source -> target.
     * Curseur initial = début (position 0).
     *
     * Le script utilise:
     * - SHIFT_RIGHT pour avancer le curseur quand on garde un caractère (match)
     * - DELETE pour supprimer le caractère sous le curseur (dans source)
     * - INSERT(c) pour insérer c à la position curseur
     *
     * (SHIFT_LEFT n'est pas nécessaire dans un script minimal standard, mais on l'ajoute
     *  en option dans "normalizeWithLeftShifts" si tu veux absolument en voir.)
     */
    public static List<Op> editScript(String source, String target) {
        int n = source.length();
        int m = target.length();

        int[][] dp = new int[n + 1][m + 1];
        // init
        for (int i = 0; i <= n; i++) dp[i][0] = i;      // delete i chars
        for (int j = 0; j <= m; j++) dp[0][j] = j;      // insert j chars

        // fill
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (source.charAt(i - 1) == target.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // keep
                } else {
                    dp[i][j] = 1 + Math.min(
                            dp[i - 1][j],          // delete
                            dp[i][j - 1]           // insert
                    );
                }
            }
        }

        // backtrack -> ops in reverse, then reverse
        List<Op> rev = new ArrayList<>();
        int i = n, j = m;

        while (i > 0 || j > 0) {
            // match -> move cursor right (keep)
            if (i > 0 && j > 0 && source.charAt(i - 1) == target.charAt(j - 1) && dp[i][j] == dp[i - 1][j - 1]) {
                rev.add(Op.right());
                i--; j--;
                continue;
            }
            // delete
            if (i > 0 && dp[i][j] == dp[i - 1][j] + 1) {
                rev.add(Op.del());
                i--;
                continue;
            }
            // insert
            if (j > 0 && dp[i][j] == dp[i][j - 1] + 1) {
                rev.add(Op.ins(target.charAt(j - 1)));
                j--;
                continue;
            }

            // fallback (ne devrait pas arriver)
            break;
        }

        Collections.reverse(rev);
        return rev;
    }

    public static int editDistance(String a, String b) {
        // distance = nb(INSERT/DELETE) dans ce modèle, SHIFT n'est pas compté comme coût
        // Ici, on calcule la vraie distance via dp (même logique que editScript)
        int n = a.length(), m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) dp[i][j] = dp[i - 1][j - 1];
                else dp[i][j] = 1 + Math.min(dp[i - 1][j], dp[i][j - 1]);
            }
        }
        return dp[n][m];
    }

    // ---------- Correction des clés top-level ----------
    public static String closestExpectedKey(String key, List<String> expected) {
        String best = null;
        int bestDist = Integer.MAX_VALUE;
        for (String e : expected) {
            int d = editDistance(key, e);
            if (d < bestDist) {
                bestDist = d;
                best = e;
            }
        }
        return best;
    }

    public static Map<String, Object> correctTopLevelKeys(Map<String, Object> root) {
        // LinkedHashMap pour préserver l’ordre
        Map<String, Object> corrected = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : root.entrySet()) {
            String originalKey = entry.getKey();
            Object value = entry.getValue();

            String best = closestExpectedKey(originalKey, EXPECTED_TOP_LEVEL_KEYS);
            int dist = (best == null) ? Integer.MAX_VALUE : editDistance(originalKey, best);

            if (best != null && !best.equals(originalKey) && dist <= MAX_EDIT_DISTANCE_TO_FIX && !corrected.containsKey(best)) {
                List<Op> ops = editScript(originalKey, best);

                System.out.println("Correction clé: '" + originalKey + "' -> '" + best + "' (distance=" + dist + ")");
                System.out.println("Ops (SHIFT/INSERT/DELETE): " + ops);

                corrected.put(best, value);
            } else {
                corrected.put(originalKey, value);
            }
        }
        return corrected;
    }

    // ---------- Dump YAML ----------
    public static String dumpYaml(Map<String, Object> data) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setIndent(2);
        options.setIndicatorIndent(2);

        Yaml yamlDumper = new Yaml(options);
        return yamlDumper.dump(data);
    }

    // ---------- MAIN ----------
    public static void main(String[] args) {
        try {
            String chemin = "C:\\Users\\Administrator\\Desktop\\config\\incubator\\elasticsearch-curator\\cronjob.yaml";

            Map<String, Object> sections = extraireSectionsPrincipales(chemin);

            System.out.println("=== AVANT ===");
            System.out.println(dumpYaml(sections));

            Map<String, Object> corrected = correctTopLevelKeys(sections);

            System.out.println("=== APRES ===");
            String out = dumpYaml(corrected);
            System.out.println(out);

            // écriture fichier corrigé
            Path input = Path.of(chemin);
            Path output = input.resolveSibling(input.getFileName().toString().replace(".yaml", "") + ".corrected.yaml");
            Files.writeString(output, out, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            System.out.println("Fichier corrigé écrit: " + output);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
