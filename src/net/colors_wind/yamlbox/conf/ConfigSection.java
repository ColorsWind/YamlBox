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
	
	

	public ConfigSection createSection(@NonNull String key) {
		if (elements.containsKey(key)) {
			throw new IllegalArgumentException("CANNOT create section because key has already existed: " + key);
		}
		ConfigSection section = new ConfigSection(this, appendPath(this.cachePath, key));
		elements.put(key, section);
		return section;
	}
	
	public ConfigSection getSection(@NonNull String key) {
		Object obj = this.getObject(key);
		return getSection0(obj, key);
	}
	
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

	public ConfigSection getParent() {
		return parent;
	}

	public String getPath() {
		return cachePath;
	}

	public boolean isSet(@NonNull String key) {
		return elements.containsKey(key);
	}

	public void set(@NonNull String key, Object value) {
		if (value != null && value instanceof ConfigSection) {
			elements.put(key, ((ConfigSection)value).elements);
		}
		elements.put(key, value);
	}

	public Object remove(@NonNull String key) {
		return elements.remove(key);
	}


	public Object getObjectDeep(@NonNull String key) {
		StringTokenizer str = new StringTokenizer(key, DOT);
		Object obj = elements;
		while (str.hasMoreElements() && obj != null && obj instanceof Map) {
			obj = ((Map<?, ?>) obj).get(str.nextElement());
		}
		return obj;
	}

	public Object getObject(@NonNull String key) {
		return elements.get(key);
	}

	public String getString(@NonNull String key) {
		Object obj = getObject(key);
		if (obj instanceof String) {
			return (String) obj;
		}
		return null;
	}

	public Optional<String> getOptionalString(@NonNull String key) {
		return Optional.ofNullable(getString(key));
	}

	public String getAsString(@NonNull String key) {
		return Objects.toString(getString(key));
	}

	public int getInt(@NonNull String key) {
		Object obj = getObject(key);
		if (obj instanceof Integer) {
			return (int) obj;
		}
		return 0;
	}

	public int getAsInt(@NonNull String key) {
		return getOptionalInt(key).orElse(0);
	}

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

	public long getLong(@NonNull String key) {
		Object obj = getObject(key);
		if (obj instanceof Long) {
			return (long) obj;
		}
		return 0L;
	}

	public long getAsLong(@NonNull String key) {
		return getOptionalLong(key).orElse(0L);
	}

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

	public double getDouble(@NonNull String key) {
		Object obj = getObject(key);
		if (obj instanceof Double) {
			return (double) obj;
		}
		return 0D;
	}

	public double getAsDouble(@NonNull String key) {
		return getOptionalDouble(key).orElse(0D);
	}

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

	public boolean getBoolean(@NonNull String key) {
		Object obj = getObject(key);
		if (obj instanceof Boolean) {
			return (boolean) obj;
		}
		return Boolean.parseBoolean(Objects.toString(obj));
	}
}
