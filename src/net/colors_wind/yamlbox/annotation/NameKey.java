package net.colors_wind.yamlbox.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 用于标记字段的 YAML key, 这个标注不是必须的. 若省略, 则使用字段名作为 YAML 中的 key.
 * @author colors_wind
 * @date 2020/5/9
 * @since 1.0.0
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface NameKey {

	String value();
	
}
