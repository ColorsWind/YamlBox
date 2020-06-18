package net.colors_wind.yamlbox.resolve;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import lombok.Data;
import net.colors_wind.yamlbox.ConfigSection;
import net.colors_wind.yamlbox.YamlBox;

public abstract class ResolverBase {
	
	protected final YamlBox yamlBox;
	protected final String uniqueName;
	
	public ResolverBase(YamlBox yamlBox, String uniqueName) {
		super();
		this.yamlBox = yamlBox;
		this.uniqueName = uniqueName;
	}
	
	/**
	 * 判断该解析器是否可以解析这个类型
	 * @param clazz
	 * @return
	 */
	public abstract boolean canAccept(Class<?> clazz);
	
	/**
	 * 判断该解析器是否可以解析这个类型
	 * @param clazz
	 * @param genericType
	 * @return
	 */
	public boolean canAccept(Class<?> clazz, Type genericType) {
		return this.canAccept(clazz);
	}
	
	/**
	 * 解析元素
	 * @param clazz
	 * @param genericType
	 * @param obj
	 * @param logger
	 * @return
	 * @throws Exception 
	 */
	public abstract Object resolve(Class<?> clazz, Type genericType, Object obj, String path) throws Exception;
	
	public abstract Object store(Class<?> clazz, Type genericType, Object obj, String path) throws Exception;
	
	public int resolveAsInt(Object obj, String path) {
		throw new UnsupportedOperationException();
	}
	
	public short resolveAsShort(Object obj, String path) {
		throw new UnsupportedOperationException();
	}
	
	public byte resolveAsByte(Object obj, String path) {
		throw new UnsupportedOperationException();
	}
	
	public boolean resolveAsBoolean(Object obj, String path) {
		throw new UnsupportedOperationException();
	}
	
	public long resolveAsLong(Object obj, String path) {
		throw new UnsupportedOperationException();
	}
	
	public double resolveAsDouble(Object obj, String path) {
		throw new UnsupportedOperationException();
	}
	
	public float resolveAsFloat(Object obj, String path) {
		throw new UnsupportedOperationException();
	}
	
	public boolean register() {
		return this.yamlBox.addResolver(uniqueName, this);
	}
	
	public boolean forceRegister() {
		return this.yamlBox.forceAddResolver(uniqueName, this);
	}
	
	
	@Data
	class NodeInf {
		private final String key;
		private final ResolverBase resolver;
		private final IFieldSelector selector;
		
		public String getRealPath(String origin) {
			return new StringBuilder(origin).append(ConfigSection.DOT).append(key).toString();
		}
	}

	public NodeInf getNodeInf(Field field) {
		Class<?> clazz = field.getDeclaringClass();
		SerializeNode sNode = field.getAnnotation(SerializeNode.class);
		ConfigNode cNode = field.getAnnotation(ConfigNode.class);
		String key; 
		ResolverBase resolver;
		IFieldSelector selector;
		if (cNode == null) {
			key = field.getName();
			resolver = yamlBox.getDefaultResolver(clazz);
		} else {
			key = cNode.path().isEmpty() ? field.getName() : cNode.path();
			resolver = yamlBox.getResolver(cNode.resolver()).orElseGet(() -> yamlBox.getDefaultResolver(clazz));
		}
		if (YamlSerializable.class.isAssignableFrom(clazz) && sNode != null) {
			selector = sNode.fieldSelector();
		} else {
			selector = FieldSelector.SELECTOR_PUBLIC;
		}
		return new NodeInf(key, resolver, selector);
	}
	

}
