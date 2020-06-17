package net.colors_wind.yamlbox.resolve;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import net.colors_wind.yamlbox.ConfigSection;
import net.colors_wind.yamlbox.YamlBox;

public class EntryResolver extends ResolverBase {

	public static final String ENTRY = "entry";
	public static final IFieldSelector SELECTOR_PUBLIC = clazz -> {
		Field[] fields = clazz.getFields();
		ArrayList<Field> list = new ArrayList<>(fields.length);
		for (Field field : fields) {
			field.setAccessible(true);
			if (!Modifier.isStatic(field.getModifiers())) {
				list.add(field);
			}
		}
		return list.toArray(new Field[list.size()]);
	};

	public static final IFieldSelector SELECTOR_DECLARE = clazz -> {
		Field[] fields = clazz.getDeclaredFields();
		ArrayList<Field> list = new ArrayList<>();
		do {
			for (Field field : fields) {
				field.setAccessible(true);
				if (!Modifier.isStatic(field.getModifiers())) {
					list.add(field);
				}
			}
		} while ((clazz = clazz.getSuperclass()) != Object.class);
		return list.toArray(new Field[list.size()]);
	};

	public EntryResolver(YamlBox yamlBox, String uniqueName) {
		super(yamlBox, uniqueName);
	}

	public <T extends YamlSerializable> T resolve(Class<T> clazz, IFieldSelector selector, ConfigSection config,
			String path) throws InstantiationException, IllegalAccessException {
		T instance = clazz.newInstance();
		for (Field field : selector.apply(clazz)) {
			// 预处理, 准备 Logger 和 Resolver
			ConfigNode ann = field.getAnnotation(ConfigNode.class);
			String key; 
			ResolverBase resolver;
			if (ann == null) {
				key = field.getName();
				resolver = yamlBox.getDefaultResolver();
			} else {
				key = ann.path().isEmpty() ? field.getName() : ann.path();
				resolver = yamlBox.getResolver(ann.resolver()).orElseGet(() -> {
					yamlBox.getLogger().warning(key, new StringBuilder("CANNOT find resolver\" ").append(ann.resolver())
							.append(" \", using ").append(UniversalResolver.UNIVERSAL).append(" instead.").toString());
					return yamlBox.getDefaultResolver(clazz);
				});
			}
			Object obj = config.getObjectDeep(key);
			Class<?> fieldType = field.getDeclaringClass();
			String nodePath = new StringBuilder(path).append(key).toString();
			try {
				if (fieldType.isPrimitive()) {
					handlePrimitiveType(field, fieldType, instance, obj, resolver, nodePath);
				} else {
					Object value = resolver.resolve(fieldType, field.getGenericType(), obj, nodePath);
					field.set(instance, value);
				}
			} catch (Exception e) {
				e.printStackTrace();
				yamlBox.getLogger().warning(nodePath, new StringBuilder("Exception occured while processing Field ").append(clazz.getSimpleName()).append(".").append(field.getName()).append(".").toString());
				continue;
			}
		}
		return instance;
	}

	private final void handlePrimitiveType(Field field, Class<?> fieldType, Object instance, Object obj,
			ResolverBase resolver, String path) throws IllegalAccessException {
		if (int.class == fieldType) {
			int i = resolver.resolveAsInt(obj, path);
			field.setInt(instance, i);
		} else if (long.class == fieldType) {
			long l = resolver.resolveAsLong(instance, path);
			field.setLong(instance, l);
		} else if (double.class == fieldType) {
			double d = resolver.resolveAsDouble(instance, path);
			field.setDouble(instance, d);
		} else if (float.class == fieldType) {
			float f = resolver.resolveAsFloat(instance, path);
			field.setFloat(instance, f);
		} else if (boolean.class == fieldType) {
			boolean b = resolver.resolveAsBoolean(instance, path);
			field.setBoolean(instance, b);
		}
		throw new UnsupportedOperationException("Unexpected exception!");
	}

	@Override
	public boolean canAccept(Class<?> clazz) {
		return YamlSerializable.class.isAssignableFrom(clazz);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object resolve(Class<?> clazz, Type genericType, Object obj, String path) throws InstantiationException, IllegalAccessException {
		return resolve((Class<YamlSerializable>) clazz, SELECTOR_PUBLIC, (ConfigSection)obj, path);
	}


}
