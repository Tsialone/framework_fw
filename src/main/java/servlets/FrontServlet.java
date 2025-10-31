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

import annotations.Controller;
import annotations.Url;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontServlet extends HttpServlet {
    private Map<String, Map<String, String>> controllerFound = new HashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            System.out.println("=== Initialisation FrontServlet ===");
            String classPath = "apps";
            List<Class<?>> classes = getClasses(classPath);
            System.out.println("Nombre de classes trouvées dans : " +classPath  + classes.size());

            for (Class<?> controller : classes) {
                if (controller.isAnnotationPresent(Controller.class)) {
                    for (Method m : controller.getDeclaredMethods()) {
                        if (m.isAnnotationPresent(Url.class)) {
                            Url uri = m.getAnnotation(Url.class);
                            Map<String, String> map = new HashMap<>();
                            map.put("classe", controller.getSimpleName());
                            map.put("methode", m.getName());
                            controllerFound.put(uri.value(), map);
                            System.out.println("Route ajoutée: " + uri.value() + " → " +
                                    controller.getSimpleName() + "." + m.getName());
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> handleRequestPackage(String url, String classPath) throws Exception {
        try {
            List<Class<?>> classes = getClasses(classPath);
            System.out.println("taille classes " + classes.size());

            for (Class<?> controller : classes) {
                if (controller.isAnnotationPresent(Controller.class)) {
                    System.out.println("Classe trouvée : " + controller.getName());

                    Object instance = controller.getDeclaredConstructor().newInstance();

                    for (Method m : controller.getDeclaredMethods()) {
                        if (m.isAnnotationPresent(Url.class)) {
                            Url uri = m.getAnnotation(Url.class);
                            if (uri.value().equals(url)) {
                                // Object result = m.invoke(instance);
                                // System.out.println(result);
                                Map map = new HashMap<>();
                                map.put("classe", controller.getSimpleName());
                                map.put("methode", m.getName());

                                return map;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("404 - Page non trouvée");
        return null;
    }

    private static List<Class<?>> getClasses(String packageName) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String protocol = resource.getProtocol();

            if ("file".equals(protocol)) {
                File directory = new File(resource.getFile());
                classes.addAll(findClasses(directory, packageName));
            } else if ("jar".equals(protocol)) {
                String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
                try (java.util.jar.JarFile jar = new java.util.jar.JarFile(jarPath)) {
                    jar.stream()
                            .filter(e -> e.getName().startsWith(path) && e.getName().endsWith(".class"))
                            .forEach(e -> {
                                String className = e.getName()
                                        .replace('/', '.')
                                        .substring(0, e.getName().length() - 6);
                                try {
                                    classes.add(Class.forName(className));
                                } catch (ClassNotFoundException ex) {
                                    ex.printStackTrace();
                                }
                            });
                }
            }
        }

        return classes;
    }

    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        if (files == null)
            return classes;

        for (File file : files) {
            System.out.println("tayyyyy " + file.getName());
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                classes.add(Class.forName(className));
            }
        }
        return classes;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        ServletContext servletContext = req.getServletContext();
        RequestDispatcher requestDispatcher = servletContext.getNamedDispatcher("default");
        String classPath = "controllers";
        try {
            boolean ressourceExist = servletContext.getResource(path) != null;
            // Map controllerFound = handleRequestPackage(path, classPath);
            if (ressourceExist) {
                requestDispatcher.forward(req, resp);
            } else if (controllerFound != null) {
                resp.setContentType("text/plain");
                resp.getWriter().println("✅ Contenu de controllerFound :");

                for (Object key : controllerFound.keySet()) {
                    Object value = controllerFound.get(key);
                    resp.getWriter().println(key + " : " + value);
                }

                resp.getWriter().println("-----------------------------");
                resp.getWriter().println("URL demandée : " + req.getRequestURI());

            } else {
                resp.setContentType("text/plain");
                resp.getWriter().println("404 error page not found, x URL est: " + req.getRequestURI());
            }
            // else {
            // resp.setContentType("text/plain");
            // resp.getWriter().println("Hello, ton URL est: " + req.getRequestURI());
            // }
        } catch (

        Exception e) {
            // throw e;
            e.printStackTrace();
        }

    }

}
