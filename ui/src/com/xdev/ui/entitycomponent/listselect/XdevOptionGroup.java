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
 */

package com.xdev.ui.entitycomponent.listselect;


import java.util.Collection;
import java.util.List;

import com.vaadin.data.Item;
import com.xdev.ui.XdevComponent;
import com.xdev.ui.entitycomponent.IDToBeanCollectionConverter;
import com.xdev.ui.entitycomponent.XdevBeanContainer;
import com.xdev.ui.paging.XdevEntityLazyQueryContainer;
import com.xdev.ui.util.KeyValueType;


/**
 * Configures select to be used as an option group.
 *
 * @author XDEV Software
 *		
 */
public class XdevOptionGroup<T> extends AbstractBeanOptionGroup<T> implements XdevComponent
{
	private final Extensions extensions = new Extensions();


	public XdevOptionGroup()
	{
		super();
	}
	
	
	public XdevOptionGroup(final String caption, final XdevBeanContainer<T> dataSource)
	{
		super(caption,dataSource);
	}
	
	
	public XdevOptionGroup(final String caption)
	{
		super(caption);
	}
	
	
	public XdevOptionGroup(final XdevBeanContainer<T> dataSource)
	{
		super(dataSource);
	}
	
	
	// init defaults
	{
		setImmediate(true);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> E addExtension(final Class<? super E> type, final E extension)
	{
		return this.extensions.add(type,extension);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> E getExtension(final Class<E> type)
	{
		return this.extensions.get(type);
	}


	/**
	 * {@inheritDoc}
	 */
	@SafeVarargs
	@Override
	public final void setContainerDataSource(final Class<T> beanClass, final boolean autoQueryData,
			final KeyValueType<?, ?>... nestedProperties)
	{
		this.setAutoQueryData(autoQueryData);
		final XdevBeanContainer<T> container = this.getModelProvider().getModel(this,beanClass,
				nestedProperties);

		// Workaround for OptionGroup internal size behavior.
		if(container instanceof XdevEntityLazyQueryContainer)
		{
			final XdevEntityLazyQueryContainer lqc = (XdevEntityLazyQueryContainer)container;
			final List<Item> items = lqc.getQueryView().getQuery().loadItems(0,Integer.MAX_VALUE);
			/*
			 * manualy set batch size because OptionGroup does not calucalte its
			 * own internal row count / size
			 */
			lqc.getQueryView().getQueryDefinition().setBatchSize(items.size());
			for(int i = 0; i < items.size(); i++)
			{
				// triggers internal add
				lqc.getQueryView().getItem(i);
			}
		}
		this.setContainerDataSource(container);
	}


	/**
	 * {@inheritDoc}
	 */
	@SafeVarargs
	@Override
	public final void setContainerDataSource(final Class<T> beanClass,
			final KeyValueType<?, ?>... nestedProperties)
	{
		this.setContainerDataSource(beanClass,true,nestedProperties);
	}


	@SafeVarargs
	@Override
	public final void setContainerDataSource(final Class<T> beanClass, final Collection<T> data,
			final KeyValueType<?, ?>... nestedProperties)
	{
		this.setAutoQueryData(false);
		final XdevBeanContainer<T> container = this.getModelProvider().getModel(this,beanClass,
				nestedProperties);
		container.addAll(data);

		this.setContainerDataSource(container);
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.vaadin.ui.AbstractSelect#setMultiSelect(boolean)
	 */
	@SuppressWarnings({"rawtypes","unchecked"})
	@Override
	public void setMultiSelect(final boolean multiSelect)
	{
		super.setMultiSelect(multiSelect);
		if(this.isAutoQueryData())
		{
			this.setConverter(new IDToBeanCollectionConverter(this.getContainerDataSource()));
		}
	}
}
