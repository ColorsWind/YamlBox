package net.colors_wind.yamlbox.loader;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.colors_wind.yamlbox.YamlBox;
import net.colors_wind.yamlbox.conf.ConfigSection;
import net.colors_wind.yamlbox.tag.SerializeNode;
import net.colors_wind.yamlbox.tag.YamlSerializable;

public class EntryLoader extends LoaderBase {

	public static final String ENTRY = "entry";
	public static final Set<Class<?>> DIRECT_FINAL = new HashSet<>();
	static {
		DIRECT_FINAL.addAll(Arrays.asList(boolean.class, Boolean.class));
		DIRECT_FINAL.addAll(Arrays.asList(byte.class, Byte.class));
		DIRECT_FINAL.addAll(Arrays.asList(short.class, Short.class));
		DIRECT_FINAL.addAll(Arrays.asList(int.class, Integer.class));
		DIRECT_FINAL.addAll(Arrays.asList(long.class, Long.class));
		DIRECT_FINAL.addAll(Arrays.asList(double.class, Double.class));
		DIRECT_FINAL.addAll(Arrays.asList(float.class, Float.class));
		DIRECT_FINAL.add(String.class);
	}

	public EntryLoader(YamlBox yamlBox) {
		super(yamlBox, ENTRY);
	}

	protected EntryLoader(YamlBox yamlBox, String uniqueName) {
		super(yamlBox, uniqueName);
	}

	public <T extends YamlSerializable> T resolve(Class<T> clazz, IFieldSelector selector, ConfigSection config,
			String path) throws InstantiationException, IllegalAccessException {
		T instance = clazz.newInstance();
		for (Field field : selector.apply(clazz)) {
			NodeInf inf = this.getNodeInf(field);
			Class<?> fieldType = field.getType();
			LoaderBase loader = inf.getLoader();
			Object obj = config.getObjectDeep(inf.getKey());
			try {
				if (loader instanceof EntryLoader) {
					((EntryLoader) loader).resolve(clazz, inf.getSelector(), config.getSectionDeep(path),
							inf.getRealPath(path));
				} else {
					if (fieldType.isPrimitive()) {
						handlePrimitiveType(field, fieldType, instance, obj, loader, inf.getRealPath(path));
					} else {
						Object value = loader.resolve(fieldType, field.getGenericType(), obj, inf.getRealPath(path));
						field.set(instance, value);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				yamlBox.getLogger().warning(inf.getRealPath(path),
						new StringBuilder("Exception occured while processing Field ").append(clazz.getSimpleName())
								.append(".").append(field.getName()).append(".").toString());
				continue;
			}
		}
		return instance;
	}

	private final void handlePrimitiveType(Field field, Class<?> fieldType, Object instance, Object obj,
			LoaderBase resolver, String path) throws IllegalAccessException {
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
		throw new UnsupportedOperationException("Unexpected primitive type!");
	}

	public <T extends YamlSerializable> Map<String, Object> store(Class<T> clazz, IFieldSelector selector, T instance,
			String path) throws InstantiationException, IllegalAccessException {
		Map<String, Object> storeMap = new LinkedHashMap<>();
		for (Field field : selector.apply(clazz)) {
			Object obj = field.get(instance);
			Class<?> type = obj.getClass();
			Type genericType = field.getGenericType();
			NodeInf inf = this.getNodeInf(field);
			LoaderBase loader = inf.getLoader();
			try {
				if (loader instanceof EntryLoader) {
					EntryLoader entryLoader = (EntryLoader) loader;
					@SuppressWarnings("unchecked")
					Class<YamlSerializable> entryType = (Class<YamlSerializable>) type;
					Map<String, Object> map = entryLoader.store(entryType, inf.getSelector(), (YamlSerializable) obj,
							path);
					storeMap.put(inf.getKey(), map);
				} else {
					Object real = inf.getLoader().store(type, genericType, obj, inf.getRealPath(path));
					storeMap.put(inf.getKey(), real);
				}
			} catch (Exception e) {
				e.printStackTrace();
				yamlBox.getLogger().warning(inf.getRealPath(path),
						new StringBuilder("Exception occurs while storing YamlSerializable. ").append(obj.toString())
								.toString());
				continue;
			}

		}
		return storeMap;
	}

	@Override
	public boolean canAccept(Class<?> clazz) {
		return YamlSerializable.class.isAssignableFrom(clazz);
	}

	public IFieldSelector getFieldSelector(Class<?> clazz) {
		SerializeNode ann = clazz.getAnnotation(SerializeNode.class);
		IFieldSelector selector;
		if (ann != null) {
			selector = ann.fieldSelector();
		} else {
			selector = FieldSelector.SELECTOR_PUBLIC;
		}
		return selector;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object resolve(Class<?> clazz, Type genericType, Object obj, String path)
			throws InstantiationException, IllegalAccessException {
		return resolve((Class<YamlSerializable>) clazz, getFieldSelector(clazz), (ConfigSection) obj, path);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> store(Class<?> clazz, Type genericType, Object obj, String path) throws Exception {
		return store((Class<YamlSerializable>) clazz, getFieldSelector(clazz), (YamlSerializable) obj, path);
	}

}
