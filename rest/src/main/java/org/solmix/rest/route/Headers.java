package org.solmix.rest.route;

import java.util.Map;

import org.solmix.rest.route.entity.CaseInsensitiveMap;

/**
 * @author Dreampie
 * @date 2015-08-21
 * @what
 */
public class Headers extends Params {
    public Headers(final Map<String, String> headers) {
        super(new CaseInsensitiveMap<Object>() {{
            putAll(headers);
        }});
    }
}
