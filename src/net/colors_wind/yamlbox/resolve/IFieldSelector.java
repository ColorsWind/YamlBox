package net.colors_wind.yamlbox.resolve;

import java.lang.reflect.Field;

@FunctionalInterface
public interface IFieldSelector {
	
	Field[] apply(Class<?> clazz);
}
