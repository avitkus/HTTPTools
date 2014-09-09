package edu.unc.cs.httpTools.request;

import static java.util.Arrays.asList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Not thread safe
 *
 * @author Andrew Vitkus
 */
public class RequestHeaderFactory {

    public static RequestHeaderFactory getDefault() {
        return new RequestHeaderFactory();
    }

    private final Map<String, String[]> headerMap;

    protected RequestHeaderFactory() {
        headerMap = new LinkedHashMap<String, String[]>(5);
    }

    public void addHeader(String key, String... value) {

        synchronized (headerMap) {
            if (headerMap.containsKey(key)) {
                LinkedHashSet<String> vals = new LinkedHashSet<String>(2);
                vals.addAll(asList(headerMap.get(key)));
                vals.addAll(asList(value));
                headerMap.put(key, vals.toArray(new String[vals.size()]));
            } else {
                headerMap.put(key, value);
            }
        }
    }

    public IRequestHeaders getHeaders() {
        return new HashRequestHeaders() {

            @Override
            protected void init() {
                synchronized (headerMap) {
                    headers.putAll(headerMap);
                }
            }
        };
    }

}
