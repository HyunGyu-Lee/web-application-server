package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.IOUtils;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.resource.ResourceAccessor;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private static final String REDIRECT_PREFIX = "redirect:";
    
    private Socket connection;

    private ResourceAccessor resourceAccessor;
    
    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
        this.resourceAccessor = ResourceAccessor.getInstance();
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        
        InputStream in = null;
        OutputStream out = null;
        
        try  {
            in = connection.getInputStream();
            out = connection.getOutputStream(); 
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("utf-8")));
            DataOutputStream dos = new DataOutputStream(out);
            
            HttpRequest currentRequest = HttpMessageParser.parseHttpMessage(reader);
            HttpResponse response = new HttpResponse();
            
            // 현재 요청에 해당하는 CustomAction이 존재하는지 확인
            if (resourceAccessor.containsAction(currentRequest.getActionUrl())) {
                Object returnValue = resourceAccessor.invokeCustomAction(currentRequest, response);
                
                // TODO 지금은 CustomAction 응답에 해당하는 파일을 반환하지만 차후 ReturnValue Resolver를 추가해 다양한 응답을 할 수 있게 바꿀것
                if (returnValue instanceof String) {
                    String responseLocator = returnValue.toString();
                    
                    // Redirect 체크
                    if (responseLocator.startsWith(REDIRECT_PREFIX)) {
                        responseLocator = responseLocator.substring(REDIRECT_PREFIX.length());
                        
                        response.addHeader("Location", responseLocator);
                        response.setBody(new byte[] {});
                        
                        processResponse(dos, currentRequest, response);
                    } else {
                        processFileResource(response, resourceAccessor.getResource(responseLocator));
                    }
                }
            }
            // CustomAction이 없는 경우 File 응답
            else {
                File resource = resourceAccessor.getResource(currentRequest.getRequestUri());
                
                if (resource == null) {
                    resource = resourceAccessor.getResource("/404.html");
                }
                
                processFileResource(response, resource);
            }
            
            // HTTP 요청에 대한 응답을 보냄
            processResponse(dos, currentRequest, response);
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }
    
    private void processFileResource(HttpResponse response, File resource) throws IOException {
        byte[] body = IOUtils.fetch(new FileInputStream(resource)).getBytes();
        
        response.setResponseStatus(200);
        response.addHeader("Content-Type", resourceAccessor.detectMimeType(resource));
        response.addHeader("Content-Length", String.valueOf(body.length));
        response.setBody(body);
    }
    
    private void processResponse(DataOutputStream dos, HttpRequest request, HttpResponse response) {
        responseHeader(dos, request, response);
        
        // Body Response
        responseBody(dos, response.getBody());
    }
    
    private void responseHeader(DataOutputStream dos, HttpRequest request, HttpResponse response) {
        StringBuffer sb = new StringBuffer();
        sb.append(request.getHttpVersion()).append(" ").append(response.getResponseCode()).append(" ").append(response.getMessage()).append(" \r\n");
        
        Map<String, String> headers = response.getHeaders();
        
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        
        sb.append("\r\n");
        
        try {
            dos.writeBytes(sb.toString());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
