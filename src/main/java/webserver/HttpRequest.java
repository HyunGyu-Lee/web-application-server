package webserver;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private String method;

    private String requestUri;

    private String httpVersion;

    private String body;
    
    private Map<String, String> headers;

    private Map<String, String> parameters;

    public HttpRequest() {
        this.headers = new HashMap<>();
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public String getActionUrl() {
        return getMethod() + "#" + getRequestUri();
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "HttpRequest [method=" + method + ", requestUri=" + requestUri + ", httpVersion=" + httpVersion
                + ", headers=" + headers + ", parameters=" + parameters + "]";
    }
    
}
