package net.colors_wind.yamlbox.loader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import net.colors_wind.yamlbox.YamlBox;

/**
 * 通用加载器, 可处理 {@link #ACCEPT_FINAL} 和 {@link enum} 类型.
 * @see {@link #UNIVERSAL}
 * @author colors_wind
 * @date 2020/6/19
 * @since 1.0.0
 */
public class UniversalLoader extends LoaderBase {

	public static final String UNIVERSAL = "universal";
	public static final Set<Class<?>> ACCEPT_FINAL = new HashSet<>();
	static {
		ACCEPT_FINAL.addAll(Arrays.asList(boolean.class, Boolean.class));
		ACCEPT_FINAL.addAll(Arrays.asList(int.class, Integer.class, OptionalInt.class));
		ACCEPT_FINAL.addAll(Arrays.asList(long.class, Long.class, OptionalLong.class));
		ACCEPT_FINAL.addAll(Arrays.asList(double.class, Double.class, OptionalDouble.class));
	}
	
	public UniversalLoader(YamlBox yamlBox) {
		super(yamlBox, UNIVERSAL);
	}
	
	protected UniversalLoader(YamlBox yamlBox, String uniqueName) {
		super(yamlBox, uniqueName);
	}


	@Getter
	@Setter
	private boolean strictMode = false;

	@Override
	public boolean canAccept(Class<?> clazz) {
		return ACCEPT_FINAL.contains(clazz) || clazz.isEnum();
	}

	@Override
	public Object resolve(Class<?> clazz, Type genericType, Object obj, String path) {
		if (Object.class == clazz) {
			return obj;
		} else if (OptionalInt.class == clazz) {
			try {
				return OptionalInt.of(resolveAsInt(obj, path));
			} catch (NumberFormatException e) {
			}
			return OptionalDouble.empty();
		} else if (OptionalDouble.class == clazz) {
			try {
				return OptionalDouble.of(resolveAsDouble(obj, path));
			} catch (NumberFormatException e) {
			}
			return OptionalDouble.empty();
		} else if (OptionalLong.class == clazz) {
			try {
				return OptionalLong.of(resolveAsLong(obj, path));
			} catch (NumberFormatException e) {
			}
			return OptionalLong.empty();
		} else if (obj == null) {
			throw new NullPointerException("CANNOT resolve (obj = NULL)");
		} else if (boolean.class == clazz || Boolean.class == clazz) {
			return resolveAsBoolean(obj, path);
		} else if (int.class == clazz || Integer.class == clazz) {
			return resolveAsInt(obj, path);
		} else if (double.class == clazz || Double.class == clazz) {
			return resolveAsDouble(obj, path);
		} else if (float.class == clazz || Float.class == clazz) {
			return resolveAsFloat(obj, path);
		} else if (long.class == clazz || Long.class == clazz) {
			return resolveAsLong(obj, path);
		} else if (String.class == clazz) {
			return obj.toString();
		} else if (Optional.class == clazz) {
			return resolveAsOptional(obj, path, genericType);
		} else if (List.class == clazz) {
			return resolveAsList(obj, path, genericType);
		} else if (Map.class == clazz) {
			return resolveAsMap(obj, path, genericType);
		} else if (clazz.isEnum()) {
			return resolveAsEnum(obj, path, clazz);
		}
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Object store(Class<?> clazz, Type genericType, Object obj, String path) throws Exception {
		if (OptionalInt.class == clazz) {
			OptionalInt opt = (OptionalInt) obj;
			return opt.isPresent() ? Integer.valueOf(opt.getAsInt()) : null;
		} else if (OptionalDouble.class == clazz) {
			OptionalDouble opt = (OptionalDouble) obj;
			return opt.isPresent() ? Double.valueOf(opt.getAsDouble()) : null;
		} else if (OptionalLong.class == clazz) {
			OptionalLong opt = (OptionalLong) obj;
			return opt.isPresent() ? Long.valueOf(opt.getAsLong()) : null;
		} else if (Optional.class == clazz) {
			Optional<?> opt = (Optional<?>) obj;
			if (opt.isPresent()) {
				Object real = opt.get();
				return store(real.getClass(), null, real, path);
			}
			return null;
		} else if (List.class.isAssignableFrom(clazz)) {
			List<?> list = (List<?>) obj;
			if (list.isEmpty()) {
				return list;
			} else {
				Object top = list.get(0);
				Class<?> type = top.getClass();
				List<Object> storeList = new ArrayList<>(list.size());
				for(Object real : list) {
					try {
						storeList.add(store(type, null, real, path));
					} catch (Exception e) {
						e.printStackTrace();
						yamlBox.getLogger().warning(path, new StringBuilder("Exception occurs while rewrap a list. ").append(real.toString()).toString());
					}
				}
				return storeList;
			}
		} else if (Map.class.isAssignableFrom(clazz)) {
			Map<?,?> map = (Map<?, ?>) obj;
			if (map.isEmpty()) {
				return map;
			} else {
				Map<Object, Object> storeMap = new LinkedHashMap<>();
				Entry<?, ?> top = map.entrySet().iterator().next();
				Class<?> keyType = top.getKey().getClass();
				Class<?> valueType = top.getValue().getClass();
				for(Entry<?,?> entry : map.entrySet()) {
					try {
						Object k = store(keyType, null, entry.getKey(), path);
						Object v = store(valueType, null, entry.getValue(), path);
						storeMap.put(k, v);
					} catch (Exception e) {
						e.printStackTrace();
						yamlBox.getLogger().warning(path, new StringBuilder("Exception occurs while rewrap a map. ").append(entry.toString()).toString());
					}
				}
				return storeMap;
			}
		} else {
			return Objects.toString(obj);
		}
	}


	public Enum<?> resolveAsEnum(Object obj, String path, Class<?> clazz) {
		if (obj instanceof Number) {
			int index = ((Number) obj).intValue();
			try {
				Method method = clazz.getMethod("values", new Class[0]);
				Enum<?>[] enums = (Enum[]) method.invoke(null, new Object[0]);
				return enums[index];
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		} else {
			try {
				Method method = clazz.getMethod("valueOf", new Class[] {String.class});
				Enum<?> enu = (Enum<?>) method.invoke(null, new Object[] {obj.toString()});
				return enu;
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public Map<Object, Object> resolveAsMap(Object obj, String path, Type genericType) {
		Class<?> clazzKey = String.class;
		Class<?> clazzValue = Object.class;
		if (genericType != null && genericType instanceof ParameterizedType) {
			ParameterizedType type = ((ParameterizedType) genericType);
			Type realKey = type.getActualTypeArguments()[0];
			Type realValue = type.getActualTypeArguments()[1];
			clazzKey = (Class<?>) realKey;
			clazzValue = (Class<?>) realValue;
		}
		if (obj == null) {
			return new LinkedHashMap<>();
		} else if (obj instanceof Map) {
			Map<Object, Object> map = new LinkedHashMap<>();
			for(Entry<?, ?> entry : ((Map<?,?>) obj).entrySet()) {
				Object key = resolve(clazzKey, clazzKey, entry.getKey(), path);
				Object value = resolve(clazzValue, clazzValue, entry.getValue(), path);
				map.put(key, value);
			}
			return map;
		} else {
			Map<Object, Object> map = new LinkedHashMap<>();
			Object value = resolve(clazzValue, clazzValue, obj, path);
			map.put("", value);
			return map;
		}
	}

	public List<?> resolveAsList(Object obj, String path, Type genericType) {
		Class<?> clazz;
		if (genericType != null && genericType instanceof ParameterizedType) {
			Type real = ((ParameterizedType) genericType).getActualTypeArguments()[0];
			clazz = (Class<?>) real;
		} else {
			clazz = Object.class;
		}
		if (obj == null) {
			return new ArrayList<>();
		} else if (obj instanceof List) {
			return ((List<?>) obj).stream().map(element -> resolve(clazz, null, element, path))
					.collect(Collectors.toCollection(ArrayList::new));
		} else {
			List<Object> list = new ArrayList<>();
			list.add(resolve(clazz, null, obj, path));
			return list;

		}
	}

	public Optional<?> resolveAsOptional(Object obj, String path, Type genericType) {
		if (genericType == null) {
			return Optional.of(obj);
		}
		if (genericType instanceof ParameterizedType) {
			Type real = ((ParameterizedType) genericType).getActualTypeArguments()[0];
			Class<?> clazz = (Class<?>) real;
			Object realObj = resolve(clazz, null, obj, path);
			return Optional.ofNullable(realObj);
		}
		return Optional.ofNullable(obj);
	}

	@Override
	public int resolveAsInt(Object obj, String path) {
		if (obj instanceof Number) {
			return ((Number) obj).intValue();
		}
		return Integer.parseInt(obj.toString());
	}
	
	@Override
	public short resolveAsShort(Object obj, String path) {
		if (obj instanceof Number) {
			return ((Number) obj).shortValue();
		}
		return Short.parseShort(obj.toString());
	}
	
	@Override
	public byte resolveAsByte(Object obj, String path) {
		if (obj instanceof Number) {
			return ((Number) obj).byteValue();
		}
		return Byte.parseByte(obj.toString());
	}

	@Override
	public boolean resolveAsBoolean(Object obj, String path) {
		if (obj instanceof Boolean) {
			return ((Boolean) obj).booleanValue();
		}
		return Boolean.parseBoolean(obj.toString());
	}

	@Override
	public long resolveAsLong(Object obj, String path) {
		if (obj instanceof Number) {
			return ((Number) obj).longValue();
		}
		return Long.parseLong(obj.toString());
	}

	@Override
	public double resolveAsDouble(Object obj, String path) {
		if (obj instanceof Number) {
			return ((Number) obj).doubleValue();
		}
		return Double.parseDouble(obj.toString());
	}

	@Override
	public float resolveAsFloat(Object obj, String path) {
		if (obj instanceof Number) {
			return ((Number) obj).floatValue();
		}
		return Float.parseFloat(obj.toString());
	}


}
