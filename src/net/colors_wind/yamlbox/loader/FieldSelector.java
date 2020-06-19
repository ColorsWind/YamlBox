package net.colors_wind.yamlbox.loader;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * 预定义的方法选择器.
 * @author colors_wind
 * @date 2020/6/19
 * @since 1.0.0
 */
public enum FieldSelector implements IFieldSelector {
	
	/**
	 * 选取所有公有(public)字段, 包括从父类继承的.
	 */
	SELECTOR_PUBLIC(clazz -> {
		Field[] fields = clazz.getFields();
		ArrayList<Field> list = new ArrayList<>(fields.length);
		for (Field field : fields) {
			field.setAccessible(true);
			if (!Modifier.isStatic(field.getModifiers())) {
				list.add(field);
			}
		}
		return list.toArray(new Field[list.size()]);
	}),
	/**
	 * 选取当前类所有字段.
	 */
	SELECTOR_DECLARE(clazz -> {
		Field[] fields = clazz.getDeclaredFields();
		for(Field field : fields) {
			field.setAccessible(true);
		}
		return fields;
	}),
	/**
	 * 选取当前类以及它的所有父类所有字段.
	 */
	SELECTOR_DECLARE_SUPER(clazz -> {
		Field[] fields = clazz.getDeclaredFields();
		ArrayList<Field> list = new ArrayList<>();
		do {
			for (Field field : fields) {
				field.setAccessible(true);
				if (!Modifier.isStatic(field.getModifiers())) {
					list.add(field);
				}
			}
		} while ((clazz = clazz.getSuperclass()) != Object.class);
		return list.toArray(new Field[list.size()]);
	});

	private final IFieldSelector selector;
	
	private FieldSelector(IFieldSelector selector) {
		this.selector = selector;
	}
	
	@Override
	public Field[] apply(Class<?> clazz) {
		return this.selector.apply(clazz);
	}

}
