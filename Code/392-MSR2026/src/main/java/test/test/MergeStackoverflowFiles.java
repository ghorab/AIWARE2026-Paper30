package test.test;



import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MergeStackoverflowFiles {

    public static void main(String[] args) {

        // TES CHEMINS
        String DOSSIER_ENTREE  = "C:\\Users\\Administrator\\Desktop\\rechercheScientifique\\MSR2026\\collect\\All";
        String FICHIER_SORTIE  = "C:\\Users\\Administrator\\Desktop\\rechercheScientifique\\MSR2026\\collect\\All\\titres_liens.txt";

        Path inputDir = Paths.get(DOSSIER_ENTREE);
        Path outputFile = Paths.get(FICHIER_SORTIE);

        System.out.println("DOSSIER_ENTREE = " + inputDir.toAbsolutePath());
        System.out.println("FICHIER_SORTIE = " + outputFile.toAbsolutePath());
        System.out.println("isDirectory?    = " + Files.isDirectory(inputDir));

        if (!Files.isDirectory(inputDir)) {
            System.err.println("❌ Le dossier n'existe pas ou n'est pas un dossier : " + inputDir);
            return;
        }

        // Regex robustes
        Pattern titrePattern = Pattern.compile("^\\s*Titre\\s*:\\s*(.*)$");
        Pattern lienPattern  = Pattern.compile("^\\s*Lien\\s*:\\s*(.*)$");

        Map<String, String> linkToTitle = new LinkedHashMap<>();
        int nbFichiers = 0;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(inputDir)) {
            for (Path file : stream) {
                if (Files.isDirectory(file)) {
                    continue;
                }
                // On ignore le fichier de sortie si on relance
                if (file.getFileName().toString().equals("titres_liens.txt")) {
                    continue;
                }

                nbFichiers++;
                System.out.println("📄 Traitement du fichier : " + file.getFileName());

                List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
                String currentTitle = null;

                for (String rawLine : lines) {
                    String line = rawLine; // sans trim, la regex gère les espaces

                    Matcher mt = titrePattern.matcher(line);
                    Matcher ml = lienPattern.matcher(line);

                    if (mt.matches()) {
                        currentTitle = mt.group(1).trim();
                        System.out.println("  → Titre trouvé : " + currentTitle);
                    } else if (ml.matches()) {
                        String link = ml.group(1).trim();
                        if (!link.isEmpty()) {
                            linkToTitle.putIfAbsent(link, currentTitle == null ? "" : currentTitle);
                            System.out.println("  → Lien trouvé  : " + link);
                        }
                        currentTitle = null;
                    }
                }
            }

            System.out.println("📂 Nombre de fichiers lus : " + nbFichiers);
            System.out.println("✅ Nombre de questions uniques trouvées : " + linkToTitle.size());

            if (linkToTitle.isEmpty()) {
                System.out.println("⚠️ Aucune paire Titre/Lien trouvée. Vérifie le format exact des lignes.");
            }

            // Écriture du résultat
            try (BufferedWriter writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8)) {
                for (Map.Entry<String, String> entry : linkToTitle.entrySet()) {
                    String title = entry.getValue();
                    String link = entry.getKey();
                    writer.write(title + " ; " + link);
                    writer.newLine();
                }
            }

            System.out.println("📝 Fichier généré : " + outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
