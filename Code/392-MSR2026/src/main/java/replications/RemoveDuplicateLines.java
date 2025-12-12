package replications;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class RemoveDuplicateLines {

    public static void main(String[] args) {

        // Chemins à modifier selon ton besoin
        String inputFile = "C:\\Users\\Administrator\\Desktop\\rechercheScientifique\\MSR2026\\datasetstackoverflow\\ALL.txt";
        String outputFile = "C:\\Users\\Administrator\\Desktop\\rechercheScientifique\\MSR2026\\datasetstackoverflow\\ALLrES.txt";

        try {
            removeDuplicateLines(inputFile, outputFile);
            System.out.println("Fichier nettoyé avec succès !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeDuplicateLines(String inputPath, String outputPath) throws IOException {

        // Lire toutes les lignes
        List<String> lines = Files.readAllLines(Paths.get(inputPath));

        // LinkedHashSet -> enlève doublons + garde l'ordre
        Set<String> uniqueLines = new LinkedHashSet<>(lines);

        // Réécrire dans un nouveau fichier
        Files.write(Paths.get(outputPath), uniqueLines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
