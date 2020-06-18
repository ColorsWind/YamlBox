package net.colors_wind.yamlbox.tag;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.colors_wind.yamlbox.loader.EntryLoader;
import net.colors_wind.yamlbox.loader.FieldSelector;

@Documented
@Retention(RUNTIME)
@Target({TYPE,FIELD})
public @interface SerializeNode {
	
	String loader() default EntryLoader.ENTRY;
	
	FieldSelector fieldSelector(); 

}
