package edu.unc.cs.httpTools.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Andrew Vitkus
 */
public abstract class MultipartContent {

    protected String contents;

    protected final HashMap<String, String> disposition;
    protected String dispositionType;
    protected String type;

    protected MultipartContent() {
        disposition = new HashMap<String, String>(3);
        init();
    }

    public String getContent() {
        return contents;
    }

    public String getDisposition(String key) {
        return disposition.get(key);
    }

    public Map<String, String> getDisposition() {
        return Collections.unmodifiableMap(disposition);
    }

    public String getDispositionType() {
        return dispositionType;
    }

    public String getType() {
        return type;
    }

    protected abstract void init();
}
