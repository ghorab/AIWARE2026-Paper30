package test.test;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;

public class DownloadJSON {
    public static void main(String[] args) {
 /*   	
    	String[] Keywords = {
    			"kubernetes",
                "k8s",
                "pod",
                "kubelet",
                "kubeadm",
                "kubectl",
                "kube-scheduler",
                "kube-controller-manager",
                "deployment",
                "service",
                "ingress",
                "nginx-ingress",
                "helm",
                "helm-chart",
                "minikube",
                "kind",
                "containerd",
                "istio",
    		};
    	*/
    	/*
    	
    	String[] Keywords = {
    		    "kubelet",
    		    "kubeadm",
    		    "kube-proxy",
    		    "etcd",
    		    "containerd",
    		    "cri-o",
    		    "cni",
    		    "calico",
    		    "flannel",
    		    "weave",
    		    "istio",
    		    "linkerd",
    		    "envoyproxy",
    		    "prometheus",
    		    "grafana",
    		    "argo-cd",
    		    "argo-workflows",
    		    "kustomize",
    		    "helmfile",
    		    "operator-framework",
    		    "kubernetes-operator",
    		    "olm",
    		    "rbac",
    		    "networkpolicy",
    		    "serviceaccount",
    		    "nodeport",
    		    "loadbalancer",
    		    "persistent-volume",
    		    "persistentvolumeclaims",
    		    "storageclass",
    		    "hpa",          // Horizontal Pod Autoscaler
    		    "livenessprobe",
    		    "readinessprobe",
    		    "startup-probe",
    		    "ingress-controller",
    		    "traefik",
    		    "nginx",
    		    "metrics-server",
    		    "gateway-api",
    		    "keda",         // Event-driven autoscaling
    		    "vault-kubernetes",
    		    "azure-aks",
    		    "aws-eks",
    		    "google-kubernetes-engine" // GKE
    		};
    	
    	*/
    	
    	
    	/*
    	
    	
    	String[] Keywords = {
    		    "cluster",
    		    "namespace",
    		    "node",
    		    "master",
    		    "control+plane",
    		    "api+server",
    		    "scheduler",
    		    "controller",
    		    "deployment+rollout",
    		    "rolling+update",
    		    "autoscaling",
    		    "scaling",
    		    "statefulset",
    		    "daemonset",
    		    "replicaset",
    		    "resources+limits",
    		    "cpu+request",
    		    "memory+request",
    		    "sidecar",
    		    "init+container",
    		    "image+pull",
    		    "pull+policy",
    		    "pod+eviction",
    		    "pod+disruption+budget",
    		    "taints",
    		    "tolerations",
    		    "affinity",
    		    "anti-affinity",
    		    "service+mesh",
    		    "load+balancing",
    		    "service+discovery",
    		    "api+gateway",
    		    "custom+resource",
    		    "crd",
    		    "node+affinity",
    		    "pod+scheduling",
    		    "log+aggregation",
    		    "distributed+tracing",
    		    "otel",
    		    "open+telemetry",
    		    "container+logs",
    		    "cluster+provisioning",
    		    "bootstrap+token",
    		    "cloud+controller manager",
    		    "runtime+class",
    		    "pod+security+policy",
    		    "seccomp",
    		    "pod+sandbox",
    		    "multi-container+pod",
    		    "headless+service",
    		    "service+routing",
    		    "cluster+autoscaler",
    		    "node+autoscaler",
    		    "horizontal+autoscaler",
    		    "vertical+autoscaler",
    		    "metrics+scraping",
    		    "audit+logs"
    		};
    	
    	
    	*/
    	
    	
    	
    	/*
    	String[] Keywords = {
    		    // Accès & RBAC
    		    "rbac","role","rolebinding","clusterrole","clusterrolebinding","serviceaccount",
    		    "service-account-token","identity-access-management","kubernetes-authentication",
    		    "kubernetes-authorization",

    		    // Secrets & Encryption
    		    "kubernetes-secrets","secret-management","encrypted-secrets","etcd-encryption",
    		    "encryption-at-rest","encryption-in-transit","vault-kubernetes","sealed-secrets","sops",

    		    // Hardening
    		    "security-context","pod-security-standards","pod-security-policy","seccomp","apparmor",
    		    "selinux","capabilities","capabilities-drop","privileged-containers","rootless-containers",
    		    "read-only-root-filesystem","run-as-non-root",

    		    // Network security
    		    "networkpolicy","calico-policy","cilium-network-policy","istio-security","envoy-security",
    		    "mtls","zero-trust","ingress-security",

    		    // Runtime security
    		    "falco","runtime-security","sysdig-secure","aqua-security","twistlock","kube-hunter",
    		    "kube-bench","cis-benchmark","container-vulnerability",

    		    // Policy enforcement
    		    "opa-gatekeeper","open-policy-agent","kyverno","admission-controller",
    		    "dynamic-admission-control","policy-enforcement"
    		};
    	
    	*/
    	
    	
    	String[] Keywords = {
    		    // API Server protections
    		    "api-server-authentication",
    		    "api-server-authorize",
    		    "api-server-audit",
    		    "api-server-hardened",

    		    // TLS & certificates
    		    "tls-kubernetes",
    		    "certificate-rotation",
    		    "cert-manager-security",
    		    "x509-kubernetes",
    		    "cluster-certificate-management",

    		    // Network stack security
    		    "cilium-security",
    		    "istio-authentication",
    		    "istio-authorization",
    		    "istio-certificate-rotation",
    		    "egress-gateway-security",

    		    // Node + Runtime enforcement
    		    "node-restrictions",
    		    "node-attestation",
    		    "workload-attestation",
    		    "runtime-protection",
    		    "container-sandboxing",

    		    // API object security
    		    "configmap-security",
    		    "namespace-isolation",
    		    "multi-tenancy-security",
    		    "tenant-isolation",

    		    // CI/CD + deployment security
    		    "secure-deployment",
    		    "kubernetes-pipeline-security",
    		    "secure-artifact-registry",
    		    "kubernetes-cicd-security",

    		    // Logging & alerting
    		    "security-alerting",
    		    "log-forwarding-security",
    		    "siem-kubernetes",
    		    "forensic-logs",

    		    // Backup & disaster recovery (security angle)
    		    "backup-encryption",
    		    "kubernetes-dr-security",
    		    "cluster-restore-security",

    		    // Cloud provider security contexts
    		    "eks-security",
    		    "gke-security",
    		    "aks-security",
    		    "cloud-iam-kubernetes",
    		    "workload-identity-security",

    		    // Edge and hybrid Kubernetes security
    		    "edge-kubernetes-security",
    		    "fleet-security",
    		    "remote-cluster-security",

    		    // Advanced access restrictions
    		    "api-whitelisting",
    		    "ip-whitelisting-kubernetes",
    		    "firewall-kubernetes",
    		    "endpoint-protection",

    		    // Anti-tampering / integrity
    		    "integrity-monitoring",
    		    "checksum-verification-k8s",
    		    "tamper-detection",
    		    "immutable-infrastructure-security"
    		};

    	
    	
    	for (int i = 0; i < Keywords.length; i++) {
    		
        String urlString ="https://api.stackexchange.com/2.3/search/advanced?q="+Keywords[i]+"&site=stackoverflow";
        String outputFile = "C:\\Users\\Administrator\\Desktop\\rechercheScientifique\\MSR2026\\collect\\auto\\Key5\\Keywords_"+Keywords[i]+".json";
        

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Vérifier la réponse HTTP
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                // Télécharger les données
                BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
                FileOutputStream fileOutputStream = new FileOutputStream(outputFile);

                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer, 0, 1024)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }

                inputStream.close();
                fileOutputStream.close();
                connection.disconnect();

                System.out.println("Téléchargement réussi ! Fichier sauvegardé sous : " + outputFile);
            } else {
                System.out.println("Erreur HTTP : " + responseCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
    	}   
        
        
    }
}

