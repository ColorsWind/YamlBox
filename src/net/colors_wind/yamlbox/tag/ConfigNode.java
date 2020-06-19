/**
 * 
 */
package net.colors_wind.yamlbox.tag;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.colors_wind.yamlbox.loader.UniversalLoader;
import net.colors_wind.yamlbox.conf.ConfigSection;

/**
 * 
 * @author colors_wind
 * @date 2020/6/17
 * @since 1.0.0
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface ConfigNode {

	/**
	 * 字段对应的YAML节点路径, 支持多级, 用 {@link ConfigSection#DOT} 分隔.
	 */
	String path() default "";
	
	/**
	 * 字段选择器
	 */
	String loader() default UniversalLoader.UNIVERSAL;
}
