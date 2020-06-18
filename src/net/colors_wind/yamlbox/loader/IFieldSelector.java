package net.colors_wind.yamlbox.loader;

import java.lang.reflect.Field;

@FunctionalInterface
public interface IFieldSelector {
	
	Field[] apply(Class<?> clazz);
}
