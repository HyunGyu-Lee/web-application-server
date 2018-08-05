/**
 * 
 */
package webserver.customaction.action;

import java.lang.reflect.Method;

/**
 * 컨트롤러 Action에 대한 정의
 *
 * @author HG
 * @since 2018. 7. 30.
 * @see
 *
 */
public class Action {

    private Method handler;
    private Object instance;
    
    public Action(Object instance, Method handler) {
        this.instance = instance;
        this.handler = handler;
    }
    
    public Object invokeAction(Object...args) throws Exception {
        return handler.invoke(instance, args);
    }
    
}
