package net.colors_wind.yamlbox.conf;

import java.util.LinkedHashMap;
import java.util.Map;


public class YamlConfig extends ConfigSection {
	
	public YamlConfig() {
		super(null, new LinkedHashMap<>(), "");
	}

	public YamlConfig(Map<String, Object> elements) {
		super(null, elements, "");
	}
	
	public YamlConfig(String root) {
		super(null, new LinkedHashMap<>(), root);
	}

	public YamlConfig(Map<String, Object> elements, String root) {
		super(null, elements, root);
	}	
	



}
