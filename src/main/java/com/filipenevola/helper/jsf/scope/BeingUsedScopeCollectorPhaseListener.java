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
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpSession;

import com.filipenevola.helper.log.Logger;

/**
 * @author Filipe Névola
 * 
 *         Collects beans when this is not being used
 */
public class BeingUsedScopeCollectorPhaseListener implements PhaseListener {
	private static final long serialVersionUID = 1L;

	public BeingUsedScopeCollectorPhaseListener() {
	}

	public void afterPhase(PhaseEvent event) {
		FacesContext ctx = event.getFacesContext();
		if (ctx == null) {
			return;
		}
		if (ctx.getViewRoot() == null || ctx.getViewRoot().getViewId() == null) {
			return;
		}
		String viewId = ctx.getViewRoot().getViewId();
		Logger.debug("BeingUsedScopeCollectorPhaseListener running - current viewId = "
				+ viewId);
		HttpSession session = (HttpSession) ctx.getExternalContext()
				.getSession(false);

		if (session == null) {
			return;
		}
		String sessionId = session.getId();

		BeingUsedScopeHolder.updateViewsAndRemoveDetachedBeans(sessionId,
				viewId);
	}

	public void beforePhase(PhaseEvent event) {
	}

	public PhaseId getPhaseId() {
		return PhaseId.RENDER_RESPONSE;
	}
}
