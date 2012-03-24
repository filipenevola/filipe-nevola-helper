package com.filipenevola.helper.jsf.core;

//~--- JDK imports ------------------------------------------------------------
import java.lang.reflect.Method;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.convert.Converter;

import com.filipenevola.helper.log.Logger;

public class JsfUtil {
	public static void error(String cmpId, String msg) {
		FacesContext.getCurrentInstance().addMessage(cmpId,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
	}

	public static void error(String msg) {
		error(null, msg);
	}

	public static void warning(String cmpId, String msg) {
		FacesContext.getCurrentInstance().addMessage(cmpId,
				new FacesMessage(FacesMessage.SEVERITY_WARN, msg, msg));
	}

	public static void warning(String msg) {
		warning(null, msg);
	}

	public static void success(String cmpId, String msg) {
		FacesContext.getCurrentInstance().addMessage(cmpId,
				new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg));
	}

	public static void success(String msg) {
		success(null, msg);
	}

	public static String getRequestParameter(String key) {
		return FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get(key);
	}

	public static Object getObjectFromRequestParameter(
			String requestParameterName, Converter converter,
			UIComponent component) {
		String theId = JsfUtil.getRequestParameter(requestParameterName);
		return converter.getAsObject(FacesContext.getCurrentInstance(),
				component, theId);
	}

	public static Object getMethod(Object obj, String name) throws Exception {
		Method createMethod = obj.getClass().getMethod(name, new Class[0]);
		return createMethod.invoke(obj, new Object[0]);
	}

	public static void setAttribute(String valorObjeto, Object tipoObjeto) {
		FacesContext.getCurrentInstance().getExternalContext().getRequestMap()
				.put(valorObjeto, tipoObjeto);
	}

	public static Object getAttribute(String valorObjeto) {
		return FacesContext.getCurrentInstance().getExternalContext()
				.getRequestMap().get(valorObjeto);
	}

	public static Flash getFlashScope() {
		return (FacesContext.getCurrentInstance().getExternalContext()
				.getFlash());
	}

	private static final String OBJ_FLASH = "obj";

	public static void putObjectOnFlashScope(Object obj) {
		Logger.debug("Putting object on flash scope: " + OBJ_FLASH + "=" + obj);
		getFlashScope().put(OBJ_FLASH, obj);
	}

	public static Object getObjectFromFlashScope() {
		Object obj = getFlashScope().get(OBJ_FLASH);
		Logger.debug("Getting object on flash scope: " + OBJ_FLASH + "=" + obj);
		return obj;
	}

}
