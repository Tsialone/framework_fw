package views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelView {
    private String view;
    private HashMap<String, Object> data  = new HashMap<>();

    public ModelView(String view) {
        this.view = view;
    }

    public ModelView() {
    }

    public void putData(String key, Object object) {
        data.put(key, object);
    }

    public Object getDataByKey(String key) {
        if (data.containsKey(key)) {
            return data.get(key);
        }
        return null;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getData() {
        return data;
    }

    // public void addData(Map<String, Object> data) {
    //     if (data != null) {
    //         datas.add(data);
    //     }
    // }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }
}
