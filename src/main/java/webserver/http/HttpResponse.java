/**
 * 
 */
package webserver.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Note class information
 *
 * @author HG
 * @since 2018. 8. 5.
 * @see
 *
 */
public class HttpResponse {

    private int responseCode;
    
    private String message;
    
    private Map<String, String> headers;
    
    private byte[] body;
    
    public HttpResponse() {
        headers = new HashMap<>();
    }
    
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
    
    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public int getResponseCode() {
        return responseCode;
    }
    
    public void setResponseStatus(int responseCode) {
        this.responseCode = responseCode;
        this.setMessage(HttpResponseCode.getMessage(responseCode));
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
}
