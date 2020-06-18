package net.colors_wind.yamlbox.loader;

import java.util.logging.Level;

public interface ILogger {
	
	void log(Level level, String path, String msg);
	
	default void info(String path, String msg) {
		log(Level.FINE, path, msg);
	}
	
	default void warning(String path, String msg) {
		log(Level.WARNING, path, msg);
	}
	
	default void sevre(String path, String msg) {
		log(Level.SEVERE, path, msg);
	}
	

}
