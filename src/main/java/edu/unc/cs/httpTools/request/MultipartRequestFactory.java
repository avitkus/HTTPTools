package edu.unc.cs.httpTools.request;

import edu.unc.cs.httpTools.util.HTTPMethod;
import static edu.unc.cs.httpTools.util.HTTPMethod.POST;
import edu.unc.cs.httpTools.util.HTTPVersion;
import static edu.unc.cs.httpTools.util.HTTPVersion.HTTP11;

/**
 *
 * @author Andrew Vitkus
 */
public class MultipartRequestFactory {

    private static final String DEFAULT_BOUNDARY = "----------1234567890f0987654321";
    private static final HTTPMethod DEFAULT_HTTP_METHOD = POST;
    private static final HTTPVersion DEFAULT_HTTP_VERSION = HTTP11;

    public static MultipartRequestFactory getCustomBoundary(String boundary, String resource) {
        return new MultipartRequestFactory(boundary, DEFAULT_HTTP_VERSION, DEFAULT_HTTP_METHOD, resource);
    }

    public static MultipartRequestFactory getCustomBoundaryAndMethod(String boundary, HTTPVersion version, HTTPMethod method, String resource) {
        return new MultipartRequestFactory(boundary, version, method, resource);
    }

    public static MultipartRequestFactory getCustomMethod(HTTPVersion version, HTTPMethod method, String resource) {
        return new MultipartRequestFactory(DEFAULT_BOUNDARY, version, method, resource);
    }

    public static MultipartRequestFactory getDefault(String resource) {
        return new MultipartRequestFactory(DEFAULT_BOUNDARY, DEFAULT_HTTP_VERSION, DEFAULT_HTTP_METHOD, resource);
    }

    private final MultipartRequestBodyFactory bodyFactory;
    private final String boundary;
    private final RequestHeaderFactory headerFactory;
    private final HTTPMethod method;
    private final String resource;
    private final HTTPVersion version;

    private MultipartRequestFactory(String boundary, HTTPVersion version, HTTPMethod method, String resource) {
        headerFactory = RequestHeaderFactory.getDefault();
        bodyFactory = MultipartRequestBodyFactory.getDefault();
        this.boundary = boundary;
        this.version = version;
        this.method = method;
        this.resource = resource;
    }

    public void addHeader(String key, String... value) {
        headerFactory.addHeader(key, value);
    }

    public void addPart(String data, String type, String dispositionType, String... disposition) {
        bodyFactory.addPart(data, type, dispositionType, disposition);
    }

    public IRequest buildRequest() {
        IRequest request;
        if (headerFactory.getHeaders().getHeaders().containsKey("Content-Type")) {
            request = buildRequest("", false);
        } else {
            request = buildRequest("multipart/mixed", true);
        }
        return request;
    }

    public IRequest buildRequest(String type, boolean addTypeHeader) {
        IRequestBody body = bodyFactory.buildBody(boundary);

        if (addTypeHeader) {
            headerFactory.addHeader("Content-Type", type, "boundary=" + boundary);
        } else {
            headerFactory.addHeader("Content-Type", "boundary=" + boundary);
        }
        headerFactory.addHeader("Content-Length", Integer.toString(body.getBody().length()));

        IRequestHeaders header = headerFactory.getHeaders();

        IRequestLine request = new RequestLine(version, method, resource);

        return MultipartRequest.getInstance(request, header, body);
    }
}
