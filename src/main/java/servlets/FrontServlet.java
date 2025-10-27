package servlets;

import java.io.IOException;
import java.net.URL;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontServlet extends HttpServlet {


    

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        ServletContext servletContext = req.getServletContext();
        RequestDispatcher requestDispatcher = servletContext.getNamedDispatcher("default");

        try {
            boolean ressourceExist = servletContext.getResource(path) != null;
            if (ressourceExist ) {
                requestDispatcher.forward(req, resp);
            } else {
                resp.setContentType("text/plain");
                resp.getWriter().println("Hello, ton URL est: " + req.getRequestURI());
            }
        } catch (Exception e) {
            throw e;

        }

    }

}
