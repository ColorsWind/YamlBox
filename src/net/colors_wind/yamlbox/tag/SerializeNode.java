package net.colors_wind.yamlbox.tag;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.colors_wind.yamlbox.loader.EntryLoader;
import net.colors_wind.yamlbox.loader.FieldSelector;

/**
 * 用于标记类&字段进行序列化/反序列化用到加载器和字段选择器.
 * @author colors_wind
 * @date 2020/6/19
 * @since 1.0.0
 */
@Documented
@Retention(RUNTIME)
@Target({TYPE,FIELD})
public @interface SerializeNode {
	
	/**
	 * 加载器
	 */
	String loader() default EntryLoader.ENTRY;
	
	/**
	 * 字段选择器
	 */
	FieldSelector fieldSelector(); 

}
