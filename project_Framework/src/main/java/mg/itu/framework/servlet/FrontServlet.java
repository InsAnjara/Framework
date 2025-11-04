package mg.itu.framework.servlet;

import mg.itu.framework.servlet.annotation.*;
import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.util.*;

public class FrontServlet extends HttpServlet {
    
    private List<MappedMethod> mappedMethods;
    
    @Override
    public void init() throws ServletException {
        super.init();
        System.out.println("[FrontServlet] ===== INITIALISATION SPRINT 2 =====");
        
        // Récupérer les classes spécifiques depuis web.xml
        String controllerClasses = getServletConfig().getInitParameter("controllerClasses");
        
        System.out.println("[FrontServlet] Configuration - controllerClasses: " + controllerClasses);
        
        if (controllerClasses != null && !controllerClasses.trim().isEmpty()) {
            System.out.println("[FrontServlet] Chargement direct des classes...");
            mappedMethods = loadSpecificControllers(controllerClasses);
        } else {
            System.out.println("[FrontServlet] Aucune classe contrôleur configurée");
            mappedMethods = new ArrayList<>();
        }
        
        // Afficher le résultat
        printScanResults();
    }
    
    private List<MappedMethod> loadSpecificControllers(String controllerClasses) {
        List<MappedMethod> result = new ArrayList<>();
        
        String[] classNames = controllerClasses.split(",");
        for (String className : classNames) {
            className = className.trim();
            if (!className.isEmpty()) {
                try {
                    System.out.println("[FrontServlet] Chargement de la classe: " + className);
                    Class<?> clazz = Class.forName(className);
                    processClass(clazz, result);
                } catch (ClassNotFoundException e) {
                    System.err.println("[FrontServlet] Classe non trouvée: " + className);
                } catch (Exception e) {
                    System.err.println("[FrontServlet] Erreur chargement " + className + ": " + e.getMessage());
                }
            }
        }
        
        return result;
    }
    
    private void processClass(Class<?> clazz, List<MappedMethod> mappedMethods) {
        try {
            System.out.println("[FrontServlet] Traitement de: " + clazz.getName());
            
            if (clazz.isAnnotationPresent(Controller.class)) {
                System.out.println("[FrontServlet] ✓ Contrôleur trouvé: " + clazz.getSimpleName());
                
                Controller controllerAnnotation = clazz.getAnnotation(Controller.class);
                String controllerUrl = controllerAnnotation.value();
                
                // Scanner les méthodes
                java.lang.reflect.Method[] methods = clazz.getDeclaredMethods();
                for (java.lang.reflect.Method method : methods) {
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
                        System.out.println("[FrontServlet] ✓ Mapping: " + fullUrl + " -> " + 
                                          clazz.getSimpleName() + "." + method.getName());
                    }
                }
            } else {
                System.out.println("[FrontServlet] ✗ Classe sans @Controller: " + clazz.getSimpleName());
            }
        } catch (Exception e) {
            System.err.println("[FrontServlet] Erreur traitement " + clazz.getName() + ": " + e.getMessage());
        }
    }
    
    private void printScanResults() {
        System.out.println("[FrontServlet] ===== RÉSULTATS SPRINT 2 =====");
        if (mappedMethods.isEmpty()) {
            System.out.println("[FrontServlet] AUCUN CONTRÔLEUR TROUVÉ!");
        } else {
            System.out.println("[FrontServlet] " + mappedMethods.size() + " MAPPAGES TROUVÉS:");
            for (MappedMethod method : mappedMethods) {
                System.out.println("[FrontServlet] → " + method);
            }
        }
        System.out.println("[FrontServlet] =====================");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String url = request.getRequestURI();
        String contextPath = request.getContextPath();
        String resourcePath = url.substring(contextPath.length());
        
        System.out.println("[FrontServlet] Requête: " + url);
        
        // Pour le Sprint 2, on affiche juste les informations
        displayFrameworkInfo(response, url, resourcePath);
    }
    
    private void displayFrameworkInfo(HttpServletResponse response, String url, String resourcePath) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Framework MVC - Sprint 2</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 40px; line-height: 1.6; }");
        out.println(".header { background: #2c3e50; color: white; padding: 20px; border-radius: 5px; }");
        out.println(".url { background: #ecf0f1; padding: 15px; border-radius: 5px; margin: 20px 0; }");
        out.println(".controller { background: #d5edf7; padding: 15px; margin: 10px 0; border-left: 4px solid #3498db; }");
        out.println(".method { background: #d4efdf; padding: 10px; margin: 5px 0; border-left: 4px solid #27ae60; }");
        out.println(".warning { background: #fdebd0; padding: 15px; border: 1px solid #f39c12; border-radius: 5px; }");
        out.println(".success { background: #d1f2eb; padding: 15px; border: 1px solid #27ae60; border-radius: 5px; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        
        out.println("<div class=\"header\">");
        out.println("<h1>🚀 Framework MVC - Sprint 2</h1>");
        out.println("<p>Approche simplifiée - Chargement direct des classes</p>");
        out.println("</div>");
        
        out.println("<h2>📋 Informations de la requête</h2>");
        out.println("<div class=\"url\">");
        out.println("<strong>URL complète:</strong> " + url + "<br>");
        out.println("<strong>Chemin:</strong> " + resourcePath);
        out.println("</div>");
        
        out.println("<h2>🎯 Contrôleurs chargés</h2>");
        if (mappedMethods.isEmpty()) {
            out.println("<div class=\"warning\">");
            out.println("<h3>❌ Aucun contrôleur trouvé</h3>");
            out.println("<p>Vérifiez votre configuration :</p>");
            out.println("<ul>");
            out.println("<li>Les classes sont-elles dans le classpath?</li>");
            out.println("<li>Les noms dans web.xml sont-ils corrects?</li>");
            out.println("<li>Les annotations @Controller et @URLMapping sont-elles présentes?</li>");
            out.println("</ul>");
            out.println("</div>");
        } else {
            out.println("<div class=\"success\">");
            out.println("<h3>✅ " + mappedMethods.size() + " mapping(s) trouvé(s)</h3>");
            out.println("</div>");
            
            // Grouper par contrôleur
            Map<Class<?>, List<MappedMethod>> byController = new HashMap<>();
            for (MappedMethod method : mappedMethods) {
                byController.computeIfAbsent(method.getControllerClass(), k -> new ArrayList<>()).add(method);
            }
            
            for (Map.Entry<Class<?>, List<MappedMethod>> entry : byController.entrySet()) {
                out.println("<div class=\"controller\">");
                out.println("<h3>🎮 " + entry.getKey().getSimpleName() + "</h3>");
                out.println("<p><strong>Classe:</strong> " + entry.getKey().getName() + "</p>");
                
                for (MappedMethod method : entry.getValue()) {
                    out.println("<div class=\"method\">");
                    out.println("<strong>URL:</strong> " + method.getUrl() + "<br>");
                    out.println("<strong>Méthode HTTP:</strong> " + method.getMethod() + "<br>");
                    out.println("<strong>Méthode Java:</strong> " + method.getControllerMethod().getName());
                    out.println("</div>");
                }
                out.println("</div>");
            }
        }
        
        out.println("</body>");
        out.println("</html>");
    }
}