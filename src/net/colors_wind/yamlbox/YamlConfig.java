package net.colors_wind.yamlbox;

import java.util.LinkedHashMap;
import java.util.Map;


public class YamlConfig extends ConfigSection {
	
	public YamlConfig() {
		super(null, new LinkedHashMap<>(), "");
	}

	public YamlConfig(Map<String, Object> elements) {
		super(null, elements, "");
	}	
	



}
