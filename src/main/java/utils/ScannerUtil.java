package utils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import annotations.ControllerAnnotation;
import annotations.UrlAnnotation;
import annotations.UrlGetAnnotation;
import annotations.UrlPostAnnotation;

public class ScannerUtil {
    List<MapUtil> mapUtils = new ArrayList<>();

    HashMap<String, List<MapUtil>> mapHash = new HashMap<>();
    String classPath;

    public HashMap<String, List<MapUtil>> getMapHash() {
        return mapHash;
    }

    public void setMapHash(HashMap<String, List<MapUtil>> mapHash) {
        this.mapHash = mapHash;
    }

    public ScannerUtil(String classPath) throws Exception {
        setClassPath(classPath);
        System.out.println("=== Initialisation FrontServlet ===");
        List<Class<?>> classes = getClasses(classPath);
        System.out.println("Nombre de classes trouvées dans : " + classPath + classes.size());

        // for (Class<?> controller : classes) {
        // if (controller.isAnnotationPresent(ControllerAnnotation.class)) {
        // for (Method m : controller.getDeclaredMethods()) {
        // if (m.isAnnotationPresent(UrlAnnotation.class)) {
        // UrlAnnotation uri = m.getAnnotation(UrlAnnotation.class);
        // MapUtil mapUtil = new MapUtil();
        // mapUtil.setClasse(controller);
        // mapUtil.setUrl(uri.value());
        // mapUtil.setMethode(m);
        // mapUtils.add(mapUtil);
        // }
        // }
        // }
        // }

        for (Class<?> controller : classes) {
            if (controller.isAnnotationPresent(ControllerAnnotation.class)) {
                Method[] hisMethods = controller.getDeclaredMethods();
                List<String> urlVerified = new ArrayList<>();
                for (int i = 0; i < hisMethods.length; i++) {
                    List<MapUtil> temp = new ArrayList<>();
                    Method methodI = hisMethods[i];
                    String urlValueI = null;
                    Annotation[] hisAnnotations = methodI.getAnnotations();
                    for (Annotation annotation : hisAnnotations) {

                        if (annotation instanceof UrlAnnotation u) {
                            urlValueI = u.value();
                        } else if (annotation instanceof UrlGetAnnotation get) {
                            urlValueI = get.value();
                        } else if (annotation instanceof UrlPostAnnotation post) {
                            urlValueI = post.value();
                        }
                    }
                    if (urlValueI == null)
                        continue;
                    if (urlVerified.contains(urlValueI))
                        continue;

                    MapUtil mapUtilI = new MapUtil();
                    mapUtilI.setClasse(controller);
                    mapUtilI.setUrl(urlValueI);
                    mapUtilI.setMethode(methodI);
                    temp.add(mapUtilI);
                    urlVerified.add(urlValueI);
                    for (int j = i + 1; j < hisMethods.length - 1; j++) {
                        Method methodJ = hisMethods[j];
                        Annotation[] hisAnnotationsJ = methodJ.getAnnotations();
                        String urlValueJ = null;
                        for (Annotation annotation : hisAnnotationsJ) {
                            if (annotation instanceof UrlAnnotation u) {
                                urlValueJ = u.value();
                            } else if (annotation instanceof UrlGetAnnotation get) {
                                urlValueJ = get.value();
                            } else if (annotation instanceof UrlPostAnnotation post) {
                                urlValueJ = post.value();
                            }
                        }

                        // if (!methodJ.isAnnotationPresent(UrlAnnotation.class))
                        // continue;
                        if (urlValueJ == null)
                            continue;

                        MapUtil mapUtilJ = new MapUtil();
                        mapUtilJ.setClasse(controller);
                        mapUtilJ.setUrl(urlValueJ);
                        mapUtilJ.setMethode(methodJ);

                        if (urlValueI.equals(urlValueJ)) {
                            System.out.println("mitovy eee: " + urlValueJ + " --- " + urlValueI);
                            temp.add(mapUtilJ);
                        }
                    }
                    System.out.println("puted:" + urlValueI + " temp_size: " + temp.size());
                    this.mapHash.put(urlValueI, temp);
                }
                // for (Method m : controller.getDeclaredMethods()) {

                // if (m.isAnnotationPresent(UrlAnnotation.class)) {
                // UrlAnnotation uri = m.getAnnotation(UrlAnnotation.class);
                // MapUtil mapUtil = new MapUtil();
                // mapUtil.setClasse(controller);
                // mapUtil.setUrl(uri.value());
                // mapUtil.setMethode(m);
                // mapUtils.add(mapUtil);
                // }
                // }
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
