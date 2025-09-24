package mg.itu.framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException {
        service(req, res);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException {
        service(req, res);
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("text/plain");
        res.getWriter().println("URL demandée: " + req.getRequestURL().toString());
        System.out.println("URL demandée: " + req.getRequestURL().toString());
    }
}