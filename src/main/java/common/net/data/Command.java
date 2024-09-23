package common.net.data;

import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class Command implements Serializable, Runnable {
    public final Map headers = new HashMap<>();
    @Getter
    protected transient Entity entity;

    public Command (Entity entity) {
        this.entity = entity;
    }


    public Command addHeader(Object k, Object v) {
        headers.put(k, v);
        return this;
    }

    public Object getHeader(Object key) {
        return headers.get(key);
    }

    public boolean isValid(Command command) {return true;}

    @Override
    public void run() {

    } // executed upon receive
}
