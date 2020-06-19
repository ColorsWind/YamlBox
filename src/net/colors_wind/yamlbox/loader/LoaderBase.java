package net.colors_wind.yamlbox.loader;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import lombok.Data;
import net.colors_wind.yamlbox.YamlBox;
import net.colors_wind.yamlbox.conf.ConfigSection;
import net.colors_wind.yamlbox.tag.ConfigNode;
import net.colors_wind.yamlbox.tag.SerializeNode;
import net.colors_wind.yamlbox.tag.YamlSerializable;

/**
 * 序列化/反序列化加载器基类.
 * @author colors_wind
 * @date 2020/6/19
 * @since 1.0.0
 */
public abstract class LoaderBase {
	
	protected final YamlBox yamlBox;
	protected final String uniqueName;
	
	/**
	 * 构造一个加载器.
	 * @see {@link #register}
	 * @see {@link #forceRegister}
	 * @param yamlBox 所属YamlBox实例
	 * @param uniqueName 唯一标识名
	 */
	public LoaderBase(YamlBox yamlBox, String uniqueName) {
		super();
		this.yamlBox = yamlBox;
		this.uniqueName = uniqueName;
	}
	
	/**
	 * 判断该加载器是否可以解析这个类型.
	 * @param clazz 类型
	 * @return {@code true} 如果可以加载, 否则返回 {@code false}.
	 */
	public abstract boolean canAccept(Class<?> clazz);
	
	/**
	 * 判断该加载器是否可以解析这个类型.
	 * @param clazz 类型
	 * @param genericType 泛型类型
	 * @return {@code true} 如果可以加载, 否则返回 {@code false}.
	 */
	public boolean canAccept(Class<?> clazz, Type genericType) {
		return this.canAccept(clazz);
	}
	
	/**
	 * 反序列化元素.
	 * @param clazz 类型
	 * @param genericType 泛型类型
	 * @param obj 原始数据
	 * @param path 数据所在的Yaml节点(绝对路径)
	 * @return 反序列化结构
	 * @throws Exception 如果反序列化过程发生异常
	 */
	public abstract Object resolve(Class<?> clazz, Type genericType, Object obj, String path) throws Exception;
	
	/**
	 * 序列化元素.
	 * @param clazz 类型
	 * @param genericType 泛型类型
	 * @param obj 原始数据
	 * @param path 数据所在的Yaml节点(绝对路径)
	 * @return 序列化结构
	 * @throws Exception 如果序列化过程发生异常
	 */
	public abstract Object store(Class<?> clazz, Type genericType, Object obj, String path) throws Exception;
	
	/**
	 * 已经数据代表 {@link int}, 反序列化元素.
	 * @param obj 原始数据
	 * @param path 数据所在的Yaml节点(绝对路径)
	 * @throws Exception 如果反序列化过程发生异常
	 * @see {@link #resolve(Class, Type, Object, String)}
	 * @see {@link #resolveAsLong(Object, String)}
	 * @see {@link #resolveAsDouble(Object, String)}
	 */
	public int resolveAsInt(Object obj, String path) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 已经数据代表 {@link short}, 反序列化元素.
	 * @param obj 原始数据
	 * @param path 数据所在的Yaml节点(绝对路径)
	 * @throws Exception 如果反序列化过程发生异常
	 * @see {@link #resolve(Class, Type, Object, String)}
	 * @see {@link #resolveAsInt(Object, String)}
	 * @see {@link #resolveAsLong(Object, String)}
	 */
	public short resolveAsShort(Object obj, String path) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 已经数据代表 {@link byte}, 反序列化元素.
	 * @param obj 原始数据
	 * @param path 数据所在的Yaml节点(绝对路径)
	 * @throws Exception 如果反序列化过程发生异常
	 * @see {@link #resolve(Class, Type, Object, String)}
	 * @see {@link #resolveAsInt(Object, String)}
	 * @see {@link #resolveAsLong(Object, String)}
	 */
	public byte resolveAsByte(Object obj, String path) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 已经数据代表 {@link boolean}, 反序列化元素.
	 * @param obj 原始数据
	 * @param path 数据所在的Yaml节点(绝对路径)
	 * @throws Exception 如果反序列化过程发生异常
	 * @see {@link #resolve(Class, Type, Object, String)}
	 */
	public boolean resolveAsBoolean(Object obj, String path) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 已经数据代表 {@link long}, 反序列化元素.
	 * @param obj 原始数据
	 * @param path 数据所在的Yaml节点(绝对路径)
	 * @throws Exception 如果反序列化过程发生异常
	 * @see {@link #resolve(Class, Type, Object, String)}
	 * @see {@link #resolveAsInt(Object, String)}
	 * @see {@link #resolveAsDouble(Object, String)}
	 */
	public long resolveAsLong(Object obj, String path) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 已经数据代表 {@link double}, 反序列化元素.
	 * @param obj 原始数据
	 * @param path 数据所在的Yaml节点(绝对路径)
	 * @throws Exception 如果反序列化过程发生异常
	 * @see {@link #resolve(Class, Type, Object, String)}
	 * @see {@link #resolveAsFloat(Object, String)}
	 * @see {@link #resolveAsLong(Object, String)}
	 */
	public double resolveAsDouble(Object obj, String path) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 已经数据代表 {@link float}, 反序列化元素.
	 * @param obj 原始数据
	 * @param path 数据所在的Yaml节点(绝对路径)
	 * @throws Exception 如果反序列化过程发生异常
	 * @see {@link #resolve(Class, Type, Object, String)}
	 * @see {@link #resolveAsDouble(Object, String)}
	 * @see {@link #resolveAsLong(Object, String)}
	 */
	public float resolveAsFloat(Object obj, String path) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 向所属YamlBox注册加载器, 若已存在同名加载器, 则注册失败.
	 * @return {@code true} 如果注册成功, 否则返回 {@code false}.
	 * @see {@link YamlBox#addLoader(String, LoaderBase)}
	 */
	public boolean register() {
		return this.yamlBox.addLoader(uniqueName, this);
	}
	
	/**
	 * 向所属YamlBox注册加载器, 若已存在同名加载器, 则覆盖.
	 * @return {@code true} 如果没有覆盖任何加载器, 否则返回 {@code false}.
	 * @see {@link YamlBox#forceAddLoader(String, LoaderBase)}
	 */
	public boolean forceRegister() {
		return this.yamlBox.forceAddLoader(uniqueName, this);
	}
	
	
	/**
	 * 代表综合分析字段标记的结果.
	 * @author colors_wind
	 * @date 2020/6/19
	 * @since 1.0.0
	 */
	@Data
	class NodeInf {
		private final String key;
		private final LoaderBase loader;
		private final IFieldSelector selector;
		
		/**
		 * 获取字段对应的Yaml节点(绝对路径).
		 * @param origin 上级节点的路径
		 * @return 绝对路径
		 */
		public String getRealPath(String origin) {
			return new StringBuilder(origin).append(ConfigSection.DOT).append(key).toString();
		}
	}

	/**
	 * 分析字段标记.
	 * @param field 字段
	 * @return 分析结构
	 */
	public NodeInf getNodeInf(Field field) {
		Class<?> clazz = field.getDeclaringClass();
		SerializeNode sNode = field.getAnnotation(SerializeNode.class);
		ConfigNode cNode = field.getAnnotation(ConfigNode.class);
		String key; 
		LoaderBase resolver;
		IFieldSelector selector;
		if (cNode == null) {
			key = field.getName();
			resolver = yamlBox.getDefaultResolver(clazz);
		} else {
			key = cNode.path().isEmpty() ? field.getName() : cNode.path();
			resolver = yamlBox.getLoader(cNode.loader()).orElseGet(() -> yamlBox.getDefaultResolver(clazz));
		}
		if (YamlSerializable.class.isAssignableFrom(clazz)) {
			if (sNode == null) {
				sNode = clazz.getAnnotation(SerializeNode.class);
			}
		}
		if (sNode == null) {
			selector = FieldSelector.SELECTOR_PUBLIC;
		} else {
			selector = sNode.fieldSelector();	
		}
		return new NodeInf(key, resolver, selector);
	}
	

}
