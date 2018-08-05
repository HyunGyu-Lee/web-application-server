/**
 * 
 */
package webserver.resource;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import webserver.controller.Controller;
import webserver.customaction.DefinitionParser;
import webserver.customaction.action.Action;
import webserver.http.HttpRequest;

/**
 * WEB서버의 자원에 접근하는 클래스
 *
 * @author HG
 * @since 2018. 7. 30.
 * @see
 *
 */
public class ResourceAccessor {

    private static final Logger logger = LoggerFactory.getLogger(ResourceAccessor.class);
    
    private static final String RESOURCE_PREFIX = "webapp";
    
    private Map<String, Action> actions;
    
    private Tika tika;
    
    public ResourceAccessor() {
        actions = new HashMap<>();
        setCustomActions(new Class<?>[] {
            Controller.class
        });
        
        tika = new Tika();
    }
    
    private void setCustomActions(Class<?>[] actionDefines) {
        for (Class<?> customDefine : actionDefines) {
            try {
                actions.putAll(DefinitionParser.parseDefinition(customDefine));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private String resolveResourcePath(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        
        return RESOURCE_PREFIX + File.separator + path;
    }
    
    public File getResource(String requestURI) {
        File fileResource = new File(resolveResourcePath(requestURI));
        
        if (!fileResource.exists()) {
            return null;
        }
        
        return fileResource;
    }
    
    public Object invokeCustomAction(HttpRequest request) {
        Action action = actions.get(request.getActionUrl());

        Object returnValue = null;

        try {
            returnValue = action.invokeAction(request);
        } catch (Exception e) {
            logger.debug("Error Occur execute custom action... " + e.getMessage());
            e.printStackTrace();
        }

        return returnValue;
    }
    
    public String detectMimeType(File resource) {
        try {
            return tika.detect(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return "";
    }
    
    public boolean containsAction(String actionUrl) {
        return actions.containsKey(actionUrl);
    } 
    
    public static ResourceAccessor getInstance(Class<?>...customActions) {
        return Singleton.instance;
    }
    
    /**
     * Singleton 인스턴스 정의
     *
     * @author HG
     * @since 2018. 7. 30.
     * @see
     *
     */
    private static class Singleton {
        private static final ResourceAccessor instance = new ResourceAccessor();
    }
    
}
