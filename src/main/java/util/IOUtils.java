package util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IOUtils {
    /**
     * @param BufferedReader는
     *            Request Body를 시작하는 시점이어야
     * @param contentLength는
     *            Request Header의 Content-Length 값이다.
     * @return
     * @throws IOException
     */
    public static String readData(BufferedReader br, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        return String.copyValueOf(body);
    }
    
    public static String fetch(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        
        StringBuffer sb = new StringBuffer();
        String line = null;
        
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        
        return sb.toString();
    }
    
    public static void closeQuietly(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                // 익셉션을 외부로 Throw하지 않지만, 오류 발생을 알리기 위해 오류 출력
                e.printStackTrace();
            }
        }
    }
    
}
