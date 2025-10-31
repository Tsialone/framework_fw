package utils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import annotations.ControllerAnnotation;
import annotations.UrlAnnotation;

public class ScannerUtil {
    List<MapUtil> mapUtils = new ArrayList<>();
    String classPath;

    public ScannerUtil(String classPath) throws Exception {
        setClassPath(classPath);
        System.out.println("=== Initialisation FrontServlet ===");
        List<Class<?>> classes = getClasses(classPath);
        System.out.println("Nombre de classes trouvées dans : " + classPath + classes.size());

        for (Class<?> controller : classes) {
            if (controller.isAnnotationPresent(ControllerAnnotation.class)) {
                for (Method m : controller.getDeclaredMethods()) {
                    if (m.isAnnotationPresent(UrlAnnotation.class)) {
                        UrlAnnotation uri = m.getAnnotation(UrlAnnotation.class);
                        MapUtil mapUtil = new MapUtil();
                        mapUtil.setClasse(controller);
                        mapUtil.setUrl(uri);
                        mapUtil.setMethode(m);
                        mapUtils.add(mapUtil);
                    }
                }
            }
        }

    };

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public void setMapUtils(List<MapUtil> mapUtils) {
        this.mapUtils = mapUtils;
    }

    public String getClassPath() {
        return classPath;
    }

    public List<MapUtil> getMapUtils() {
        return mapUtils;
    }

    private List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
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

    private List<Class<?>> getClasses(String packageName) throws Exception {
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

}
