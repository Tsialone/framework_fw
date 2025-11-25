package utils;

import java.lang.reflect.Method;

public class MapUtil {
    private String url;
    private Class<?> classe;
    private Method methode;
    private String httpMethode = "ALL";

    public String getHttpMethode() {
        return httpMethode;
    }

    public void setHttpMethode(String httpMethode) {
        this.httpMethode = httpMethode;
    }

    public MapUtil() {
    }

    public MapUtil(String url, Class<?> classe, Method methode, String httpMethode) {
        this.url = url;
        this.classe = classe;
        this.methode = methode;
        this.httpMethode = httpMethode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Class<?> getClasse() {
        return classe;
    }

    public void setClasse(Class<?> classe) {

        this.classe = classe;

    }

    public Method getMethode() {
        return methode;
    }

    public void setMethode(Method methode) {
        if (methode != null) {
            if (methode.isAnnotationPresent(annotations.UrlPostAnnotation.class)) {
                httpMethode = "POST";
            } else if (methode.isAnnotationPresent(annotations.UrlGetAnnotation.class)) {
                httpMethode = "GET";
            } else  {
                httpMethode = "ALL";
            }
            this.methode = methode;
        }
    }

    @Override
    public String toString() {
        return "MapUtil{" +
                "url='" + url + '\'' +
                ", classe=" + (classe != null ? classe.getName() : "null") +
                ", methode=" + (methode != null ? methode.getName() : "null") +
                ", httpMethode=" + (httpMethode != null ? httpMethode : "null") +
                '}';
    }
}
