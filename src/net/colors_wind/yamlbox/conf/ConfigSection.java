package net.colors_wind.yamlbox.conf;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.StringTokenizer;

import lombok.NonNull;

/**
 * YAML配置对象基类, 对应 {@link Map}.
 * @author colors_wind
 * @date 2020/6/19
 * @since 1.0.0
 */
public class ConfigSection {
	public static final String DOT = ".";
	public static String appendPath(String origin, String key) {
		return new StringBuilder().append(origin).append(DOT).append(key).toString();
	}
	protected final ConfigSection parent;
	protected final Map<String, Object> elements;
	protected final String cachePath;

	protected ConfigSection(ConfigSection parent, String currentNode) {
		this(parent, new LinkedHashMap<>(), currentNode);
	}

	protected ConfigSection(ConfigSection parent, Map<String, Object> elements, String path) {
		this.parent = parent;
		this.elements = elements;
		this.cachePath = path;
	}
	
	
	/**
	 * 创建一个配置文件子对象.
	 * @param key 相对路径
	 * @return 新建子对象
	 */
	public ConfigSection createSection(@NonNull String key) {
		if (elements.containsKey(key)) {
			throw new IllegalArgumentException("CANNOT create section because key has already existed: " + key);
		}
		ConfigSection section = new ConfigSection(this, appendPath(this.cachePath, key));
		elements.put(key, section);
		return section;
	}
	
	/**
	 * 根据相对路径获取子对象.
	 * @param key 相对路径
	 * @return 子对象, 若找不到返回 {@code null}.
	 */
	public ConfigSection getSection(@NonNull String key) {
		Object obj = this.getObject(key);
		return getSection0(obj, key);
	}
	
	/**
	 * 根据多级相对路径获取子对象.
	 * @param key 相对路径, 用 {@link #DOT} 分隔.
	 * @return 子对象, 若找不到返回 {@code null}.
	 */
	public ConfigSection getSectionDeep(@NonNull String key) {
		Object obj = this.getObjectDeep(key);
		return getSection0(obj, key);
	}
	
	private ConfigSection getSection0(Object obj, String key) {
		if (obj != null && Map.class.isAssignableFrom(obj.getClass())) {
			return new ConfigSection(this, elements, key);
		}
		return null;
	}

	/**
	 * 获取当前配置对象的父对象.
	 * @return 父对象, 若当前对象是根节点, 返回 {@code null}.
	 */
	public ConfigSection getParent() {
		return parent;
	}

	/**
	 * 获取当前对象的绝对路径.
	 * @return 绝对路径
	 */
	public String getPath() {
		return cachePath;
	}

	/**
	 * 配置指定相对路径的对象是否存在.
	 * @param key 相对路径.
	 * @return {@code true} 如果存在, 否则返回 {@code false}.
	 */
	public boolean isSet(@NonNull String key) {
		return elements.containsKey(key);
	}

	/**
	 * 将指定对象放到指定相对路径.
	 * @param key 相对路径
	 * @param value 对象
	 * @return 该位置原来的对象, 若不存在, 返回 {@code null}.
	 */
	public Object set(@NonNull String key, Object value) {
		if (value != null && value instanceof ConfigSection) {
			return elements.put(key, ((ConfigSection)value).elements);
		}
		return elements.put(key, value);
	}

	/**
	 * 移除指定配置的对象.
	 * @param key 相对路径.
	 * @return 移除的对象, 若不存在, 返回 {@code null}.
	 */
	public Object remove(@NonNull String key) {
		return elements.remove(key);
	}


	/**
	 * 获取指定位置的对象
	 * @param key 多级相对路径
	 * @return 该位置的对象, 若不存在, 返回 {@code null}.
	 * @see {@link ConfigSection#getObject(String)}
	 */
	public Object getObjectDeep(@NonNull String key) {
		StringTokenizer str = new StringTokenizer(key, DOT);
		Object obj = elements;
		while (str.hasMoreElements() && obj != null && obj instanceof Map) {
			obj = ((Map<?, ?>) obj).get(str.nextElement());
		}
		return obj;
	}

	/**
	 * 获取指定位置的对象
	 * @param key 相对路径
	 * @return 该位置的对象, 若该对象不存在, 返回 {@code null}.
	 * @see {@link ConfigSection#getObjectDeep(String)}
	 */
	public Object getObject(@NonNull String key) {
		return elements.get(key);
	}

	/**
	 * 获取指定位置的字符串.
	 * @param key 相对路径
	 * @return 该位置的字符串, 若该对象不存在, 或不是字符串, 返回 {@code null}.
	 */
	public String getString(@NonNull String key) {
		Object obj = getObject(key);
		if (obj instanceof String) {
			return (String) obj;
		}
		return null;
	}

	/**
	 * 获取指定位置的字符串, 并包装.
	 * @param key 相对路径
	 * @return 包装后字符串, 若该位置的对象不存在, 或不是字符串, 返回 {@link Optional#empty()}}.
	 */
	public Optional<String> getOptionalString(@NonNull String key) {
		return Optional.ofNullable(getString(key));
	}

	/**
	 * 将该位置的对象以字符串形式返回.
	 * @param key 相对路径
	 * @return 字符串
	 * @see {@link Objects#toString()}
	 */
	public String getAsString(@NonNull String key) {
		return Objects.toString(getString(key));
	}

	/*
	 * 获取指定位置的整形数.
	 * @param key 相对路径
	 * @return 该位置的整形数, 若该对象不存在, 或不是(长)整形数, 或发生溢出, 返回 {@code 0L}.
	 */
	public int getInt(@NonNull String key) {
		Object obj = getObject(key);
		if (obj instanceof Integer) {
			return ((Integer)obj).intValue();
		} else if (obj instanceof Long) {
			long l = (long) obj;
			if (l < Integer.MAX_VALUE && l > Integer.MIN_VALUE) {
				return ((Long)l).intValue();
			}
		}
		return 0;
	}
	
	/**
	 * 获取指定位置的整形数, 并包装.
	 * @param key 相对路径
	 * @return 包装后整形数, 若该位置的对象不存在, 或不是无法转化为整形数, 返回 {@link OptionalInt#empty()}.
	 */
	public OptionalInt getOptionalInt(@NonNull String key) {
		Object obj = getObject(key);
		if (obj == null) {
			return OptionalInt.empty();
		}
		if (obj instanceof Number) {
			return OptionalInt.of(((Number) obj).intValue());
		}
		try {
			return OptionalInt.of(Integer.parseInt(obj.toString()));
		} catch (NumberFormatException e) {
		}
		return OptionalInt.empty();
	}

	/**
	 * 将该位置的对象以整形数形式返回.
	 * @param key 相对路径
	 * @return 整形数, 若无法转化位整形数, 返回 {@code 0}.
	 */
	public int getAsInt(@NonNull String key) {
		return getOptionalInt(key).orElse(0);
	}

	/**
	 * 获取指定位置的长整形数.
	 * @param key 相对路径
	 * @return 该位置的长整形数, 若该对象不存在, 或不是(长)整形数, 返回 {@code 0L}.
	 */
	public long getLong(@NonNull String key) {
		Object obj = getObject(key);
		if (obj instanceof Long || obj instanceof Integer) {
			return ((Number)obj).longValue();
		}
		return 0L;
	}

	/**
	 * 将该位置的对象以长整形数形式返回.
	 * @param key 相对路径
	 * @return 长整形数, 若无法转化位长整形数, 返回 {@code 0L}.
	 */
	public long getAsLong(@NonNull String key) {
		return getOptionalLong(key).orElse(0L);
	}

	/**
	 * 获取指定位置的长整形数, 并包装.
	 * @param key 相对路径
	 * @return 包装后的长整形数, 若该位置的对象不存在, 或不是无法转化为长整形数, 返回 {@link OptionalLong#empty()}.
	 */
	public OptionalLong getOptionalLong(@NonNull String key) {
		Object obj = getObject(key);
		if (obj == null) {
			return OptionalLong.empty();
		}
		if (obj instanceof Number) {
			return OptionalLong.of(((Number) obj).intValue());
		}
		try {
			return OptionalLong.of(Long.parseLong(obj.toString()));
		} catch (NumberFormatException e) {
		}
		return OptionalLong.empty();
	}

	/**
	 * 获取指定位置的双精度浮点数.
	 * @param key 相对路径
	 * @return 该位置的双精度浮点数, 若该对象不存在, 或不是数, 返回 {@code 0.0D}.
	 */
	public double getDouble(@NonNull String key) {
		Object obj = getObject(key);
		if (obj instanceof Number) {
			return ((Number)obj).doubleValue();
		}
		return 0D;
	}

	/**
	 * 将该位置的对象以双精度浮点数形式返回.
	 * @param key 相对路径
	 * @return 双精度浮点数, 若无法转化位双精度浮点数, 返回 {@code 0.0D}.
	 */
	public double getAsDouble(@NonNull String key) {
		return getOptionalDouble(key).orElse(0D);
	}

	/**
	 * 获取指定位置的双精度浮点数, 并包装.
	 * @param key 相对路径
	 * @return 包装后的双精度浮点数, 若该位置的对象不存在, 或不是无法转化为双精度浮点数, 返回 {@link OptionalDouble#empty()}.
	 */
	public OptionalDouble getOptionalDouble(@NonNull String key) {
		Object obj = getObject(key);
		if (obj == null) {
			return OptionalDouble.empty();
		}
		if (obj instanceof Number) {
			return OptionalDouble.of(((Number) obj).intValue());
		}
		try {
			return OptionalDouble.of(Double.parseDouble(obj.toString()));
		} catch (NumberFormatException e) {
		}
		return OptionalDouble.empty();
	}

	/**
	 * 获取指定位置的布尔值.
	 * @param key 相对位置
	 * @return 指定位置的布尔值, 若该位置对象无法转换为布尔值, 返回 {@code false}.
	 */
	public boolean getBoolean(@NonNull String key) {
		Object obj = getObject(key);
		if (obj instanceof Boolean) {
			return (boolean) obj;
		}
		return Boolean.parseBoolean(Objects.toString(obj));
	}
}
