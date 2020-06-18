package net.colors_wind.yamlbox;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import org.yaml.snakeyaml.Yaml;

import lombok.Getter;
import net.colors_wind.yamlbox.conf.YamlConfig;
import net.colors_wind.yamlbox.loader.EntryLoader;
import net.colors_wind.yamlbox.loader.LoaderBase;
import net.colors_wind.yamlbox.loader.UniversalLoader;
import net.colors_wind.yamlbox.tag.YamlSerializable;

public class YamlBox {
	protected final Yaml yaml;
	protected final Map<String, LoaderBase> loaders;
	@Getter
	protected final ILogger logger;

	public YamlBox(ILogger logger) {
		this.logger = logger;
		this.yaml = new Yaml();
		this.loaders = new HashMap<>();
		this.loaders.put(UniversalLoader.UNIVERSAL, new UniversalLoader(this));
		this.loaders.put(EntryLoader.ENTRY, new EntryLoader(this));
	}
	
	public YamlBox(Logger logger) {
		this((level, path, msg) -> {
			logger.log(level, new StringBuilder(path).append(": ").append(msg).toString());
		});
	}

	public boolean addLoader(String name, LoaderBase resolver) {
		return this.loaders.putIfAbsent(name, resolver) == null;
	}

	public boolean forceAddLoader(String name, LoaderBase resolver) {
		return this.loaders.put(name, resolver) == null;
	}

	public Optional<LoaderBase> getLoader(String name) {
		return Optional.ofNullable(this.loaders.get(name));
	}

	public LoaderBase getLoader(String name, Class<?> clazz, String key) {
		return getLoader(name).orElseGet(() -> {
			LoaderBase resolver = getDefaultResolver(clazz);
			logger.warning(key, new StringBuilder("CANNOT find resolver\" ").append(name).append(" \", using ")
					.append(resolver).append(" instead.").toString());
			return resolver;
		});
	}

	public boolean removeLoader(String name) {
		return this.loaders.remove(name) == null;
	}

	public YamlConfig load(String yamlString) {
		return load(yamlString, "");
	}
	
	public YamlConfig load(String yamlString, String root) {
		Map<String, Object> map = yaml.load(yamlString);
		YamlConfig yamlConfig = new YamlConfig(map, root);
		return yamlConfig;
	}

	public YamlConfig load(Reader reader) {
		return load(reader, "");
	}
	
	public YamlConfig load(Reader reader, String root) {
		Map<String, Object> map = yaml.load(reader);
		YamlConfig yamlConfig = new YamlConfig(map, root);
		return yamlConfig;
	}


	public void dump(YamlConfig config, Writer writer) throws IOException {
		String yamlString = yaml.dumpAsMap(config);
		writer.write(yamlString);
	}

	public String dumpAsString(YamlConfig config) {
		return yaml.dumpAsMap(config);
	}

	public LoaderBase getDefaultResolver() {
		return getDefaultResolver(Object.class);
	}

	public LoaderBase getDefaultResolver(Class<?> clazz) {
		Optional<LoaderBase> option = YamlSerializable.class.isAssignableFrom(clazz)
				? getLoader(EntryLoader.ENTRY)
				: getLoader(UniversalLoader.UNIVERSAL);
		return option.orElseThrow(() -> new NullPointerException("CANNOT determine default resolver."));
	}

}
