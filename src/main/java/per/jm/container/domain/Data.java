package per.jm.container.domain;

import java.util.HashMap;

public class Data {
    private String data;
    private HashMap<String,String> kv = new HashMap<String, String>();

    public String getKv(String k) {
        return kv.get(k);
    }

    public void setKv(String k,String vlaue) {
        this.kv.put(k,vlaue);
    }


    public Object getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


}
