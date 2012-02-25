/*
 * Copyright 2012 Filipe Nevola (@FilipeNevola)
 * Site: filipenevola.tumblr.com
 * Email: filipenevola@gmail.com
 * Work at Tecsinapse (@Tecsinapse)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.filipenevola.helper.jsf.scope;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import com.filipenevola.helper.log.Logger;

/**
 * @author Filipe Névola
 * 
 *         Keep the bean alive while this is being used
 */
public class BeingUsedScope implements Scope {

	public Object get(String beanName, ObjectFactory<?> objectFactory) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		if (ctx == null) {
			return null;
		}
		if (ctx.getViewRoot() == null || ctx.getViewRoot().getViewId() == null) {
			return null;
		}
		String viewId = ctx.getViewRoot().getViewId();
		HttpSession session = (HttpSession) ctx.getExternalContext()
				.getSession(false);

		if (session == null) {
			return null;
		}
		String sessionId = session.getId();

		if (BeingUsedScopeHolder.beanOnScope(beanName, sessionId)) {
			return BeingUsedScopeHolder.getBeanFromScope(beanName, sessionId,
					viewId);
		} else {
			Object object = objectFactory.getObject();
			BeingUsedScopeHolder.putBeanOnScope(object, beanName, sessionId,
					viewId);

			Logger.debug("-->PUT bean [" + object
					+ "] on scope - current viewId = " + viewId);
			BeingUsedScopeHolder
					.logDetailScopeContentBySession("After put a new bean");
			return object;
		}
	}

	public Object remove(String beanName) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		if (ctx == null) {
			return null;
		}
		HttpSession session = (HttpSession) ctx.getExternalContext()
				.getSession(false);
		if (session == null) {
			return null;
		}
		String sessionId = session.getId();
		Object obj = BeingUsedScopeHolder.removeBeanFromScope(beanName,
				sessionId);

		Logger.debug("<--REMOVED bean [" + beanName + "] from scope");
		BeingUsedScopeHolder
				.logDetailScopeContentBySession("After removed a bean");

		return obj;
	}

	public void registerDestructionCallback(String name, Runnable callback) {
		// Do nothing
	}

	public Object resolveContextualObject(String key) {
		return null;
	}

	public String getConversationId() {
		return null;
	}
}
