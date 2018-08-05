/**
 * 
 */
package webserver.customaction.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Request URI에 대응하는 동작에 대한 매핑을 정의한다.
 *
 * @author HG
 * @since 2018. 7. 30.
 * @see
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface RequestMapping {

    String method() default "GET";
    
    String value();
    
}
