/**
 * 
 */
package net.colors_wind.yamlbox.resolve;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
/**
 * @author colors_wind
 * @date 2020/6/17
 * @since 1.0.0
 */
public @interface ConfigNode {

	String path() default "";
	
	String resolver() default UniversalResolver.UNIVERSAL;
}
