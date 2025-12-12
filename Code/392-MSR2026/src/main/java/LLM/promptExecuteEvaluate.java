package LLM;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import P01.YamlSectionExtractor;
import replications.K8sMultiScanner;

public class promptExecuteEvaluate {
	
	static String hfToken="";

	
	public static void saveStringToFile(String content, Path file) throws IOException {
	    Files.writeString(
	        file,
	        content,
	        StandardCharsets.UTF_8
	    );
	}
	
	
	public static int promptANDevaluat(String prompt,String model1) throws Exception {
		
        
        
        System.out.println("       prompt   :"+prompt);
        String result = HuggingFaceClientRouter.generate(
                hfToken,
                model1,
                prompt
        );
		
		
        System.out.println(result);
        saveStringToFile(result, Path.of("temp.yaml"));
        
        
        
        
        var res = KubevalRunner.validateWithKubeval(
                Path.of("temp.yaml"),
                Duration.ofSeconds(30)
        		 );
		
        if (!res.stdout.isBlank() || !res.stderr.isBlank()) {
            return 0;
        }
		
		
		
		
        String configFile ="temp/temp.yaml";
        List<String> ids = K8sMultiScanner.scanRuleIdsOnly(Path.of(configFile));
        System.out.println(ids);
        
        int score =0;
        if(!ids.isEmpty()) { //not correct we will introduce Kubecurity to try to correct 
  
        	Map<String, Object> root =
                    YamlSectionExtractor.extraireSectionsPrincipales(configFile.toString());

            // 3️⃣ Corriger les clés (shift / insert / delete)
            Map<String, Object> corrected =
                    YamlSectionExtractor.correctTopLevelKeys(root);

            // 4️⃣ Re-générer le YAML
            String correctedYaml =
                    YamlSectionExtractor.dumpYaml(corrected);

            // 5️⃣ Écrire le fichier corrigé
            Path outputFile = Paths.get(
            		configFile.toString().replace(".yaml", configFile)
            );

            Files.writeString(
                outputFile,
                correctedYaml,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            );
        	
        	
        	
        	
            res = KubevalRunner.validateWithKubeval(
                    Path.of(configFile),
                    Duration.ofSeconds(30)
            		 );
    		
            if (!res.stdout.isBlank() || !res.stderr.isBlank()) {
                return 0;
            }
    			
        	
        	
        } 
        else  return 1; //ya une misconfiguration at least
		
        
        
        
        return score;
		
		
	}
	
}
