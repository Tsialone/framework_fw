package servlets;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import annotations.RequestParam;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.MapUtil;
import utils.ScannerUtil;
import utils.SplitUtil;
import views.ModelView;

import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;

public class FrontServlet extends HttpServlet {
    private ScannerUtil scannerUtil;
    private DefaultFormattingConversionService conversionService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            try {
                String packageToScan = config.getInitParameter("packageToScan");
                conversionService = new DefaultFormattingConversionService();
                DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
                registrar.setUseIsoFormat(true);
                registrar.registerFormatters(conversionService);
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
            } else {
                resp.setContentType("text/plain");

                HashMap<String, List<MapUtil>> controllerMapped = scannerUtil.getMapHash();
                List<String> possiblePath = new ArrayList<>();
                String regex = null;
                possiblePath.add(path);

                // raha ? ilay split
                if (path.contains("\\?")) {
                    List<String> baraingos = SplitUtil.splitByStr(path, "\\?");
                    if (baraingos.size() > 1) {
                        possiblePath.add("/" + baraingos.getFirst());
                        regex = "\\?";
                    }
                }
                // raha / ilay split
                else {
                    List<String> slash = SplitUtil.splitByStr(path, "/");
                    System.out.println("taille an'ilay slash: " + slash.size());
                    if (slash.size() > 1) {
                        possiblePath.add("/" + slash.getFirst());
                        regex = "\\/";
                    }

                }
                List<MapUtil> mapUtils = new ArrayList<>();
                String pathBase = "";
                List<String> controllerUrlSplited = SplitUtil.splitByStr(path, "/");
                if (!controllerUrlSplited.isEmpty())
                    pathBase = "/" + controllerUrlSplited.getFirst();

                boolean urlFound = false;
                boolean methodeMatches = false;

                for (String registeredUrl : controllerMapped.keySet()) {
                    if (SplitUtil.urlMatch(registeredUrl, path)) {
                        System.out.println("Url matched, registeredUrl: " + registeredUrl);
                        mapUtils = controllerMapped.get(registeredUrl);
                        System.out.println(mapUtils);
                        break;
                    }
                }
                // for (String paf : possiblePath) {
                // if (paf.equals(pathBase)) {
                // mapUtils = controllerMapped.get(paf);
                // break;
                // } else {
                // System.out.println("tsy mitovy: " + paf + " vs " + pathBase);
                // }
                // }

                String servletMethode = req.getMethod();
                if (!mapUtils.isEmpty()) {
                    // MapUtil classe = null;
                    System.out.println("MIsy ilay mapUtils");
                    for (MapUtil mapUtil : mapUtils) {
                        List<Object> arguments = new ArrayList<>();
                        if (!mapUtil.getHttpMethode().equals("ALL")
                                && !servletMethode.equals(mapUtil.getHttpMethode())) {
                            System.out
                                    .println("why not matching? " + servletMethode + " vs " + mapUtil.getHttpMethode());
                            continue;

                        } else {
                            methodeMatches = true;
                        }
                        Object controllerInstance = mapUtil.getClasse().getDeclaredConstructor().newInstance();
                        List<Parameter> parameters = SplitUtil.getParameterByMethod(mapUtil.getMethode());
                        for (Parameter param : parameters) {
                            String toFind = param.getName();
                            RequestParam rp = param.getAnnotation(RequestParam.class);
                            if (rp != null && !rp.name().isEmpty()) {
                                toFind = rp.name();
                            }
                            Object requestOb = req.getParameter(toFind);
                            if (regex != null && regex.equals("\\/")) {
                                HashMap<String, Object> pathParamMap = SplitUtil.initKey(path, mapUtil.getUrl(), "\\/");
                                System.out.println("pathParamMap: " + pathParamMap);
                                if (requestOb == null)
                                    requestOb = pathParamMap.get(toFind);
                            }
                            Object ob = conversionService.convert(requestOb, param.getType());
                            arguments.add(ob);
                            System.out.println("param: " + toFind);
                        }
                        Object[] argumentsArray = arguments.toArray(new Object[arguments.size()]);
                        Object result = mapUtil.getMethode().invoke(controllerInstance, argumentsArray);

                        if (result.getClass().equals(ModelView.class)) {
                            ModelView modelView = (ModelView) result;
                            req.setAttribute("modelView", modelView);
                            req.getRequestDispatcher("/WEB-INF/" + modelView.getView()).forward(req, resp);
                        } else if (result.getClass().equals(String.class)) {
                            System.out.println("String ilay retour eeee!");
                            resp.getWriter().println(
                                    "controlleur trouvé: " +
                                            "\nmethode: " + mapUtil.getMethode().getName() +
                                            "\nurl: " + mapUtil.getUrl() +
                                            "\nclasse: " + mapUtil.getClasse().getSimpleName() +
                                            "\nhttMethode: " + mapUtil.getHttpMethode() +
                                            "\nresult invoke: " + result);
                        }
                    }
                    if (!methodeMatches) {
                        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                                "Méthode non autorisée : " + req.getMethod());
                    }
                } else {
                    System.out.println("Tsy hita lty ilay url aaaaaa");
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "URL non trouvée : " + req.getRequestURI());
                    // resp.setContentType("text/plain");
                    // resp.getWriter().println("404 error page not found, x URL est: " +
                    // req.getRequestURI());
                }
            }

        } catch (Exception e) {
            resp.getWriter().println(e.getCause().getMessage());
            e.getCause().printStackTrace();
            // e.printStackTrace();
            // throw e;
        }

    }

}
