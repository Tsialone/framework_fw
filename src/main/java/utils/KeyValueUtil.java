package utils;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

public class KeyValueUtil {
    private String key;
    private Object value;

    public KeyValueUtil(String key, Object value) {
        setKey(key);
        setValue(value);
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public static HashMap<String, Object> getMapByRequest(HttpServletRequest req) {
        HashMap<String, Object> resp = new HashMap<>();

        Map<String, String[]> params = req.getParameterMap();

        for (String key : params.keySet()) {
            Object[] ob = req.getParameterValues(key);
            System.out.println("Key: " + key);
            for (Object object : ob) {
                System.out.println("         ob:" + object.toString());
            }
            resp.put(key, ob);
        }

        return resp;
    }

    public static String outMap(HashMap<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "Map vide ou null";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Contenu du Map :\n");

        for (String key : map.keySet()) {
            Object value = map.get(key);

            sb.append(key).append(" = ");

            if (value instanceof Object[] arr) {
                sb.append("[");
                for (int i = 0; i < arr.length; i++) {
                    sb.append(arr[i]);
                    if (i < arr.length - 1)
                        sb.append(", ");
                }
                sb.append("]");
            } else {
                sb.append(value);
            }

            sb.append("\n");
        }

        return sb.toString();
    }

}
