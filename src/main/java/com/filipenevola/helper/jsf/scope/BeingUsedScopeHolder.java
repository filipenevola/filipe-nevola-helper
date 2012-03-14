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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.filipenevola.helper.log.Logger;

/**
 * @author Filipe Névola
 * 
 *         Holds and manipulate the beans and views on this Scope
 */
public class BeingUsedScopeHolder {

	private static final String KEY_HOLDER = "beingUsedScopeHolder";
	private static final String KEY_VIEWS = "beingUsedScopeViews";

	public static void putBeanOnScope(HttpSession session, Object bean,
			String beanName, String viewId) {
		UsingScopeKey key = new UsingScopeKey(session.getId(), beanName);
		getHolder(session).put(key, bean);
		beanBeingUsedOnViewId(session, viewId, key);
	}

	private static void removeDetachedBeans(HttpSession session, String viewId) {
		for (Iterator<Map.Entry<UsingScopeKey, Object>> iterator = getHolder(
				session).entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<UsingScopeKey, Object> entry = iterator.next();
			UsingScopeKey key = entry.getKey();
			if (key.getSessionId().equals(session.getId())) {
				boolean detached = !getViews(session).containsKey(key)
						|| !getViews(session).get(key).equals(viewId);

				if (detached) {
					iterator.remove();

					Logger.debug("<--REMOVED DETACHED bean [" + key
							+ "] from scope");
				}
			}

		}
	}

	public static Boolean beanOnScope(HttpSession session, String beanName) {
		UsingScopeKey key = new UsingScopeKey(session.getId(), beanName);
		Boolean contains = getHolder(session).containsKey(key);

		return contains;
	}

	public static Object getBeanFromScope(HttpSession session, String beanName,
			String viewId) {
		UsingScopeKey key = new UsingScopeKey(session.getId(), beanName);
		beanBeingUsedOnViewId(session, viewId, key);
		return getHolder(session).get(key);
	}

	private static void beanBeingUsedOnViewId(HttpSession session,
			String viewId, UsingScopeKey key) {
		Logger.debug("BeginUsed - " + key + " = " + viewId);
		getViews(session).put(key, viewId);
	}

	public static Object removeBeanFromScope(HttpSession session,
			String beanName) {
		UsingScopeKey key = new UsingScopeKey(session.getId(), beanName);
		return getHolder(session).remove(key);
	}

	public static void logScopeContent(HttpSession session) {
		StringBuilder sb = new StringBuilder();
		sb.append("Beans = [ ");
		for (UsingScopeKey key : getHolder(session).keySet()) {
			sb.append(key);
			sb.append(" ");
		}
		sb.append("]");
		sb.append(" - Views = ");
		sb.append(getViews(session).values());
		Logger.debug(sb.toString());
	}

	public static void logDetailScopeContentBySession(HttpSession session,
			String place) {
		Map<String, Map<String, List<String>>> map = new HashMap<String, Map<String, List<String>>>();
		for (UsingScopeKey key : getHolder(session).keySet()) {
			if (!map.containsKey(key.getSessionId())) {
				map.put(key.getSessionId(), new HashMap<String, List<String>>());
			}
			if (!map.get(key.getSessionId()).containsKey("beans")) {
				map.get(key.getSessionId()).put("beans",
						new ArrayList<String>());
			}
			map.get(key.getSessionId()).get("beans")
					.add(getHolder(session).get(key).toString());
		}
		for (UsingScopeKey key : getViews(session).keySet()) {
			if (!map.containsKey(key.getSessionId())) {
				map.put(key.getSessionId(), new HashMap<String, List<String>>());
			}
			if (!map.get(key.getSessionId()).containsKey("views")) {
				map.get(key.getSessionId()).put("views",
						new ArrayList<String>());
			}
			map.get(key.getSessionId()).get("views")
					.add(getViews(session).get(key));
		}

		StringBuilder sb = new StringBuilder();
		sb.append("\n+++BEGIN LOG DETAIL BEING USED SCOPE: ");
		sb.append(place);
		int sessionCount = 1;
		for (String sessionId : map.keySet()) {
			sb.append("\n");
			sb.append("SessionId(");
			sb.append(sessionCount++);
			sb.append(")=");
			sb.append(sessionId);
			sb.append(":\n");
			sb.append("[");
			for (String type : map.get(sessionId).keySet()) {
				sb.append("(\n");
				sb.append(type);
				sb.append(":\n");
				sb.append(map.get(sessionId).get(type));
				sb.append(")");
			}
			sb.append("]");

		}
		sb.append("\n---END LOG DETAIL BEING USED SCOPE: ");
		sb.append(place);
		Logger.debug(sb.toString());
	}

	@SuppressWarnings("unchecked")
	private static Map<UsingScopeKey, String> getViews(HttpSession session) {
		if (session.getAttribute(KEY_VIEWS) == null) {
			session.setAttribute(KEY_VIEWS, Collections
					.synchronizedMap(new HashMap<UsingScopeKey, String>()));
		}
		return (Map<UsingScopeKey, String>) session.getAttribute(KEY_VIEWS);
	}

	@SuppressWarnings("unchecked")
	private static Map<UsingScopeKey, Object> getHolder(HttpSession session) {
		if (session.getAttribute(KEY_HOLDER) == null) {
			session.setAttribute(KEY_HOLDER, Collections
					.synchronizedMap(new HashMap<UsingScopeKey, Object>()));
		}
		return (Map<UsingScopeKey, Object>) session.getAttribute(KEY_HOLDER);
	}

	public static void updateViewsAndRemoveDetachedBeans(HttpSession session,
			String viewId) {

		removeDetachedBeans(session, viewId);
		removeViewsFromThisSession(session);
		logDetailScopeContentBySession(session,
				"After updateViewsAndRemoveDetachedBeans");

	}

	private static void removeViewsFromThisSession(HttpSession session) {
		for (Iterator<Map.Entry<UsingScopeKey, String>> iterator = getViews(
				session).entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<UsingScopeKey, String> entry = iterator.next();
			UsingScopeKey key = entry.getKey();
			if (key.getSessionId().equals(session.getId())) {
				iterator.remove();
			}
		}
	}
}

/**
 * @author Filipe Névola
 * 
 *         Key for hold objects on this Scope
 */
class UsingScopeKey {
	private String sessionId;
	private String beanName;

	public UsingScopeKey(String sessionId, String beanName) {
		super();
		this.sessionId = sessionId;
		this.beanName = beanName;
	}

	@Override
	public String toString() {
		return "UsingScopeKey [sessionId=" + sessionId + ", beanName="
				+ beanName + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((beanName == null) ? 0 : beanName.hashCode());
		result = prime * result
				+ ((sessionId == null) ? 0 : sessionId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UsingScopeKey))
			return false;
		UsingScopeKey other = (UsingScopeKey) obj;
		if (beanName == null) {
			if (other.beanName != null)
				return false;
		} else if (!beanName.equals(other.beanName))
			return false;
		if (sessionId == null) {
			if (other.sessionId != null)
				return false;
		} else if (!sessionId.equals(other.sessionId))
			return false;
		return true;
	}

	public String getBeanName() {
		return beanName;
	}

	public String getSessionId() {
		return sessionId;
	}

}
