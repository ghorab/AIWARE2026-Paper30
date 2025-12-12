package replications;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TOBEDELETD {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub


		String st="You are a Kubernetes security and configuration expert.\r\n"
				+ "\r\n"
				+ "The following Kubernetes configuration contains a known misconfiguration.\r\n"
				+ "\r\n"
				+ "Misconfiguration type:\r\n"
				+ "{{MISCONFIGURATION_TYPE}}\r\n"
				+ "\r\n"
				+ "Your task is to correct the configuration accordingly.\r\n"
				+ "\r\n"
				+ "Rules:\r\n"
				+ "- Output ONLY the corrected Kubernetes YAML.\r\n"
				+ "- Do NOT include explanations, comments, analysis, or reasoning.\r\n"
				+ "- Do NOT wrap the output in code fences.\r\n"
				+ "- Modify only the fields related to the specified misconfiguration.\r\n"
				+ "- Preserve the original structure and semantics.\r\n"
				+ "\r\n"
				+ "Kubernetes configuration:\r\n"
				+ "{{KUBERNETES_YAML}}\r\n"
				+ "";
		
		String s=st.replace("{{KUBERNETES_YAML}}", "ghorab");
		
		s=s.replace("{{MISCONFIGURATION_TYPE}}", "mostafa anouar");
		
		System.out.println(s);
	}
	
	public static String readFileToString(Path filePath) throws IOException {
	    return new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
	}

}
