package mg.itu.framework.servlet;

import java.lang.reflect.Method;

public class MappedMethod {
    private String url;
    private String method;
    private Class<?> controllerClass;
    private Method controllerMethod;
    
    public MappedMethod(String url, String method, Class<?> controllerClass, Method controllerMethod) {
        this.url = url;
        this.method = method;
        this.controllerClass = controllerClass;
        this.controllerMethod = controllerMethod;
    }
    
    // Getters
    public String getUrl() { return url; }
    public String getMethod() { return method; }
    public Class<?> getControllerClass() { return controllerClass; }
    public Method getControllerMethod() { return controllerMethod; }
    
    @Override
    public String toString() {
        return String.format("URL: %s | Method: %s | Controller: %s | Method: %s", 
                           url, method, controllerClass.getSimpleName(), controllerMethod.getName());
    }
}