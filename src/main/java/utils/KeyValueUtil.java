package utils;

public class KeyValueUtil {
    private String key;
    private Object value;


    public KeyValueUtil (String key , Object value){
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
}
