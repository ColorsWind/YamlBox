package net.colors_wind.yamlbox;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.NonNull;

public class YamlConfig extends ConfigSection {
	
	public YamlConfig() {
		super(null, new LinkedHashMap<>());
	}

	public YamlConfig(Map<String, Object> elements) {
		super(null, elements);
	}	
	
	@Override
	public ConfigSection getParent() {
		return this;
	}

	public ConfigSection getSection(@NonNull String key) {
		Object obj = elements.get(key);
		if (obj != null && Map.class.isAssignableFrom(obj.getClass())) {
			return new ConfigSection(this, elements);
		}
		return null;
	}


}
