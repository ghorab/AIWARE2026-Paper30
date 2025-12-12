package replications;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class RemplirTemplete{
	
	
	public static String readFileToString(Path filePath) throws IOException {
	    return new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
	}
	
	public static String Buildtemplete(Path config) throws IOException {
		Path templete = Path.of("C:\\Users\\Administrator\\Desktop\\rechercheScientifique\\MSR2026\\Replication\\Script and prompts\\TemPrompt01.txt");
		String content = readFileToString(templete);
		System.out.println(content);
		
		String F=readFileToString(config);
		
		String prompt=F.replace("{{KUBERNETES_YAML}}", F);
		
	    return "";
	}
	
	public static String Buildtemplete(Path config,String Type) throws IOException {
		
		Path templete = Path.of("C:\\Users\\Administrator\\Desktop\\rechercheScientifique\\MSR2026\\Replication\\Script and prompts\\TemPrompt02.txt");
		String content = readFileToString(templete);
		System.out.println(content);
		
		String F=readFileToString(config);
		
		String prompt=F.replace("{{KUBERNETES_YAML}}", F);
		
		prompt=prompt.replace("{{MISCONFIGURATION_TYPE}}", Type);
		
		
		return prompt;
	}
	
	
	public static String Buildtemplete(Path config,String Type,String Description) throws IOException {
		
		Path templete = Path.of("C:\\Users\\Administrator\\Desktop\\rechercheScientifique\\MSR2026\\Replication\\Script and prompts\\TemPrompt03.txt");
		String content = readFileToString(templete);
		System.out.println(content);
		
		
		String F=readFileToString(config);
		
		String prompt=F.replace("{{KUBERNETES_YAML}}", F);
		
		prompt=prompt.replace("{{MISCONFIGURATION_TYPE}}", Type);
		
		prompt=prompt.replace("{{MISCONFIGURATION_DESCRIPTION}}", Description);
		
		
		return prompt;
	}
	


}