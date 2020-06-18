package net.colors_wind.yamlbox;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.yaml.snakeyaml.Yaml;

import lombok.Getter;
import net.colors_wind.yamlbox.resolve.EntryResolver;
import net.colors_wind.yamlbox.resolve.ILogger;
import net.colors_wind.yamlbox.resolve.ResolverBase;
import net.colors_wind.yamlbox.resolve.UniversalResolver;
import net.colors_wind.yamlbox.resolve.YamlSerializable;

public class YamlBox {
	protected final Yaml yaml;
	protected final Map<String, ResolverBase> resolvers;
	@Getter
	protected final ILogger logger;
	
	public YamlBox(ILogger logger) {
		this.logger = logger;
		this.yaml = new Yaml();
		this.resolvers = new HashMap<>();
		this.resolvers.put(UniversalResolver.UNIVERSAL, new UniversalResolver(this));
		this.resolvers.put(EntryResolver.ENTRY, new EntryResolver(this));
	}

	public boolean addResolver(String name, ResolverBase resolver) {
		return this.resolvers.putIfAbsent(name, resolver) == null;
	}

	public boolean forceAddResolver(String name, ResolverBase resolver) {
		return this.resolvers.put(name, resolver) == null;
	}

	public Optional<ResolverBase> getResolver(String name) {
		return Optional.of(this.resolvers.get(name));
	}

	public boolean removeResolver(String name) {
		return this.resolvers.remove(name) == null;
	}

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

	public ResolverBase getDefaultResolver() {
		return getDefaultResolver(Object.class);
	}

	public ResolverBase getDefaultResolver(Class<?> clazz) {
		Optional<ResolverBase> option = YamlSerializable.class.isAssignableFrom(clazz) ?
			getResolver(EntryResolver.ENTRY) : getResolver(UniversalResolver.UNIVERSAL);
		return option.orElseThrow(() -> new NullPointerException("CANNOT determine default resolver."));
	}

}
