package net.colors_wind.yamlbox.loader;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target({TYPE,FIELD})
public @interface SerializeNode {
	
	String loader() default EntryLoader.ENTRY;
	
	FieldSelector fieldSelector(); 

}
