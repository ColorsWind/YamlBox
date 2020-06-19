package net.colors_wind.yamlbox.loader;

import java.lang.reflect.Field;

/**
 * 字段选择器接口
 * @author colors_wind
 * @date 2020/6/19
 * @since 1.0.0
 */
@FunctionalInterface
public interface IFieldSelector {
	
	/**
	 * 选择类中的字段
	 * @param clazz 类
	 * @return 命中的字段
	 */
	Field[] apply(Class<?> clazz);
}
