package views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelView {
    private String view;
    private List<Map<String, Object>> datas = new ArrayList<>();

    public ModelView(String view) {
        this.view = view;
    }

    public ModelView() {
    }

    public void putData(String key, Object object) {
        HashMap<String, Object> tempMap = new HashMap<>();
        tempMap.put(key, object);
        datas.add(tempMap);
    }

    public Object getDataByKey(String key) {
        for (Map<String, Object> map : datas) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
        }
        return null;
    }

    public void setData(List<Map<String, Object>> datas) {
        this.datas = datas;
    }

    public List<Map<String, Object>> getData() {
        return datas;
    }

    public void addData(Map<String, Object> data) {
        if (data != null) {
            datas.add(data);
        }
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }
}
