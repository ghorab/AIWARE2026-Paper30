package test.test;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	
    	String[] tags = {"kubernetes", "k8s", "pod","kubectl","kube-scheduler","helm","nginx-ingress","kubernetes-secrets","kubernetes-deployment","kubernetes-service","minikube","kind"};
        System.out.println( "Hello World!" );
        
        
        // Affichage avec une boucle for classique
        for (int i = 0; i < tags.length; i++) {
            System.out.println(tags[i]);
        }
    }
}
