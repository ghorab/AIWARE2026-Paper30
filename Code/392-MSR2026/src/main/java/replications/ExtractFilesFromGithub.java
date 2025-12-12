package replications;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import LLM.promptExecuteEvaluate;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ExtractFilesFromGithub {

    public static void main(String[] args) throws Exception {
    	
        int scoreM1P1=0;
        int scoreM2P1=0;
        int scoreM3P1=0;
        int scoreM4P1=0;
        int scoreM5P1=0;
        
        int scoreM1P2=0;
        int scoreM2P2=0;
        int scoreM3P2=0;
        int scoreM4P2=0;
        int scoreM5P2=0;
        
        
        
        int scoreM1P3=0;
        int scoreM2P3=0;
        int scoreM3P3=0;
        int scoreM4P3=0;
        int scoreM5P3=0; 
    	
    	/*I put in this file all the configuration files collected directly from GitHub; all of them have already been validated using kube-eval.
    	 *  so no need to recollect the files we will use this dataset offile directly */
    	
        Path csvPath = Path.of("C:\\Users\\Administrator\\Desktop\\rechercheScientifique\\MSR2026\\Replication\\DATA\\RQ4\\Dataset_RQ4.csv");
        
    
       String path="C:\\Users\\Administrator\\Desktop\\rechercheScientifique\\MSR2026\\Replication\\DATA\\RQ4\\yamls\\";
      

        try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                // Skip header
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                // Trim to be safe
                line = line.trim();

                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }

                
                // Process filename
                System.out.println(line);
                
                // scan using the three tools
                String configFile ="path"+line+"yaml";
                List<String> ids = K8sMultiScanner.scanRuleIdsOnly(Path.of(configFile));
                System.out.println(ids);
                
                
                for (String id : ids) {
                    System.out.println("Misconfiguration détectée : " + id);
                    
                    Path MapperSNYK = Path.of("C:\\Users\\Administrator\\Desktop\\rechercheScientifique\\MSR2026\\Replication\\Mapping table\\SnykMapper.txt"); 
                    Path MapperDatree = Path.of("C:\\Users\\Administrator\\Desktop\\rechercheScientifique\\MSR2026\\Replication\\Mapping table\\SnykMapper.txt"); 
                    Path MapperKubescore = Path.of("C:\\Users\\Administrator\\Desktop\\rechercheScientifique\\MSR2026\\Replication\\Mapping table\\SnykMapper.txt"); 
                   
                    String formA = id;
                    
                    // Mapping betewen tools label and taxonomy type :
                    
                    String FormB = MisconfigMapping.getFormB(MapperSNYK, formA);   // null si la clé n'existe pas
                    if (FormB==null) {MisconfigMapping.getFormB(MapperDatree, FormB);}   // null si la clé n'existe pas
                    else {MisconfigMapping.getFormB(MapperKubescore, FormB);}
                    
                    System.out.println(FormB);   
                    
                    
                    
                    String description = MisconfigurationDescriptionLoader.getDescription("C:\\Users\\Administrator\\Desktop\\rechercheScientifique\\MSR2026\\Replication\\Mapping table\\Description.json", id);
                    
                    String prompt1=RemplirTemplete.Buildtemplete(Paths.get(configFile));
                    String prompt2=RemplirTemplete.Buildtemplete(Paths.get(configFile),FormB);
                    String prompt3=RemplirTemplete.Buildtemplete(Paths.get(configFile),FormB,description);
                    
                    
                    String model1 = "mistralai/Mistral-7B-Instruct-v0.2";
                    String model2 = "Xenova/gpt-3.5-turbo";
                    String model3 = "meta-llama/Llama-3.1-70B-Instruct";
                    String model4 = "deepseek-ai/DeepSeek-R1";
                    String model5 = "Qwen/Qwen2.5-7B-Instruct";
                    
                    scoreM1P1=scoreM1P1+promptExecuteEvaluate.promptANDevaluat(prompt1,model1);
                    scoreM2P1=scoreM2P1+promptExecuteEvaluate.promptANDevaluat(prompt1,model2);
                    scoreM3P1=scoreM3P1+promptExecuteEvaluate.promptANDevaluat(prompt1,model3);
                    scoreM4P1=scoreM4P1+promptExecuteEvaluate.promptANDevaluat(prompt1,model4);
                    scoreM5P1=scoreM5P1+promptExecuteEvaluate.promptANDevaluat(prompt1,model5);
                    
                    scoreM1P2=scoreM1P2+promptExecuteEvaluate.promptANDevaluat(prompt2,model1);
                    scoreM2P2=scoreM2P2+promptExecuteEvaluate.promptANDevaluat(prompt2,model2);
                    scoreM3P2=scoreM3P2+promptExecuteEvaluate.promptANDevaluat(prompt2,model3);
                    scoreM4P2=scoreM4P2+promptExecuteEvaluate.promptANDevaluat(prompt2,model4);
                    scoreM5P2=scoreM5P2+promptExecuteEvaluate.promptANDevaluat(prompt2,model5);
                              
                    scoreM1P3=scoreM1P3+promptExecuteEvaluate.promptANDevaluat(prompt3,model1);
                    scoreM2P3=scoreM2P3+promptExecuteEvaluate.promptANDevaluat(prompt3,model2);
                    scoreM3P3=scoreM3P3+promptExecuteEvaluate.promptANDevaluat(prompt3,model3);
                    scoreM4P3=scoreM4P3+promptExecuteEvaluate.promptANDevaluat(prompt3,model4);
                    scoreM5P3=scoreM5P3+promptExecuteEvaluate.promptANDevaluat(prompt3,model5);  
                    
                    
                    
                    System.out.println("#################################################################################");
                    System.out.println("################################# Results :   ##################################");
                    System.out.println("                Model 1, Exp 1 : "+scoreM1P1+" corrected from 10k configurations");
                    System.out.println("                Model 2, Exp 1 : "+scoreM2P1+" corrected from 10k configurations");
                    System.out.println("                Model 3, Exp 1 : "+scoreM3P1+" corrected from 10k configurations");
                    System.out.println("                Model 4, Exp 1 : "+scoreM4P1+" corrected from 10k configurations");
                    System.out.println("                Model 5, Exp 1 : "+scoreM5P1+" corrected from 10k configurations");
                    System.out.println("                Model 1, Exp 2 : "+scoreM1P2+" corrected from 10k configurations");
                    System.out.println("                Model 2, Exp 2 : "+scoreM2P2+" corrected from 10k configurations");
                    System.out.println("                Model 3, Exp 2 : "+scoreM3P2+" corrected from 10k configurations");
                    System.out.println("                Model 4, Exp 2 : "+scoreM4P2+" corrected from 10k configurations");
                    System.out.println("                Model 5, Exp 2 : "+scoreM5P2+" corrected from 10k configurations");
                    System.out.println("                Model 1, Exp 3 : "+scoreM1P3+" corrected from 10k configurations");
                    System.out.println("                Model 2, Exp 3 : "+scoreM2P3+" corrected from 10k configurations");
                    System.out.println("                Model 3, Exp 3 : "+scoreM3P3+" corrected from 10k configurations");
                    System.out.println("                Model 4, Exp 3 : "+scoreM4P3+" corrected from 10k configurations");
                    System.out.println("                Model 5, Exp 3 : "+scoreM5P3+" corrected from 10k configurations");
                    System.out.println("#################################################################################");
                    System.out.println("#################################################################################");
                    
                    
                    
                    
                }
                
               
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
