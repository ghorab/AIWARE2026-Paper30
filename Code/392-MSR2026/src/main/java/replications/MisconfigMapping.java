package replications;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MisconfigMapping {

    /**
     * Charge le fichier de mapping : FormeA ==> FormeB
     */
    public static Map<String, String> loadMapping(Path mappingFile) throws IOException {
        Map<String, String> map = new HashMap<>();

        try (BufferedReader br = Files.newBufferedReader(mappingFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;

                int idx = trimmed.indexOf("==>");
                if (idx < 0) continue;

                String key = trimmed.substring(0, idx).trim();      // FormeA
                String value = trimmed.substring(idx + 3).trim();   // FormeB

                if (!key.isEmpty() && !value.isEmpty()) {
                    map.put(key, value);
                }
            }
        }
        return map;
    }

    /**
     * Retourne FormeB si FormeA existe, sinon null
     */
    public static String getFormB(Path mappingFile, String formA) throws IOException {
        if (formA == null) return null;

        Map<String, String> map = loadMapping(mappingFile);
        return map.get(formA.trim());
    }

    // Exemple
    public static void main(String[] args) throws Exception {

        Path file = Path.of("C:\\Users\\Administrator\\Desktop\\rechercheScientifique\\MSR2026\\Replication\\Mapping table\\SnykMapper.txt"); 
        String formA = "host-device-mounted2";
        
        String result = MisconfigMapping.getFormB(file, formA);
        System.out.println(result);   // null si la clé n'existe pas
    }
}
