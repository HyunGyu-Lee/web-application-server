/**
 * 
 */
package webserver.customaction;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import webserver.customaction.action.Action;
import webserver.customaction.annotation.RequestMapping;

/**
 * Action Parser
 *
 * @author HG
 * @since 2018. 8. 5.
 * @see
 *
 */
public class DefinitionParser {

    private static final Logger logger = LoggerFactory.getLogger(DefinitionParser.class); 
    
    public static Map<String, Action> parseDefinition(Class<?> definition) throws Exception {
        Object instance = definition.newInstance();
        
        Map<String, Action> actions = new HashMap<>();
        
        Method[] methods = definition.getDeclaredMethods();
        
        for (Method method : methods) {
            RequestMapping mapping = method.getAnnotation(RequestMapping.class);
            
            if (mapping != null) {
                Action action = new Action(instance, method);
                actions.put(mapping.method() + "#" + mapping.value(), action);
                
                logger.debug("Regist Custom Action on {}#{}", mapping.method(), mapping.value());
            }
        }
        
        return actions;        
    }
    
}
