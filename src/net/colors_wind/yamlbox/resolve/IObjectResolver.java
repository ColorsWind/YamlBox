 package net.colors_wind.yamlbox.resolve;

import java.lang.reflect.Type;
import java.util.function.Consumer;

public interface IObjectResolver {
	
	
	/**
	 * 判断该解析器是否可以解析这个类型
	 * @param clazz
	 * @return
	 */
	boolean canAccept(Class<?> clazz);
	
	/**
	 * 判断该解析器是否可以解析这个类型
	 * @param clazz
	 * @param genericType
	 * @return
	 */
	default boolean canAccept(Class<?> clazz, Type genericType) {
		return this.canAccept(clazz);
	}
	
	/**
	 * 解析元素
	 * @param clazz
	 * @param genericType
	 * @param obj
	 * @param logger
	 * @return
	 */
	Object resolve(Class<?> clazz, Type genericType, Object obj, Consumer<String> logger);
	
	default int resolveAsInt(Object obj, Consumer<String> logger) {
		throw new UnsupportedOperationException();
	}
	
	default boolean resolveAsBoolean(Object obj, Consumer<String> logger) {
		throw new UnsupportedOperationException();
	}
	
	default long resolveAsLong(Object obj, Consumer<String> logger) {
		throw new UnsupportedOperationException();
	}
	
	default double resolveAsDouble(Object obj, Consumer<String> logger) {
		throw new UnsupportedOperationException();
	}
	
	default float resolveAsFloat(Object obj, Consumer<String> logger) {
		throw new UnsupportedOperationException();
	}
}
