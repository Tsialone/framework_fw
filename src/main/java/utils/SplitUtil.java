package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SplitUtil {
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
    public static HashMap<String, Object> getKeyValueByParam(String str) {
        List<String> splited = splitByStr(str, "\\=");
        if (splited.size() == 2) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(splited.get(0), splited.get(1));
            return hashMap;
        }
        return null;
    }

    public static List<String> getPossiblePath (String path){
        List<String> splited = splitByStr(path, "/");
        List<String>resp = new ArrayList<>();
        if (splited.size() > 1) {
            resp.add(splited.getFirst());
        }
        // resp.add(path);
        return resp;
    }

    public static List<HashMap<String, Object>> getKeyValueByParamUrl(String param) {
        List<HashMap<String, Object>> resp = new ArrayList<>();

        List<String> splited = splitByStr(param, "\\&");
        for (String string : splited) {
            resp.add(getKeyValueByParam(string));
        }
        return resp;
    }
}
