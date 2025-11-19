package utils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SplitUtil {

    public static HashMap<String, Object> initKey(String uri, String controllerUrl, String regex) {
        List<String> sp1 = splitByStr(uri, regex);
        List<String> sp2 = splitByStr(controllerUrl, regex);
        if (regex.equals("\\/") && sp1.size() != sp2.size())
            return null;
        if (sp1.size() > 1)
            sp1.remove(0);
        sp2.remove(0);
        System.out.println("sp1: " + sp1);
        System.out.println("sp2: " + sp2);
        HashMap<String, Object> resp = new HashMap<>();

        if (regex.equals("\\/")) {
            for (int i = 0; i < sp1.size(); i++) {
                String newKey = sp2.get(i).replace("}", "").replace("{", "");
                resp.put(newKey, sp1.get(i));
            }
        } else if (regex.equals("\\?")) {
            return getKeyValueByParamUrl(sp1.getFirst());
        }

        return resp;
    }

    public static List<String> splitByStr(String path, String str) {
        List<String> resp = new ArrayList<>();
        String[] splited = path.split(str);
        for (String string : splited) {
            String trimString = string.trim();
            if (!trimString.isEmpty() && trimString.length() > 0) {
                resp.add(trimString);
            }
        }
        return resp;
    }

    // doit etre de la forme: key=1
    public static KeyValueUtil getKeyValueByParam(String str) {
        List<String> splited = splitByStr(str, "\\=");
        if (splited.size() == 2) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(splited.get(0), splited.get(1));
            KeyValueUtil keyValueUtil = new KeyValueUtil(splited.get(0), splited.get(1));
            return keyValueUtil;
        }
        return null;
    }

    public static List<String> getPossiblePath(String path) {
        List<String> splited = splitByStr(path, "/");
        List<String> resp = new ArrayList<>();
        if (splited.size() > 1) {
            resp.add(splited.getFirst());
        }
        // resp.add(path);
        return resp;
    }

    public static HashMap<String, Object> getKeyValueByParamUrl(String param) {
        HashMap<String, Object> resp = new HashMap<>();

        List<String> splited = splitByStr(param, "\\&");
        for (String string : splited) {
            KeyValueUtil keyValueUtil = getKeyValueByParam(string);
            resp.put(keyValueUtil.getKey(), keyValueUtil.getValue());
        }
        return resp;
    }

    public static List<Parameter> getParameterByMethod(Method method) {
        Parameter[] parameters = method.getParameters();
        return Arrays.asList(parameters);
    }
}
