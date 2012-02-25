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

import com.filipenevola.helper.log.Logger;

/**
 * @author Filipe Névola
 * 
 *         Holds and manipulate the beans and views on this Scope
 */
public class BeingUsedScopeHolder {

	private static Map<UsingScopeKey, Object> holder = Collections
			.synchronizedMap(new HashMap<UsingScopeKey, Object>());
	private static Map<UsingScopeKey, String> views = Collections
			.synchronizedMap(new HashMap<UsingScopeKey, String>());

	public static void putBeanOnScope(Object bean, String beanName,
			String sessionId, String viewId) {
		UsingScopeKey key = new UsingScopeKey(sessionId, beanName);
		holder.put(key, bean);
		beanBeingUsedOnViewId(viewId, key);
	}

	private static void removeDetachedBeans(String sessionId, String viewId) {
		for (Iterator<Map.Entry<UsingScopeKey, Object>> iterator = holder
				.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<UsingScopeKey, Object> entry = iterator.next();
			UsingScopeKey key = entry.getKey();
			if (key.getSessionId().equals(sessionId)) {
				boolean detached = !views.containsKey(key)
						|| !views.get(key).equals(viewId);

				if (detached) {
					iterator.remove();

					Logger.debug("<--REMOVED DETACHED bean [" + key
							+ "] from scope");
				}
			}

		}
	}

	public static Boolean beanOnScope(String beanName, String sessionId) {
		UsingScopeKey key = new UsingScopeKey(sessionId, beanName);
		Boolean contains = holder.containsKey(key);

		return contains;
	}

	public static Object getBeanFromScope(String beanName, String sessionId,
			String viewId) {
		UsingScopeKey key = new UsingScopeKey(sessionId, beanName);
		beanBeingUsedOnViewId(viewId, key);
		return holder.get(key);
	}

	private static void beanBeingUsedOnViewId(String viewId, UsingScopeKey key) {
		Logger.debug("BeginUsed - " + key + " = " + viewId);
		views.put(key, viewId);
	}

	public static Object removeBeanFromScope(String beanName, String sessionId) {
		UsingScopeKey key = new UsingScopeKey(sessionId, beanName);
		return holder.remove(key);
	}

	public static void logScopeContent() {
		StringBuilder sb = new StringBuilder();
		sb.append("Beans = [ ");
		for (UsingScopeKey key : holder.keySet()) {
			sb.append(key);
			sb.append(" ");
		}
		sb.append("]");
		sb.append(" - Views = ");
		sb.append(views.values());
		Logger.debug(sb.toString());
	}

	public static void logDetailScopeContentBySession(String place) {
		Map<String, Map<String, List<String>>> map = new HashMap<String, Map<String, List<String>>>();
		for (UsingScopeKey key : holder.keySet()) {
			if (!map.containsKey(key.getSessionId())) {
				map.put(key.getSessionId(), new HashMap<String, List<String>>());
			}
			if (!map.get(key.getSessionId()).containsKey("beans")) {
				map.get(key.getSessionId()).put("beans",
						new ArrayList<String>());
			}
			map.get(key.getSessionId()).get("beans")
					.add(holder.get(key).toString());
		}
		for (UsingScopeKey key : views.keySet()) {
			if (!map.containsKey(key.getSessionId())) {
				map.put(key.getSessionId(), new HashMap<String, List<String>>());
			}
			if (!map.get(key.getSessionId()).containsKey("views")) {
				map.get(key.getSessionId()).put("views",
						new ArrayList<String>());
			}
			map.get(key.getSessionId()).get("views").add(views.get(key));
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

	public static void updateViewsAndRemoveDetachedBeans(String sessionId,
			String viewId) {

		removeDetachedBeans(sessionId, viewId);
		removeViewsFromThisSession(sessionId);
		logDetailScopeContentBySession("After updateViewsAndRemoveDetachedBeans");

	}

	private static void removeViewsFromThisSession(String sessionId) {
		for (Iterator<Map.Entry<UsingScopeKey, String>> iterator = views
				.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<UsingScopeKey, String> entry = iterator.next();
			UsingScopeKey key = entry.getKey();
			if (key.getSessionId().equals(sessionId)) {
				iterator.remove();
			}
		}
	}
}

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
