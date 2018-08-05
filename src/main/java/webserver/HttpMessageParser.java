/**
 * 
 */
package webserver;

import java.io.BufferedReader;
import java.io.IOException;

import util.HttpRequestUtils;
import util.IOUtils;
import util.HttpRequestUtils.Pair;
import webserver.http.HttpRequest;

/**
 * HTTP Protocol Parser
 *
 * @author HG
 * @since 2018. 8. 5.
 * @see
 *
 */
public class HttpMessageParser {

    public static HttpRequest parseHttpMessage(BufferedReader reader) {
        HttpRequest currentRequest = new HttpRequest();
        
        try {
            parseBaseInfo(reader, currentRequest);
            
            parseHeader(reader, currentRequest);
            
            if (!"GET".equals(currentRequest.getMethod())) {
                parseBody(reader, currentRequest);
            }
            
            // 파라미터 처리
            parseParameter(reader, currentRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return currentRequest;
    }
    
    private static void parseBaseInfo(BufferedReader reader, HttpRequest currentRequest) throws Exception {
        String line = reader.readLine();
        
        String[] lineArr = line.split(" ");
        
        if (lineArr.length != 3) {
            throw new IOException("Not HTTP Request");
        }
        
        // HTTP 기본정보 처리
        currentRequest.setMethod(lineArr[0].toUpperCase());
        currentRequest.setRequestUri(lineArr[1]);
        currentRequest.setHttpVersion(lineArr[2]);
    }
    
    private static void parseHeader(BufferedReader reader, HttpRequest currentRequest) throws Exception {
        String line = null;
        
        while ((line = reader.readLine()) != null) {
            if ("".equals(line)) {
                return;
            }
            
            Pair pair = HttpRequestUtils.parseHeader(line);

            currentRequest.addHeader(pair.getKey(), pair.getValue());
        }
    }
    
    private static void parseBody(BufferedReader reader, HttpRequest currentRequest) throws Exception {
        if (currentRequest.getHeader("Content-Length") != null) {
            int contentLength = Integer.valueOf(currentRequest.getHeader("Content-Length"));
            
            currentRequest.setBody(IOUtils.readData(reader, contentLength));
        }
    }
    
    private static void parseParameter(BufferedReader reader, HttpRequest currentRequest) throws IOException {
        // GET 요청 파라미터 처리
        if ("GET".equals(currentRequest.getMethod())) {
            String[] uriParam = currentRequest.getRequestUri().split("\\?");
            
            if (uriParam.length == 2) {
                currentRequest.setRequestUri(uriParam[0]);
                currentRequest.setParameters(HttpRequestUtils.parseQueryString(uriParam[1]));
            }
        }
        // POST 요청 파라미터 처리
        else if ("POST".equals(currentRequest.getMethod())) {
            currentRequest.setParameters(HttpRequestUtils.parseQueryString(currentRequest.getBody()));
        }
    }
    
}
