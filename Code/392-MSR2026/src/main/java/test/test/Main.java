package test.test;

import replications.MisconfigurationDescriptionLoader;

public class Main {
    public static void main(String[] args) throws Exception {

        String filePath = "C:\\Users\\Administrator\\Desktop\\rechercheScientifique\\MSR2026\\Replication\\MisconfDescription.txt";
        String misconfig = "No PodDisruptionBudget (PDB) for critical workloads";

        String description =
                MisconfigurationDescriptionLoader.getDescription(filePath, misconfig);

        if (description != null) {
            System.out.println("Description: " + description);
        } else {
            System.out.println("Misconfiguration not found");
        }
    }
}
