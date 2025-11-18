package servlets;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.MapUtil;
import utils.ScannerUtil;
import views.ModelView;

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
                // servlet context
                scannerUtil = new ScannerUtil(packageToScan);
                System.out.println("Package scanné : " + packageToScan);
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
            boolean classeFound = false;
            if (ressourceExist) {
                requestDispatcher.forward(req, resp);
                return;
            } else if (scannerUtil.getMapUtils().size() > 0) {
                resp.setContentType("text/plain");
                resp.getWriter().println("Les contenus de controllerFound :");
                resp.getWriter().println("path : " + path);
                for (MapUtil mapUtil : scannerUtil.getMapUtils()) {
                    if (mapUtil.getUrl().equals(path)) {
                        classeFound = true;
                        Object controllerInstance = mapUtil.getClasse().getDeclaredConstructor().newInstance();
                        Object result = mapUtil.getMethode().invoke(controllerInstance);
                        if (result.getClass().equals(ModelView.class)) {
                            ModelView modelView = (ModelView) result;
                            
                            req.setAttribute("modelView", modelView);
                            req.getRequestDispatcher("/WEB-INF/" + modelView.getView()).forward(req, resp);
                        } else if (result.getClass().equals(String.class)) {
                            resp.getWriter().println(
                                    "controlleur trouvé: " +
                                            "\nmethode: " + mapUtil.getMethode().getName() +
                                            "\nurl: " + mapUtil.getUrl() +
                                            "\nclasse: " + mapUtil.getClasse().getSimpleName() +
                                            "\nresult invoke: " + result);

                        }
                    }
                }

            } if (!classeFound) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "URL non trouvée : " + req.getRequestURI());
                // resp.setContentType("text/plain");
                // resp.getWriter().println("404 error page not found, x URL est: " + req.getRequestURI());
            }
        } catch (

        Exception e) {
            // throw e;
            e.printStackTrace();
        }

    }

}
