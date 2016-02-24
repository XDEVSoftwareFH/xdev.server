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

package com.xdev.ui.entitycomponent.listselect;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.OptionGroup;
import com.xdev.ui.entitycomponent.BeanComponent;
import com.xdev.ui.entitycomponent.UIModelProvider;
import com.xdev.ui.entitycomponent.XdevBeanContainer;
import com.xdev.ui.paging.LazyLoadingUIModelProvider;


/**
 * @author XDEV Software
 * 		
 * @since 1.1
 */
public abstract class AbstractBeanOptionGroup<BEANTYPE> extends OptionGroup
		implements BeanComponent<BEANTYPE>
{
	private boolean autoQueryData = true;


	public AbstractBeanOptionGroup()
	{
		super();
	}
	
	
	public AbstractBeanOptionGroup(final String caption)
	{
		super(caption);
	}
	
	
	public AbstractBeanOptionGroup(final XdevBeanContainer<BEANTYPE> dataSource)
	{
		super(null,dataSource);
	}
	
	
	public AbstractBeanOptionGroup(final String caption,
			final XdevBeanContainer<BEANTYPE> dataSource)
	{
		super(caption,dataSource);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public XdevBeanContainer<BEANTYPE> getContainerDataSource()
	{
		if(super.getContainerDataSource() instanceof XdevBeanContainer)
		{
			return (XdevBeanContainer<BEANTYPE>)super.getContainerDataSource();
		}
		// else
		// {
		// throw new RuntimeException(
		// "While using BeanComponents a fitting XdevBeanContainer must be
		// set");
		// }
		return null;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setContainerDataSource(final Container newDataSource)
	{
		if(newDataSource instanceof XdevBeanContainer)
		{
			super.setContainerDataSource(newDataSource);
			this.getModelProvider().setRelatedModelConverter(this,
					(XdevBeanContainer<BEANTYPE>)newDataSource);
		}
		else
		{
			super.setContainerDataSource(newDataSource);
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 *
	 * @see com.xdev.ui.entitycomponent.BeanComponent#isAutoQueryData()
	 */
	@Override
	public boolean isAutoQueryData()
	{
		return this.autoQueryData;
	}
	
	
	/*
	 * (non-Javadoc)
	 *
	 * @see com.xdev.ui.entitycomponent.BeanComponent#autoQueryData(boolean)
	 */
	@Override
	public void setAutoQueryData(final boolean autoQuery)
	{
		this.autoQueryData = autoQuery;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public BeanItem<BEANTYPE> getItem(final Object itemId)
	{
		return this.getContainerDataSource().getItem(itemId);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public BeanItem<BEANTYPE> getSelectedItem()
	{
		return this.getContainerDataSource().getItem(this.getValue());
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<BeanItem<BEANTYPE>> getSelectedItems()
	{
		if(this.isMultiSelect())
		{
			final XdevBeanContainer<BEANTYPE> container = this.getContainerDataSource();
			return ((Collection<?>)this.getValue()).stream().map(id -> container.getItem(id))
					.collect(Collectors.toList());
		}
		else
		{
			final List<BeanItem<BEANTYPE>> list = new ArrayList<>(1);
			list.add(getSelectedItem());
			return list;
		}
	}
	
	
	protected UIModelProvider<BEANTYPE> getModelProvider()
	{
		if(this.isAutoQueryData())
		{
			return new LazyLoadingUIModelProvider<BEANTYPE>(20,false,false);
		}
		else
		{
			return new UIModelProvider.Implementation<BEANTYPE>();
		}
	}
}
