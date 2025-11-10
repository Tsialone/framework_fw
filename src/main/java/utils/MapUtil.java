package utils;

import java.lang.reflect.Method;


public class MapUtil {
    private String url;
    private Class<?> classe;
    private Method methode;

    public MapUtil() {
    }

    public MapUtil(String url, Class<?> classe, Method methode) {
        this.url = url;
        this.classe = classe;
        this.methode = methode;
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
