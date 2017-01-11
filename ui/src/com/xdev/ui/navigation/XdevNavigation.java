/*
 * Copyright (C) 2013-2017 by XDEV Software, All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * For further information see
 * <http://www.rapidclipse.com/en/legal/license/license.html>.
 */

package com.xdev.ui.navigation;


import java.util.Collection;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.UI;
import com.xdev.communication.URLParameterRegistry;
import com.xdev.communication.URLParameterRegistryValue;
import com.xdev.dal.DAOs;


/**
 *
 * @author XDEV Software (JW)
 *		
 */
public class XdevNavigation implements NavigationDefinition
{
	private final URLParameterRegistry registry = UI.getCurrent().getSession()
			.getAttribute(URLParameterRegistry.class);
			
	private String viewName;
	
	
	public String getViewName()
	{
		if(this.viewName != null)
		{
			return this.viewName;
		}
		
		throw new RuntimeException("No View set");
	}
	
	
	@Override
	public NavigationDefinition to(final String viewName)
	{
		this.viewName = viewName;
		
		return this;
	}
	
	
	@Override
	public NavigationDefinition parameter(final String parameterName, final Object parameterValue)
	{
		this.registry.put(parameterValue,this.getViewName(),parameterName);
		return this;
	}
	
	
	@Override
	public void navigate()
	{
		final Collection<URLParameterRegistryValue> values = this.registry
				.getValues(this.getViewName());
				
		String navigationURL = this.getViewName();
		for(final URLParameterRegistryValue urlParameterRegistryValue : values)
		{
			navigationURL += "/" + urlParameterRegistryValue.getPropertyName();
		}
		
		UI.getCurrent().getNavigator().navigateTo(navigationURL);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getParameter(final ViewChangeEvent navigationEvent, final String parameterName,
			final Class<T> type)
	{
		final String[] parameters = navigationEvent.getParameters().split("/");
		
		for(int i = 0; i < parameters.length; i++)
		{
			if(parameters[i].equals(parameterName))
			{
				final URLParameterRegistryValue value = this.registry
						.get(navigationEvent.getViewName(),parameterName);
				if(value != null)
				{
					if(value.getType().isAssignableFrom(type))
					{
						if(value.getPersistentID() != null)
						{
							return DAOs.getByEntityType(type).find(value.getPersistentID());
						}
						else
						{
							return (T)value.getValue();
						}
					}
				}
			}
		}
		return null;
	}
	
}
