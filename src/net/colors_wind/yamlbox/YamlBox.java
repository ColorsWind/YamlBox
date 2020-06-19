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

/**
 * 代表一个 YamlBox实例.
 * @author colors_wind
 * @date 2020/6/19
 * @since 1.0.0
 */
public class YamlBox {
	protected final Yaml yaml;
	protected final Map<String, LoaderBase> loaders;
	@Getter
	protected final ILogger logger;

	/**
	 * 使用自定义日志样式输出, 创建一个YamlBox实例.
	 * @param logger 日志系统
	 */
	public YamlBox(ILogger logger) {
		this.logger = logger;
		this.yaml = new Yaml();
		this.loaders = new HashMap<>();
		this.loaders.put(UniversalLoader.UNIVERSAL, new UniversalLoader(this));
		this.loaders.put(EntryLoader.ENTRY, new EntryLoader(this));
	}
	
	/**
	 * 使用预设日志样式输出, 创建一个YamlBox实例.
	 * @param logger 日志系统
	 */
	public YamlBox(Logger logger) {
		this((level, path, msg) -> {
			logger.log(level, new StringBuilder(path).append(": ").append(msg).toString());
		});
	}

	/**
	 * 添加加载器器, 若已存在同名加载器, 则添加失败.
	 * @param name 唯一标识
	 * @param loader 加载器
	 * @return {@link true} 如果成功添加, 否则返回 {@code false}.
	 * @see {@link #forceAddLoader(String, LoaderBase)}
	 */
	public boolean addLoader(String name, LoaderBase loader) {
		return this.loaders.putIfAbsent(name, loader) == null;
	}

	/**
	 * 添加加载器器, 若已存在同名加载器, 则添加覆盖.
	 * @param name 唯一标识
	 * @param loader 加载器
	 * @return {@link true} 如果同名加载器不存在, 否则返回 {@code false}.
	 * @see {@link #forceAddLoader(String, LoaderBase)}
	 */
	public boolean forceAddLoader(String name, LoaderBase loader) {
		return this.loaders.put(name, loader) == null;
	}

	/**
	 * 获取指定名称的加载器.
	 * @param name 唯一标识
	 * @return 加载器, 若指定名称加载器不存在, 返回 {@link Optional#empty()}}.
	 */
	public Optional<LoaderBase> getLoader(String name) {
		return Optional.ofNullable(this.loaders.get(name));
	}

	/**
	 * 获取指定名称的加载器, 若找不到, 返回解析该类的默认加载器.
	 * @param name 唯一表示
	 * @param clazz 待解析的类
	 * @param key 唯一标识
	 * @return 加载器
	 */
	public LoaderBase getLoader(String name, Class<?> clazz, String key) {
		return getLoader(name).orElseGet(() -> {
			LoaderBase loader = getDefaultResolver(clazz);
			logger.warning(key, new StringBuilder("CANNOT find loader\" ").append(name).append(" \", using ")
					.append(loader).append(" instead.").toString());
			return loader;
		});
	}

	/**
	 * 移除注册的加载器
	 * @param name 唯一标识
	 * @return {@code true} 如果成功移除, 否则返回 {@code false}.
	 */
	public boolean removeLoader(String name) {
		return this.loaders.remove(name) == null;
	}

	/**
	 * 从字符串加载Yaml格式数据到当前节点.
	 * @param yamlString Yaml格式字符串
	 * @return 配置对象
	 */
	public YamlConfig load(String yamlString) {
		return load(yamlString, "");
	}
	
	/**
	 * 从字符串加载Yaml格式数据到当前节点.
	 * @param yamlString Yaml格式字符串
	 * @param root 配置对象的根路径
	 * @return 配置对象
	 */
	public YamlConfig load(String yamlString, String root) {
		Map<String, Object> map = yaml.load(yamlString);
		YamlConfig yamlConfig = new YamlConfig(map, root);
		return yamlConfig;
	}

	/**
	 * 从Reader加载Yaml格式数据到当前节点.
	 * @param reader {@link Reader}
	 * @return 配置对象
	 */
	public YamlConfig load(Reader reader) {
		return load(reader, "");
	}
	
	/**
	 * 从Reader加载Yaml格式数据到当前节点.
	 * @param reader {@link Reader}
	 * @param root 配置对象的根路径
	 * @return 配置对象
	 */
	public YamlConfig load(Reader reader, String root) {
		Map<String, Object> map = yaml.load(reader);
		YamlConfig yamlConfig = new YamlConfig(map, root);
		return yamlConfig;
	}

	/**
	 * 以Yaml格式写入当前节点数据.
	 * @param config 配置对象
	 * @param writer {@link Writer}
	 * @throws IOException 如果出现IO异常.
	 */
	public void dump(YamlConfig config, Writer writer) throws IOException {
		String yamlString = yaml.dumpAsMap(config);
		writer.write(yamlString);
	}

	/**
	 * 以Yaml格式输出当前节点的数据.
	 * @param config
	 * @return 字符串
	 */
	public String dumpAsString(YamlConfig config) {
		return yaml.dumpAsMap(config);
	}

	
	/**
	 * 获取默认加载器.
	 * @return 加载器
	 */
	public LoaderBase getDefaultResolver() {
		return getDefaultResolver(Object.class);
	}

	/**
	 * 获取指定类的默认加载器.
	 * @return 加载器
	 */
	public LoaderBase getDefaultResolver(Class<?> clazz) {
		Optional<LoaderBase> option = YamlSerializable.class.isAssignableFrom(clazz)
				? getLoader(EntryLoader.ENTRY)
				: getLoader(UniversalLoader.UNIVERSAL);
		return option.orElseThrow(() -> new NullPointerException("CANNOT determine default resolver."));
	}

}
