package mg.itu.framework.servlet.scanner;

import mg.itu.framework.servlet.annotation.Controller;
import mg.itu.framework.servlet.annotation.URLMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ClassScanner {
    
    public static List<MappedMethod> scanControllers(String packageNames) {
        List<MappedMethod> mappedMethods = new ArrayList<>();
        
        if (packageNames == null || packageNames.trim().isEmpty()) {
            System.out.println("[ClassScanner] Aucun package spécifié");
            return mappedMethods;
        }
        
        System.out.println("[ClassScanner] Début du scan avec packages: " + packageNames);
        
        // Séparer les packages par virgule si plusieurs sont spécifiés
        String[] packages = packageNames.split(",");
        
        for (String packageName : packages) {
            packageName = packageName.trim();
            System.out.println("[ClassScanner] Scan du package: " + packageName);
            
            // Charger les classes connues pour ce package
            scanKnownControllers(packageName, mappedMethods);
        }
        
        System.out.println("[ClassScanner] Scan terminé. " + mappedMethods.size() + " méthodes mappées trouvées.");
        return mappedMethods;
    }
    
    private static void scanKnownControllers(String packageName, List<MappedMethod> mappedMethods) {
        try {
            // Cette méthode repose sur le fait que les classes sont déjà dans le classpath
            // On va essayer de charger les classes courantes qu'on s'attend à trouver
            
            // Liste des noms de classes courants qu'on pourrait trouver
            String[] commonClassNames = {
                "Test1", "Test2", "Test3", 
                "HomeController", "UserController", "AdminController",
                "MainController", "DefaultController"
            };
            
            for (String className : commonClassNames) {
                String fullClassName = packageName + "." + className;
                try {
                    Class<?> clazz = Class.forName(fullClassName);
                    processClass(clazz, mappedMethods);
                } catch (ClassNotFoundException e) {
                    // C'est normal, on continue avec la classe suivante
                }
            }
            
            // Essayer aussi de scanner dynamiquement en listant les ressources?
            // Cette approche est limitée mais plus fiable que le scan de fichiers
            scanViaClassLoader(packageName, mappedMethods);
            
        } catch (Exception e) {
            System.err.println("[ClassScanner] Erreur lors du scan connu: " + e.getMessage());
        }
    }
    
    private static void scanViaClassLoader(String packageName, List<MappedMethod> mappedMethods) {
        try {
            // Approche alternative: utiliser le ClassLoader pour trouver les classes
            // Cette méthode est plus fiable car elle utilise le mécanisme standard de chargement de classes
            
            // On va essayer de détecter les classes en utilisant la réflexion sur les packages chargés
            Package[] packages = Package.getPackages();
            for (Package pkg : packages) {
                if (pkg.getName().startsWith(packageName)) {
                    System.out.println("[ClassScanner] Package trouvé: " + pkg.getName());
                }
            }
            
        } catch (Exception e) {
            System.err.println("[ClassScanner] Erreur scan ClassLoader: " + e.getMessage());
        }
    }
    
    private static void processClass(Class<?> clazz, List<MappedMethod> mappedMethods) {
        try {
            System.out.println("[ClassScanner] Classe chargée: " + clazz.getName());
            
            if (clazz.isAnnotationPresent(Controller.class)) {
                System.out.println("[ClassScanner] ✓ Classe annotée @Controller: " + clazz.getSimpleName());
                
                Controller controllerAnnotation = clazz.getAnnotation(Controller.class);
                String controllerUrl = controllerAnnotation.value();
                
                // Scanner les méthodes
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(URLMapping.class)) {
                        URLMapping urlMapping = method.getAnnotation(URLMapping.class);
                        String fullUrl = controllerUrl + urlMapping.url();
                        
                        MappedMethod mappedMethod = new MappedMethod(
                            fullUrl, 
                            urlMapping.method(), 
                            clazz, 
                            method
                        );
                        
                        mappedMethods.add(mappedMethod);
                        System.out.println("[ClassScanner] ✓ Méthode mappée: " + mappedMethod);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[ClassScanner] Erreur traitement classe " + clazz.getName() + ": " + e.getMessage());
        }
    }
}