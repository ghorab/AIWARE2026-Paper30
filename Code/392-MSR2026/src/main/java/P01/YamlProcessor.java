package P01;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import org.yaml.snakeyaml.Yaml;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

public class YamlProcessor {

    // Nettoyer le fichier YAML en supprimant les commentaires
    public static void nettoyerFichier(String cheminFichier) throws IOException {
        List<String> lignes = Files.readAllLines(Paths.get(cheminFichier));
        List<String> lignesNettoyees = new ArrayList<>();

        for (String ligne : lignes) {
            String ligneSansCommentaire = ligne.split("#")[0].stripTrailing();
            if (!ligneSansCommentaire.trim().isEmpty()) {
                lignesNettoyees.add(ligneSansCommentaire);
            }
        }

        Files.write(Paths.get(cheminFichier), lignesNettoyees);
    }

    // Obtenir la version et le kind à partir du fichier YAML
    public static Map<String, String> getVersionKind(String cheminFichier) throws IOException {
        List<String> lignes = Files.readAllLines(Paths.get(cheminFichier));
        String ligne1 = lignes.size() > 0 ? lignes.get(0).trim() : "";
        String ligne2 = lignes.size() > 1 ? lignes.get(1).trim() : "";

        String version = "non";
        String kind = "non";
        boolean erreur = false;

        if (ligne1.contains("v1beta1")) {
            version = "v1beta1";
        } else if (ligne1.contains("v1beta2")) {
            version = "v1beta2";
        } else if (ligne1.contains("v1")) {
            version = "v1";
        } else {
            erreur = true;
        }

        if (ligne2.toLowerCase().contains("kind")) {
            kind = ligne2.split(":", 2)[1].trim();
        } else {
            erreur = true;
        }

        Map<String, String> result = new HashMap<>();
        result.put("version", version);
        result.put("kind", kind);
        result.put("erreur", String.valueOf(erreur));
        return result;
    }

    // Obtenir la clé à partir de la version et du kind
    public static String getKeyFromVersionKind(String version, String kind, String cheminJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(cheminJson));
        JsonNode definitions = root.path("definitions");

        String pattern = "io.k8s.*.*." + version + "." + kind;
        String regexPattern = pattern.replace(".", "\\.").replace("*", ".+");

        Iterator<String> fieldNames = definitions.fieldNames();
        while (fieldNames.hasNext()) {
            String key = fieldNames.next();
            if (key.matches(regexPattern)) {
                return key;
            }
        }
        return null;
    }

    // Vérifier si le schéma est déprécié
    public static String verifySchemaDeprecated(String key, String cheminJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(cheminJson));
        JsonNode definitions = root.path("definitions");
        JsonNode component = definitions.path(key);

        if (component.isMissingNode()) {
            System.out.println("Aucune correspondance trouvée.");
            return key;
        }

        String description = component.path("description").asText();
        if (description.contains("Deprecated")) {
            String ref = component.path("$ref").asText();
            if (!ref.isEmpty()) {
                int index = ref.indexOf("io.");
                if (index != -1) {
                    return ref.substring(index);
                }
            }
        }
        return key;
    }

    // Obtenir le schéma à partir de la clé
    public static JsonNode getSchemaUsingKey(String key, String cheminJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(cheminJson));
        return root.path("definitions").path(key);
    }

    // Obtenir un élément du schéma
    public static JsonNode getElementFromSchema(JsonNode schema, String key) {
        return schema.path("properties").path(key);
    }

    // Parcourir le fichier YAML
    public static void parcourirYaml(Map<String, Object> data, JsonNode schema, String cheminJson) throws IOException {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                System.out.println("######################  Deb " + key + " : ##########################");
                JsonNode element = getElementFromSchema(schema, key);
                String ref = element.path("$ref").asText();
                if (!ref.isEmpty()) {
                    int index = ref.indexOf("io.k8s");
                    if (index != -1) {
                        String subKey = ref.substring(index);
                        subKey = verifySchemaDeprecated(subKey, cheminJson);
                        JsonNode subSchema = getSchemaUsingKey(subKey, cheminJson);
                        System.out.println("Sous schéma : " + subSchema.toPrettyString());
                        System.out.println("Value : " + value);
                        // Vous pouvez appeler récursivement parcourirYaml ici si nécessaire
                    }
                }
                System.out.println("######################  Fin " + key + " : ############################");
            } else {
                System.out.println("     Element Simple ");
                JsonNode element = getElementFromSchema(schema, key);
                System.out.println("key : " + key);
                System.out.println("sous schéma : " + element.toPrettyString());
                System.out.println("value : " + value);
            }
        }
    }

    public static void main(String[] args) {
        try {
            String cheminYaml = "C:\\Users\\Administrator\\Desktop\\config\\incubator\\elasticsearch-curator\\cronjob.yaml";
            String cheminJson = "C:\\Users\\Administrator\\Desktop\\rechercheScientifique\\conference and journales\\MSR\\to be deleted\\AllJsonK8s.yml";

            System.out.println(" ################## Nettoyage Fichier :  ################## ");
            nettoyerFichier(cheminYaml);

            System.out.println(" ################## Get_Version_Kind :  ################## ");
            Map<String, String> versionKind = getVersionKind(cheminYaml);
            String version = versionKind.get("version");
            String kind = versionKind.get("kind");
            boolean erreur = Boolean.parseBoolean(versionKind.get("erreur"));
            System.out.println("version : " + version);
            System.out.println("kind : " + kind);
            System.out.println("erreur : " + erreur);

            System.out.println(" ################## Get key from schéma :  ################## ");
            String key = getKeyFromVersionKind(version, kind, cheminJson);
            System.out.println("Key : " + key);

            System.out.println(" ################## Verify schema deprecated DB :  ################## ");
            key = verifySchemaDeprecated(key, cheminJson);
            System.out.println("verify_schema_deprecated_DB , Key : " + key);

            System.out.println(" ################## get_schema_using_key_DB :  ################## ");
            JsonNode schema = getSchemaUsingKey(key, cheminJson);
            System.out.println(schema.toPrettyString());

            System.out.println("***************************************************************************************************");
            System.out.println("###################################################################################################");
            System.out.println("################################# Affichage de Test : ##############################################");
            System.out.println("###################################################################################################");
            System.out.println("***************************************************************************************************");

            Yaml yaml = new Yaml();
            InputStream inputStream = new FileInputStream(new File(cheminYaml));
            Map<String, Object> config = yaml.load(inputStream);

            parcourirYaml(config, schema, cheminJson);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
