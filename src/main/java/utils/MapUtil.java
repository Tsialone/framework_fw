package utils;

import java.lang.reflect.Method;

import annotations.UrlAnnotation;

public class MapUtil {
    private UrlAnnotation url;
    private Class<?> classe;
    private Method methode;

    public MapUtil() {
    }

    public MapUtil(UrlAnnotation url, Class<?> classe, Method methode) {
        this.url = url;
        this.classe = classe;
        this.methode = methode;
    }

    public UrlAnnotation getUrl() {
        return url;
    }

    public void setUrl(UrlAnnotation url) {
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
        this.methode = methode;
    }

    @Override
    public String toString() {
        return "MapUtil{" +
                "url='" + url + '\'' +
                ", classe=" + (classe != null ? classe.getName() : "null") +
                ", methode=" + (methode != null ? methode.getName() : "null") +
                '}';
    }
}
