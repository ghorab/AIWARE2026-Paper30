package P01;




import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;  
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class LauncherV05 {
	
	
	public static String cheminJson = "C:\\Users\\Administrator\\Desktop\\rechercheScientifique\\conference and journales\\MSR\\to be deleted\\AllJsonK8s.yml";
	
	public static boolean canCast(Object obj, Class<?> targetClass) {
	    return targetClass.isInstance(obj);
	}
	
	public static Object getSection(Object data, String path) {
        if (path == null || path.isEmpty()) return data;

        String[] keys = path.split("\\.");
        Object current = data;

        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(key);
            } else if (current instanceof List) {
                try {
                    int index = Integer.parseInt(key);
                    List<?> list = (List<?>) current;
                    current = list.get(index);
                } catch (NumberFormatException e) {
                    System.out.println("Erreur : \"" + key + "\" n'est pas un index valide pour une liste.");
                    return null;
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Erreur : index hors limites pour \"" + key + "\".");
                    return null;
                }
            } else {
                System.out.println("Erreur : clé \"" + key + "\" non applicable à un objet de type " + current.getClass().getSimpleName());
                return null;
            }

            if (current == null) {
                System.out.println("Erreur : clé \"" + key + "\" introuvable.");
                return null;
            }
        }

        return current;
    }
	
	
    public static int countOccurrences(String texte, char caractere) {
        int compteur = 0;
        for (int i = 0; i < texte.length(); i++) {
            if (texte.charAt(i) == caractere) {
                compteur++;
            }
        }
        return compteur;
    }

    public static Map<String, Object> extraireSectionsPrincipales(Object data) throws Exception {

    	
        System.out.println("************//////**************");
        System.out.println("data : "+data);
        System.out.println("************//////**************");

        if (!(data instanceof Map)) {
            throw new IllegalArgumentException("Le document YAML doit être un dictionnaire à la racine.");
        }

        // Cast en Map
        @SuppressWarnings("unchecked")
        Map<String, Object> sections = (Map<String, Object>) data;
        return sections;
    }


    public static List<String> GetPrincipalesKey(Map<String, Object> data) {
    	List<String> mainKeys = new ArrayList<>();
    	if (data == null || data.isEmpty()) {
            System.out.println("Aucune clé principale trouvée.");
            return mainKeys;
        }
    	
        for (String key : data.keySet()) {
                      
            mainKeys.add(key);
            Object Obj =getSection(data, key);         
                                      
        }
		return mainKeys;
    }
    
    
    public static void ParcoursConfiguration(Map<String, Object> map,String ShemeKey) throws IOException {
    	
    	
    	System.out.println(" ---------------- Start ParcoursConfiguration -------------------");
    	System.out.println("ShemeKey : "+ShemeKey);
    	//SHEMA : 
    	
    	System.out.println(" ################## Verify schema deprecated DB :  ################## ");
    	ShemeKey = ShemaManipulation.verifySchemaDeprecated(ShemeKey, cheminJson);
        System.out.println("verify_schema_deprecated_DB , Key : " + ShemeKey);
        
        System.out.println(" ################## Get_schema_using_key_DB :  ################## ");
        JsonNode schema = ShemaManipulation.getSchemaUsingKey(ShemeKey, cheminJson);
//      System.out.println(schema.toPrettyString());      
        
        //Convert JsonNode to mapper - Unifié la representation :            
        Map<String, Object> mapper = ConvertJsonToMapper(schema);
        System.out.println("Map Json : "+mapper);
        
        //Get Properties section :      
        Map<String, Object> MapperProp=(Map<String, Object>) getSection(mapper, "properties");   
        System.out.println("Map Json sub section properties Only : "+MapperProp); 
        
   /*   //Get Required section : to be added later 
        Map<String, Object> MapperRequired=(Map<String, Object>) getSection(mapper, "required");   
        
        if(MapperRequired!=null) {       
        	System.out.println("On doit verifié les champs required : "+MapperRequired); 
        	}
   */ 
        
        Map<String, Object> MapperElem;
        
    	
        List<String> mainKeys = GetPrincipalesKey((Map<String, Object>) map);         
        System.out.println("                                  Verify required ");
        for (String key : mainKeys) {
        	System.out.println("                                       key : "+key);
        }
        System.out.println("                                  End of required verification");
             
        
        
        for (String key : mainKeys) {
            
            if(map.get(key) instanceof  String) {
            	 System.out.println("////////////////////// TYPE VERIFICATION ///////////////////////");
            	 System.out.println("**(key,data) "+key+" : "+map.get(key)+"       Type verification");
            	 MapperElem=(Map<String, Object>) getSection(MapperProp,key);   
                 System.out.println("        Mapper Element  : "+MapperElem); 
                 System.out.println("///////////////////////////////////////////////////////////////");
            }
          
            if(map.get(key) instanceof  List) {
            	
            	  List<?> liste = (List<?>) map.get(key);
            	  System.out.println("List : "+liste);
            	    for (Object element : liste) {           	            
            	            System.out.println("Texte : " + element);           	            
            	            if(canCast(element, Map.class)) {
            	               //  	GetPrincipalesKey((Map<String, Object>) map.get(key));
            	            	System.out.println("Appeles recursif dans une liste Get shema key : "+key);
            	                  	ParcoursConfiguration((Map<String, Object>) element,ShemeKey);
            	                
            	                  }
            	    }  
            }
       
            if(canCast(map.get(key), Map.class)) {
            	System.out.println("////////////////////// MAP ///////////////////////");
            	System.out.println("Appeles recursif Get shema key : "+key);
            	
            	MapperElem=(Map<String, Object>) getSection(MapperProp,key); 
            	System.out.println("        Mapper Element Recursifs : "+MapperElem);
            	
            	if(MapperElem.get("$ref")!=null) {
            		System.out.println(" ++++++++++++++++++++ ref non null : ++++++++++++++++");
            	String Shemaref =(String) MapperElem.get("$ref");
            	Shemaref=Shemaref.substring(Shemaref.indexOf("io."), Shemaref.length());
            	System.out.println("        Mapper Element Recursifs ref : "+Shemaref);
            	
            	// Shemaref : io.xxx.xxxx.xxxx.xxxx.xx.xx
            	ParcoursConfiguration((Map<String, Object>) map.get(key),Shemaref);
            	}
            	System.out.println("//////////////////////////////////////////////////");
            }
 
        }
        
        
    	System.out.println(" --------------------------- END ParcoursConfiguration ----------------------------------------");   	
    }
 
    
    
    public static Map<String, Object> ConvertJsonToMapper(JsonNode schema){
		
    	ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.convertValue(schema, Map.class);
    	
    	return map;
    }
    
    
    
    public static void main(String[] args) {
        try {
            String chemin = "C:\\Users\\Administrator\\Desktop\\config\\incubator\\elasticsearch-curator\\cronjob.yaml";
            String cheminJson = "C:\\Users\\Administrator\\Desktop\\rechercheScientifique\\conference and journales\\MSR\\to be deleted\\AllJsonK8s.yml";
                 
            System.out.println(" ################## Nettoyage Fichier :  ################## ");
            ShemaManipulation.nettoyerFichier(chemin);
                       
            System.out.println(" ################## Get_Version_Kind :  ################## ");
            Map<String, String> versionKind = ShemaManipulation.getVersionKind(chemin);
            String version = versionKind.get("version");
            String kind = versionKind.get("kind");
            boolean erreur = Boolean.parseBoolean(versionKind.get("erreur"));
            System.out.println("version : " + version);
            System.out.println("kind : " + kind);
            System.out.println("erreur : " + erreur);

            System.out.println(" ################## Get key from schéma :  ################## ");
            String key = ShemaManipulation.getKeyFromVersionKind(version, kind, cheminJson);
            System.out.println("Key : " + key);

                               
            System.out.println("\n \n \n \n \n \n \n \n \n \n");     
            
                     
            
            Yaml yaml = new Yaml();
            InputStream input = new FileInputStream(new File(chemin));
            Object data = yaml.load(input);
            
            Map<String, Object> map = (Map<String, Object>) data;
            
            System.out.println("############# Data To Get initial sheme #############");
            System.out.println("                "+map.get("apiVersion"));
            System.out.println("                "+map.get("kind"));
            System.out.println("#####################################################");
            
            
            ParcoursConfiguration((Map<String, Object>) map,key);
              
            System.out.println("---------------------------- The END ---------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

