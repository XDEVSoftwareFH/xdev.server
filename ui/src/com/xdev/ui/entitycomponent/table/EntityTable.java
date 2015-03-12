//FIXME create beanitemcontainer derivation entitycontainer

//
//package com.xdev.ui.entitycomponent.table;
//
//
//import java.util.Collection;
//
//import com.vaadin.data.util.BeanItem;
//import com.vaadin.data.util.BeanItemContainer;
//import com.xdev.ui.entitycomponent.UIModelProvider;
//import com.xdev.ui.util.KeyValueType;
//
//
//public class EntityTable<T> extends AbstractEntityTable<T>
//{
//	/**
//	 *
//	 */
//	private static final long	serialVersionUID	= 8319108515219631399L;
//
//
//	@Override
//	public BeanItem<T> getItem(final Object id)
//	{
//		return getEntityDataSource().getEntityItem(id);
//	}
//
//
//	@Override
//	@SafeVarargs
//	public final void setModel(final Class<T> entityClass,
//			final KeyValueType<?, ?>... nestedProperties)
//	{
//		this.setEntityDataSource(this.getModelProvider()
//				.getModel(this,entityClass,nestedProperties));
//	}
//
//
//	@SafeVarargs
//	@Override
//	public final void setModel(final Class<T> entityClass, final Collection<T> data,
//			final KeyValueType<?, ?>... nestedProperties)
//	{
//		final BeanItemContainer<T> container = this.getModelProvider().getModel(this,entityClass,
//				nestedProperties);
//		for(final T entity : data)
//		{
//			container.addBean(entity);
//		}
//
//		this.setGenericDataSource(container);
//	}
//
//
//	@Override
//	protected UIModelProvider.Implementation<T> getModelProvider()
//	{
//		return new UIModelProvider.Implementation<T>();
//	}
//
//
//	@Override
//	public BeanItem<T> getSelectedItem()
//	{
//		if(!this.isMultiSelect())
//		{
//			return this.getContainerDataSource().getItem(this.getValue());
//		}
//		return null;
//	}
// }
