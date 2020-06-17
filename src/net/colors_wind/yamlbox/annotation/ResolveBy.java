/**
 * 
 */
package net.colors_wind.yamlbox.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.colors_wind.yamlbox.resolve.IObjectResolver;

@Documented
@Retention(RUNTIME)
@Target({ TYPE, FIELD })
/**
 * 用于标记解析这个字段的解析器, 这个标记不是必须的. <p>
 * 解析器的查找顺序是: 字段注解 -> 类注解 -> 预设注解
 * @author colors_wind
 * @date 2020/5/9
 * @since 1.0.0
 */
public @interface ResolveBy {
	
	Class<? extends IObjectResolver> value(); 
	
	boolean share() default true;
}
