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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.IOUtils;
import webserver.resource.ResourceAccessor;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

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
            HttpRequest currentRequest = HttpMessageParser.parseHttpMessage(reader);
            
            byte[] body = null;
            
            File resource = resourceAccessor.getResource(currentRequest.getRequestUri());
            
            if (resource == null) {
                Object result = null;
                
                try {
                    result = resourceAccessor.invokeCustomAction(currentRequest);
                } catch (Exception e) {
                    // 404 처리
                }
                
                if (result == null) {
                    return;
                } else {
                    resource = resourceAccessor.getResource(result.toString());
                }
            }
            
            body = IOUtils.fetch(new FileInputStream(resource)).getBytes();
            
            DataOutputStream dos = new DataOutputStream(out);
            
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }
    
    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
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
