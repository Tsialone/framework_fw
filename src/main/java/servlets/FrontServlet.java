package servlets;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import annotations.ControllerAnnotation;
import annotations.UrlAnnotation;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.MapUtil;
import utils.ScannerUtil;

public class FrontServlet extends HttpServlet {
    private ScannerUtil scannerUtil;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            try {
                String packageToScan = config.getInitParameter("packageToScan");
                // if (packageToScan == null || packageToScan.isEmpty()) {
                // packageToScan = "apps";
                // }
                scannerUtil = new ScannerUtil(packageToScan);
                System.out.println("📦 Package scanné : " + packageToScan);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        ServletContext servletContext = req.getServletContext();
        RequestDispatcher requestDispatcher = servletContext.getNamedDispatcher("default");
        try {
            boolean ressourceExist = servletContext.getResource(path) != null;
            if (ressourceExist) {
                requestDispatcher.forward(req, resp);
            } else if (scannerUtil.getMapUtils().size() > 0) {
                resp.setContentType("text/plain");
                resp.getWriter().println("Les contenus de controllerFound :");
                resp.getWriter().println("path : " + path);
                for (MapUtil mapUtil : scannerUtil.getMapUtils()) {
                    if (mapUtil.getUrl().value().equals(path)) {
                        Object controllerInstance = mapUtil.getClasse().getDeclaredConstructor().newInstance();
                        Object result = mapUtil.getMethode().invoke(controllerInstance);
                        resp.getWriter().println(
                                "controlleur trouvé: " +
                                        "\nmethode: " + mapUtil.getMethode().getName() +
                                        "\nurl: " + mapUtil.getUrl().value() +
                                        "\nclasse: " + mapUtil.getClasse().getSimpleName() + 
                                        "\nresult invoke: "  +  result
                                        );

                    }
                }

            } else {
                resp.setContentType("text/plain");
                resp.getWriter().println("404 error page not found, x URL est: " + req.getRequestURI());
            }
        } catch (

        Exception e) {
            // throw e;
            e.printStackTrace();
        }

    }

}
