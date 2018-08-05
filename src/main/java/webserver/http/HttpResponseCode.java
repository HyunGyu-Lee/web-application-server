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
public class HttpResponseCode {

    private static final Map<Integer, String> responseCodeMap = new HashMap<>();
    
    static {
        responseCodeMap.put(200, "OK");
        
        responseCodeMap.put(404, "Not Found");
        
        responseCodeMap.put(500, "Internal Server Error");
    }
    
    public static String getMessage(int responseCode) {
        return responseCodeMap.get(responseCode);
    }
    
}
