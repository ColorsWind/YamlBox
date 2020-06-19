package net.colors_wind.yamlbox.conf;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * YAML配置对象节点.
 * @author colors_wind
 * @date 2020/6/19
 * @since 1.0.0
 */
public class YamlConfig extends ConfigSection {
	
	/**
	 * 创建一个空的 {@link YamlConfig}.
	 */
	public YamlConfig() {
		super(null, new LinkedHashMap<>(), "");
	}

	/**
	 * 由一个映射创建 {@link YamlConfig}.
	 */
	public YamlConfig(Map<String, Object> elements) {
		super(null, elements, "");
	}
	
	/**
	 * 创建一个空的 {@link YamlConfig}, 并指定根节点路径.
	 */
	public YamlConfig(String root) {
		super(null, new LinkedHashMap<>(), root);
	}

	/**
	 * 由一个映射创建 {@link YamlConfig}, 并指定根节点路径.
	 */
	public YamlConfig(Map<String, Object> elements, String root) {
		super(null, elements, root);
	}	
	



}
