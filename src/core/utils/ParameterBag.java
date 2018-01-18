package core.utils;

import java.util.HashMap;

/**
 * represents a set of parameters transmitted between functions (for rendering or router)
 */
public class ParameterBag extends HashMap<String, Object> {

    public ParameterBag add(String k, Object v) {
        super.put(k, v);
        return this;
    }

    public boolean has(String k) {
        return this.containsKey(k);
    }

}
