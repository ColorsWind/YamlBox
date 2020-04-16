package net.colors_wind.yamlbox;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class YamlBox {
	private final Yaml yaml = new Yaml();
	
	public YamlConfig load(String yamlString) {
		Map<String, Object> map = yaml.load(yamlString);
		YamlConfig yamlConfig = new YamlConfig(map);
		return yamlConfig;
	}
	
	public YamlConfig load(Reader reader) {
		Map<String, Object> map = yaml.load(reader);
		YamlConfig yamlConfig = new YamlConfig(map);
		return yamlConfig;
	}
	
	public void dump(YamlConfig config, Writer writer) throws IOException {
		String yamlString = yaml.dumpAsMap(config);
		writer.write(yamlString);
	}
	
	public String dumpAsString(YamlConfig config) {
		return yaml.dumpAsMap(config);
	}

}
