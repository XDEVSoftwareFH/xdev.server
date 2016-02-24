/*
 * Copyright (C) 2013-2016 by XDEV Software, All Rights Reserved.
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

package com.xdev.ui.entitycomponent;


import java.util.Locale;

import com.vaadin.data.util.converter.Converter;
import com.xdev.util.EntityIDResolver;
import com.xdev.util.HibernateEntityIDResolver;


public class BeanToBeanConverter<T> implements Converter<T, T>
{
	private final XdevBeanContainer<T>	container;
	private final EntityIDResolver		idResolver;


	/**
	 *
	 */
	public BeanToBeanConverter(final XdevBeanContainer<T> container)
	{
		this.container = container;
		this.idResolver = HibernateEntityIDResolver.getInstance();
	}


	@Override
	public T convertToModel(final T bean, final Class<? extends T> targetType, final Locale locale)
			throws Converter.ConversionException
	{
		return bean;
	}


	@Override
	public T convertToPresentation(final T value, final Class<? extends T> targetType,
			final Locale locale) throws Converter.ConversionException
	{
		if(value == null)
		{
			return null;
		}

		final Object id = this.idResolver.getEntityIDPropertyValue(value);
		final T containerValue = this.container.getItemIds().stream()
				.map(propertyId -> this.container.getItem(propertyId).getBean())
				.filter(bean -> bean.equals(value) || (id != null
						&& id.equals(this.idResolver.getEntityIDPropertyValue(bean))))
				.findFirst().orElse(null);
		return containerValue != null ? containerValue : value;
	}


	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getModelType()
	{
		return (Class<T>)this.container.getBeanType();
	}


	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getPresentationType()
	{
		return (Class<T>)this.container.getBeanType();
	}
}
