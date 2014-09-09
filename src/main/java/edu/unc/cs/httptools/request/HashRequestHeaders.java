package edu.unc.cs.httpTools.request;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Andrew Vitkus
 */
public abstract class HashRequestHeaders implements IRequestHeaders {

    private String headerText;
    protected final LinkedHashMap<String, String[]> headers;

    protected HashRequestHeaders() {
        headers = new LinkedHashMap<String, String[]>(5);
        headerText = "";
        init();
    }

    @Override
    public String getHeader() {
        if (headerText.isEmpty()) {
            StringBuilder header = new StringBuilder(100);
            for (Entry<String, String[]> entry : headers.entrySet()) {
                header.append(entry.getKey()).append(": ");
                String[] values = entry.getValue();
                for (int i = 0; i < values.length - 1; i++) {
                    header.append(values[i]).append("; ");
                }
                header.append(values[values.length - 1]).append("\r\n");
            }
            headerText = header.toString();
        }
        return headerText;
    }

    @Override
    public Map<String, String[]> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    protected abstract void init();
}
