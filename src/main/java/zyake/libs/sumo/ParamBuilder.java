package zyake.libs.sumo;

import zyake.libs.sumo.util.Args;

import java.util.HashMap;
import java.util.Map;

/**
 * An object that constructs statement parameters.
 *
 * <p>
 *     Typical usage:
 * </p>
 * <pre>
 *     new ParamBuilder()
 *      .set(1, id)
 *      .set(2, name)
 *      .set(3, value);
 * </pre>
 *
 */
public final class ParamBuilder {

    private final Map<String, Object> params = new HashMap<>();

    public ParamBuilder set(String param, Object value) {
        Args.check(param);
        Args.check(value);
        params.put(param, value);
        return this;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void clear() {
        params.clear();
    }
}
