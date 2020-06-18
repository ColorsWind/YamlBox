package net.colors_wind.yamlbox.resolve;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public enum FieldSelector implements IFieldSelector {
	
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
	SELECTOR_DECLARE(clazz -> {
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
