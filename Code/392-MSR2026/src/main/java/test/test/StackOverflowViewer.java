package test.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;
import java.io.IOException;

public class StackOverflowViewer {

    // Configuration de l'affichage
	static int cmpt=0;
	
    public static class DisplayConfig {
        boolean showTitle = true;
        boolean showTags = true;
        boolean showOwnerName = true;
        boolean showLink = true;
        boolean showStats = true; // vues, score, réponses

        String requiredTag = "kubernetes"; // null = pas de filtre
        int maxItems = 10;                 // 0 ou < 0 = pas de limite
    }

    public static void main(String[] args) throws IOException {
    	
    
    	
    	String[] Keywords = {
    			"kubernetes",
                "k8s",
                "pod",
                "kubelet",
                "kubeadm",
                "kubectl",
                "kube-scheduler",
                "kube-controller-manager",
                "deployment",
                "service",
                "ingress",
                "nginx-ingress",
                "helm",
                "helm-chart",
                "minikube",
                "kind",
                "containerd",
                "istio",
    		};
    	
    	
    	for (int i = 0; i < Keywords.length; i++) {
        // 1) Charger le JSON depuis un fichier
        File jsonFile = new File("C:\\Users\\Administrator\\Desktop\\rechercheScientifique\\MSR2026\\collect\\auto\\Key\\Keywords_"+Keywords[i]+".json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonFile);

        // 2) Récupérer le tableau "items"
        ArrayNode items = (ArrayNode) root.get("items");

        // 3) Définir la configuration d'affichage
        DisplayConfig config = new DisplayConfig();
        // Exemples si tu veux changer :
        // config.showTags = false;
        // config.requiredTag = "docker";
        // config.maxItems = 5;

        // 4) Parcourir et afficher
        int displayed = 0;
        for (JsonNode item : items) {

            if (!matchTagFilter(item, config.requiredTag)) {
                continue; // ne pas afficher si le tag requis n'est pas présent
            }

            printItem(item, config);
            displayed++;

            if (config.maxItems > 0 && displayed >= config.maxItems) {
                break;
            }
        }

        if (displayed == 0) {
            System.out.println("Aucune question ne correspond au filtre.");
        }
        
    	}
    }

    // Vérifie si l'item contient le tag requis
    private static boolean matchTagFilter(JsonNode item, String requiredTag) {
        if (requiredTag == null || requiredTag.isEmpty()) {
            return true; // pas de filtre
        }

        JsonNode tagsNode = item.get("tags");
        if (tagsNode == null || !tagsNode.isArray()) {
            return false;
        }

        for (JsonNode tag : tagsNode) {
            if (requiredTag.equalsIgnoreCase(tag.asText())) {
                return true;
            }
        }
        return false;
    }

    // Affiche un item selon la config
    private static void printItem(JsonNode item, DisplayConfig config) {
    	cmpt++;
        System.out.println("-------------------------  "+ cmpt  +"  -------------------------");

        if (config.showTitle && item.has("title")) {
            System.out.println("Titre : " + item.get("title").asText());
        }

        if (config.showTags && item.has("tags")) {
            System.out.print("Tags  : ");
            JsonNode tagsNode = item.get("tags");
            for (int i = 0; i < tagsNode.size(); i++) {
                System.out.print(tagsNode.get(i).asText());
                if (i < tagsNode.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println();
        }

        if (config.showOwnerName && item.has("owner")) {
            JsonNode owner = item.get("owner");
            if (owner.has("display_name")) {
                System.out.println("Auteur : " + owner.get("display_name").asText());
            }
        }

        if (config.showStats) {
            int views = item.has("view_count") ? item.get("view_count").asInt() : -1;
            int score = item.has("score") ? item.get("score").asInt() : 0;
            int answers = item.has("answer_count") ? item.get("answer_count").asInt() : 0;
            boolean isAnswered = item.has("is_answered") && item.get("is_answered").asBoolean();

            System.out.println("Vues / Score / Réponses : " +
                    views + " / " + score + " / " + answers +
                    " (résolue : " + isAnswered + ")");
        }

        if (config.showLink && item.has("link")) {
            System.out.println("Lien  : " + item.get("link").asText());
        }

        System.out.println();
    }
}
