package LLM;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class HuggingFaceMainRouter {

    public static void main(String[] args) throws Exception {

        String hfToken = "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";


        String model1 = "mistralai/Mistral-7B-Instruct-v0.2";
        String model2 = "Xenova/gpt-3.5-turbo";
        String model3 = "meta-llama/Llama-3.1-70B-Instruct";
        String model4 = "deepseek-ai/DeepSeek-R1";
        String model5 = "Qwen/Qwen2.5-7B-Instruct";
        
        

      //  String prompt = "Explain Kubernetes misconfigurations in one sentence.";
        
        
        Path promptFile = Path.of(
                "C:\\Users\\Administrator\\Desktop\\rechercheScientifique\\MSR2026\\Replication\\Script and prompts\\test.txt"
        );

        String prompt = Files.readString(promptFile, StandardCharsets.UTF_8).trim();
        
        System.out.println("       prompt   :"+prompt);
        String result = HuggingFaceClientRouter.generate(
                hfToken,
                model1,
                prompt
        );

        System.out.println(result);
    }
}
