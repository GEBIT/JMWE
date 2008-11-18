package com.innovalog.googlecode.jsu.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.innovalog.googlecode.jsu.util.LogUtils;

/**
 * @author <A href="mailto:abashev at gmail dot com">Alexey Abashev</A>
 * @version $Id: MapFieldProcessor.java,v 1.1 2008/08/07 17:08:04 fischer Exp $
 */
public class MapFieldProcessor extends AbstractVisitor {
	private final Class<? extends Annotation> annotation;
	private final Map<String, Object> values;
	
	/**
	 * @param annotation
	 * @param values
	 */
	public MapFieldProcessor(Class<? extends Annotation> annotation, Map<String, Object> values) {
		super();
		
		this.annotation = annotation;
		this.values = values;
	}

	public Class<? extends Annotation> getAnnotation() {
		return annotation;
	}

	/* (non-Javadoc)
	 * @see com.innovalog.googlecode.jsu.annotation.AbstractVisitor#visitField(java.lang.reflect.Field)
	 */
	public void visitField(Object source, Field field, Annotation sourceAnnon) {
		String fieldName = getAnnotationValue(sourceAnnon);
		
		if ((fieldName == null) || ("".equals(fieldName))) {
			fieldName = field.getName();
		}
		
		try {
			boolean access = field.isAccessible();
			
			field.setAccessible(true);
			field.set(source, values.get(fieldName));
			field.setAccessible(access);
		} catch (IllegalArgumentException e) {
			LogUtils.getGeneral().error("Unable to set class field - " + fieldName, e);
		} catch (IllegalAccessException e) {
			LogUtils.getGeneral().error("Unable to set class field - " + fieldName, e);
		}
	}
	
	protected String getAnnotationValue(Annotation annotation) {
		String result = null;
		
		try {
			Method valueMethod = annotation.getClass().getDeclaredMethod("value", new Class[] {});
			
			result = (String) valueMethod.invoke(annotation, new Object[] {});
		} catch (SecurityException e) {
			// Everything ok
		} catch (NoSuchMethodException e) {
			// Everything ok
		} catch (IllegalArgumentException e) {
			// Everything ok
		} catch (IllegalAccessException e) {
			// Everything ok
		} catch (InvocationTargetException e) {
			// Everything ok
		}
		
		return result;
	}
}
