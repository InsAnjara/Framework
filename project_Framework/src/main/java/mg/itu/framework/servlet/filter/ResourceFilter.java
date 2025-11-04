package mg.itu.framework.servlet.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class ResourceFilter implements Filter {
    
    private ServletContext context;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.context = filterConfig.getServletContext();
        System.out.println("[ResourceFilter] Filtre initialisé");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String url = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        
        // Extraire le chemin après le contextPath
        String resourcePath = url.substring(contextPath.length());
        
        System.out.println("[ResourceFilter] URL interceptée: " + url);
        System.out.println("[ResourceFilter] Chemin de la ressource: " + resourcePath);
        
        // Vérifier si c'est une ressource statique qui existe
        if (isStaticResource(resourcePath) && resourceExists(resourcePath)) {
            System.out.println("[ResourceFilter] Ressource statique trouvée: " + resourcePath);
            // Servir la ressource statique directement depuis le filtre
            serveStaticResource(resourcePath, httpResponse);
        } else {
            System.out.println("[ResourceFilter] Ressource non trouvée ou non statique: " + resourcePath);
            // Continuer vers FrontServlet
            chain.doFilter(request, response);
        }
    }
    
    private boolean isStaticResource(String resourcePath) {
        if (resourcePath == null || resourcePath.isEmpty() || "/".equals(resourcePath)) {
            return false;
        }
        
        // Vérifier les extensions de fichiers statiques (exclure .jsp pour éviter de servir le code source)
        return resourcePath.endsWith(".html") ||
               resourcePath.endsWith(".css") ||
               resourcePath.endsWith(".js") ||
               resourcePath.endsWith(".png") ||
               resourcePath.endsWith(".jpg") ||
               resourcePath.endsWith(".gif") ||
               resourcePath.endsWith(".ico") ||
               resourcePath.endsWith(".");
    }
    
    private boolean resourceExists(String resourcePath) {
        try {
            // Vérifier si la ressource existe dans le contexte web
            return context.getResource(resourcePath) != null;
        } catch (Exception e) {
            System.out.println("[ResourceFilter] Erreur lors de la vérification de la ressource: " + e.getMessage());
            return false;
        }
    }
    
    private void serveStaticResource(String resourcePath, HttpServletResponse response) throws IOException {
        String mimeType = context.getMimeType(resourcePath);
        if (mimeType != null) {
            response.setContentType(mimeType);
        }
        
        try (InputStream is = context.getResourceAsStream(resourcePath);
             OutputStream os = response.getOutputStream()) {
            
            if (is == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            byte[] buffer = new byte[4096];  // Tampon plus grand pour l'efficacité
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            System.out.println("[ResourceFilter] Erreur lors du service de la ressource: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    @Override
    public void destroy() {
        System.out.println("[ResourceFilter] Filtre détruit");
    }
}