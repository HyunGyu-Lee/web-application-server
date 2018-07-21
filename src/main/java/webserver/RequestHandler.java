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

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("utf-8")));
            HttpRequest currentRequest = parseRequest(reader);
            byte[] body = "Hello World".getBytes();
            
            log.debug("{}", currentRequest);
            
            File file = new File("src/main/webapp" + currentRequest.getRequestUri());
            
            
            
            if (file.exists()) {
                body = IOUtils.fetch(new FileInputStream(file)).getBytes();
            } else {
                // TODO 404오류 RESPONSE
            }
            
            DataOutputStream dos = new DataOutputStream(out);
            
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private HttpRequest parseRequest(BufferedReader reader) throws IOException {
        HttpRequest currentRequest = new HttpRequest();
        
        String line = reader.readLine();
        
        String[] lineArr = line.split(" ");
        
        if (lineArr.length != 3) {
            throw new IOException("Not HTTP Request");
        }
        
        currentRequest.setMethod(lineArr[0]);
        currentRequest.setRequestUri(lineArr[1]);
        currentRequest.setHttpVersion(lineArr[2]);
        
        return currentRequest;
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
