
package replications;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import test.test.StackOverflowViewer.DisplayConfig;

public class ParcoursDossier {


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
    	System.out.println("############### Debut : ############### ");
    	System.out.println("");
    	System.out.println("");
    	
        File dossier = new File("C:\\Users\\Administrator\\Desktop\\rechercheScientifique\\MSR2026\\collect\\auto\\Key4");

        if (!dossier.exists() || !dossier.isDirectory()) {
            System.out.println("Dossier introuvable : " + dossier);
            return;
        }

        File[] fichiers = dossier.listFiles();

        if (fichiers != null) {
            for (File f : fichiers) {
            	String path =f.getAbsolutePath();
            	  //path = path.substring(path.lastIndexOf("All\\")+4,path.length());
               //   System.out.println(path);
                
                File jsonFile = new File(path);
           //     System.out.println(jsonFile);

                
                
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
  //      System.out.println("-------------------------  "+ cmpt  +"  -------------------------");

    

        
        
        
        


        
        
        
        
        
        
        
        
        
        if (config.showLink && item.has("link")) {
           System.out.println("" + item.get("link").asText());
        }

        if (config.showTitle && item.has("title")) {
        //	    System.out.println("" + item.get("title").asText());
        }
        
    //    System.out.println();
    }
}

