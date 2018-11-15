package edu.unc.cs.httpTools.request;

import java.util.ArrayList;
import java.util.Map.Entry;

/**
 *
 * @author Andrew Vitkus
 */
public abstract class MultipartRequestBody implements IRequestBody {

    private String bodyText;
    protected String boundary;
    protected final ArrayList<MultipartContent> contents;

    protected MultipartRequestBody() {
        contents = new ArrayList<MultipartContent>(2);
        boundary = "";
        bodyText = "";
        init();
        boundary = boundary.trim();
    }

    @Override
    public String getBody() {
        return computeBody();
    }

    public MultipartContent[] getContents() {
        return contents.toArray(new MultipartContent[contents.size()]);
    }

    private String computeBody() {
        if (bodyText.isEmpty()) {
            StringBuilder body = new StringBuilder(100);
            for (MultipartContent content : contents) {
                body.append("--").append(boundary).append("\r\n");
                String dispostion = content.getDispositionType();
                if (!dispostion.isEmpty()) {
                    body.append("Content-Disposition: ").append(content.getDispositionType());
                    for (Entry<String, String> entry : content.getDisposition().entrySet()) {
                        body.append("; ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
                    }
                    body.append("\r\n");
                }

                String type = content.getType();
                if (!type.isEmpty()) {
                    body.append("Content-Type: ").append(content.getType()).append("\r\n");
                }

                body.append("\r\n").append(content.getContent()).append("\r\n");
            }
            body.append("--").append(boundary).append("--");
            bodyText = body.toString();
        }
        return bodyText;
    }

    protected abstract void init();

}
