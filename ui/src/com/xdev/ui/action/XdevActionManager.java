/*
 * Copyright (C) 2013-2015 by XDEV Software, All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.xdev.ui.action;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.xdev.ui.XdevUI;
import com.xdev.ui.event.ContextSensitiveHandlerChangeEvent;
import com.xdev.ui.event.ContextSensitiveHandlerChangeListener;
import com.xdev.ui.util.UIUtils;


/**
 * Registry for all {@link ContextSensitiveAction}s of a {@link XdevUI}. The
 * actions are registered automatically inside this manager.
 *
 * @see #addContextSensitiveHandlerChangeListener(Class,
 *      ContextSensitiveHandlerChangeListener)
 * @see XdevUI#getXdevActionManager()
 *
 * @author XDEV Software
 *
 */
public class XdevActionManager
{
	/**
	 * Returns the action manager of the UI of the given component.
	 *
	 * @param c
	 *            any component in the component hierarchy
	 * @return the action manager of the UI
	 */
	public static XdevActionManager get(final Component c)
	{
		final UI ui = c.getUI();
		if(ui instanceof XdevUI)
		{
			return ((XdevUI)ui).getXdevActionManager();
		}

		return null;
	}


	/**
	 * Notifies all {@link ContextSensitiveHandlerChangeListener}s that the
	 * state of a certain handler has changed.
	 * <p>
	 * Convenience method for
	 * <code>XdevActionManager.get(component).fireContextSensitiveHandlerChanged(handlerType,handler);</code>
	 *
	 * @param handlerType
	 * @param handler
	 */
	@SuppressWarnings("unchecked")
	public static <T extends ContextSensitiveHandler, H extends Component & ContextSensitiveHandler> void contextSensitiveHandlerChanged(
			final Class<T> handlerType, final H handler)
	{
		get(handler).fireContextSensitiveHandlerChanged(handlerType,(T)handler);
	}



	private static class ActionContext<T extends ContextSensitiveHandler>
	{
		T										handler	= null;
		final List<ContextSensitiveAction<T>>	actions	= new ArrayList<>();
	}

	private final XdevUI																						ui;
	private final Map<Class<? extends ContextSensitiveHandler>, ActionContext<?>>								handlerContextMap;
	private final Map<Class<? extends ContextSensitiveHandler>, List<ContextSensitiveHandlerChangeListener>>	changeListeners;


	/**
	 * Creates a new action manager. The best way for users to acquire the
	 * current action manager is {@link XdevUI#getXdevActionManager()}.
	 *
	 * @param ui
	 *            the associated UI
	 */
	public XdevActionManager(final XdevUI ui)
	{
		this.ui = ui;
		this.handlerContextMap = new HashMap<>();
		this.changeListeners = new HashMap<>();

		ui.addFocusChangeListener(event -> focusChanged(event.getComponent()));
	}


	/**
	 * Returns the associated UI of this action manager.
	 *
	 * @return the UI
	 */
	public XdevUI getUI()
	{
		return this.ui;
	}


	/**
	 * Registeres a state change listener for a certain handler type.
	 *
	 * @see #contextSensitiveHandlerChanged(Class, Component)
	 *
	 * @param handlerType
	 *            the handler type to register the listener for
	 * @param listener
	 *            the listener to add
	 */
	public void addContextSensitiveHandlerChangeListener(
			final Class<? extends ContextSensitiveHandler> handlerType,
			final ContextSensitiveHandlerChangeListener listener)
	{
		synchronized(this.changeListeners)
		{
			List<ContextSensitiveHandlerChangeListener> list = this.changeListeners
					.get(handlerType);
			if(list == null)
			{
				list = new ArrayList<>();
				this.changeListeners.put(handlerType,list);
			}
			list.add(listener);
		}
	}


	/**
	 * Removes the listener of a certain handler type.
	 *
	 * @param handlerType
	 *            the handler type to remove the listener for
	 * @param listener
	 *            the listener to remove
	 */
	public void removeContextSensitiveHandlerChangeListener(
			final Class<? extends ContextSensitiveHandler> handlerType,
			final ContextSensitiveHandlerChangeListener listener)
	{
		synchronized(this.changeListeners)
		{
			final List<ContextSensitiveHandlerChangeListener> list = this.changeListeners
					.get(handlerType);
			if(list != null && list.remove(listener) && list.isEmpty())
			{
				this.changeListeners.remove(handlerType);
			}
		}
	}


	/**
	 * Notifies all {@link ContextSensitiveHandlerChangeListener}s that the
	 * state of a certain handler has changed.
	 *
	 * @param handlerType
	 *            the specific handler type
	 * @param handler
	 *            the handler instance
	 */
	public <H extends ContextSensitiveHandler> void fireContextSensitiveHandlerChanged(
			final Class<H> handlerType, final H handler)
	{
		synchronized(this.changeListeners)
		{
			final List<ContextSensitiveHandlerChangeListener> list = this.changeListeners
					.get(handlerType);
			if(list != null)
			{
				final ContextSensitiveHandlerChangeEvent event = new ContextSensitiveHandlerChangeEvent(
						handler);
				for(final ContextSensitiveHandlerChangeListener listener : list)
				{
					listener.contextSensitiveHandlerChanged(event);
				}
			}
		}
	}


	<T extends ContextSensitiveHandler> void registerContextSensitiveAction(
			final ContextSensitiveAction<T> action, final Class<T> handlerType)
	{
		@SuppressWarnings("unchecked")
		ActionContext<T> actionContext = (ActionContext<T>)this.handlerContextMap.get(handlerType);
		if(actionContext == null)
		{
			actionContext = new ActionContext<>();
			this.handlerContextMap.put(handlerType,actionContext);
		}
		actionContext.actions.add(action);
		if(actionContext.handler != null)
		{
			action.setHandler(actionContext.handler);
		}
	}


	private void focusChanged(final Component root)
	{
		for(final Class<? extends ContextSensitiveHandler> type : this.handlerContextMap.keySet())
		{
			focusChanged(root,type);
		}
	}


	private <T extends ContextSensitiveHandler> void focusChanged(final Component root,
			final Class<T> type)
	{
		final T newHandler = getHandler(type,root);

		@SuppressWarnings("unchecked")
		final ActionContext<T> actionContext = (ActionContext<T>)this.handlerContextMap.get(type);
		if(newHandler != actionContext.handler)
		{
			actionContext.handler = newHandler;
			for(final ContextSensitiveAction<T> action : actionContext.actions)
			{
				action.setHandler(newHandler);
			}
		}
	}


	private <T extends ContextSensitiveHandler> T getHandler(final Class<T> type,
			final Component root)
	{
		T handler = UIUtils.getNextParent(root,type);
		while(handler != null)
		{
			if(handler.isContextSensitiveHandlerEnabled(type))
			{
				return handler;
			}

			handler = UIUtils.getNextParent(((Component)handler).getParent(),type);
		}

		final T singleHandler = getSingleHandler(type,root.getUI());
		if(singleHandler != null)
		{
			return singleHandler;
		}

		return null;
	}


	private <T extends ContextSensitiveHandler> T getSingleHandler(final Class<T> type, final UI ui)
	{
		final List<T> handlers = new ArrayList<>();
		UIUtils.lookupComponentTree(ui,component -> {
			if(type.isInstance(component))
			{
				final T handler = type.cast(component);
				if(handler.isContextSensitiveHandlerEnabled(type))
				{
					handlers.add(handler);
				}
			}
			return null;
		});
		if(handlers.size() == 1)
		{
			return handlers.get(0);
		}
		return null;
	}
}
