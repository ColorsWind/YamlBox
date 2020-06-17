package net.colors_wind.yamlbox;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ConfigObject {

	protected final ConfigSection parent;
	protected final String cachePath;
	protected final Object wrap;

	public ConfigObject(ConfigSection parent, String cachePath, Object wrap) {
		this.parent = parent;
		this.cachePath = cachePath;
		this.wrap = wrap;
	}

	public ConfigSection getParent() {
		return parent;
	}

	public String getPath() {
		return cachePath;
	}

	public boolean isEmpty() {
		return wrap == null;
	}

	public boolean isMap() {
		return !isEmpty() && wrap instanceof Map;
	}

	public boolean isList() {
		return !isEmpty() && wrap instanceof List;
	}

	@SuppressWarnings("unchecked")
	public ConfigSection asSection() {
		if (!isMap()) {
			throw new ClassCastException(
					new StringBuilder("wrap = ").append(Objects.toString(wrap)).append(" is NOT a map.").toString());
		}
		return new ConfigSection(parent, (Map<String, Object>) wrap, cachePath);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T asAny() {
		return (T) wrap;
	}
	
	public Map<?,?> asMap() {
		return asAny();
	}
	
	public List<?> asList() {
		return asAny();
	}

	public Object getReal() {
		return wrap;
	}

}
