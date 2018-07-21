package webserver;

public class HttpRequest {

    private String method;
    
    private String requestUri;
    
    private String httpVersion;

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

    @Override
    public String toString() {
        return "HttpRequest [method=" + method + ", requestUri=" + requestUri + ", httpVersion=" + httpVersion + "]";
    }
    
}
