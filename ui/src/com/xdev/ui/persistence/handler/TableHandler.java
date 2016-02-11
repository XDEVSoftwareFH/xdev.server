
package com.xdev.ui.persistence.handler;


import java.util.Map;

import com.vaadin.ui.Table;
import com.xdev.ui.persistence.GuiPersistenceEntry;


public class TableHandler extends AbstractComponentHandler<Table>
{
	protected static final String	KEY_VALUE						= "value";
	protected static final String	KEY_SORT_CONTAINER_PROPERTY_ID	= "sortContainerPropertyId";
	protected static final String	KEY_VISIBLE_COLUMNS				= "visibleColumns";
	protected static final String	KEY_IS_COLLAPSED				= "isCollapsed";
	protected static final String	KEY_IS_ASCENDING				= "isAscending";
	protected static final String	KEY_COLUMN_WIDTH				= "columnWidth";
																	
																	
	@Override
	public Class<Table> handledType()
	{
		return Table.class;
	}
	
	
	@Override
	protected void addEntryValues(final Map<String, Object> entryValues, final Table component)
	{
		super.addEntryValues(entryValues,component);
		
		entryValues.put(KEY_VALUE,component.getValue());
		entryValues.put(KEY_SORT_CONTAINER_PROPERTY_ID,component.getSortContainerPropertyId());
		
		final Object[] comVisCol = new Object[component.getVisibleColumns().length];
		final Object[] widthCol = new Object[component.getVisibleColumns().length];
		final Object[] allColumns = component.getVisibleColumns();
		int i = 0;
		
		for(final Object o : allColumns)
		{
			widthCol[i] = component.getColumnWidth(allColumns[i]);
			if(component.isColumnCollapsed(o))
			{
				comVisCol[i] = true;
			}
			else
			{
				comVisCol[i] = false;
			}
			i++;
		}
		
		entryValues.put(KEY_VISIBLE_COLUMNS,changeObjectArray(allColumns));
		entryValues.put(KEY_IS_COLLAPSED,changeObjectArray(comVisCol));
		entryValues.put(KEY_COLUMN_WIDTH,changeObjectArray(widthCol));
		entryValues.put(KEY_IS_ASCENDING,component.isSortAscending());
	}
	
	
	@Override
	public void restore(final Table component, final GuiPersistenceEntry entry)
	{
		super.restore(component,entry);
		
		component.setValue(entry.value(KEY_VALUE));
		component.setSortContainerPropertyId(entry.value(KEY_SORT_CONTAINER_PROPERTY_ID));
		
		final Object[] comVisCol = returnObjectArray(entry.value(KEY_IS_COLLAPSED).toString());
		final Object[] widthCol = returnObjectArray(entry.value(KEY_COLUMN_WIDTH).toString());
		final Object[] namesCol = returnObjectArray(entry.value(KEY_VISIBLE_COLUMNS).toString());

		if(component.isColumnCollapsingAllowed())
		{
			for(int i = 0; i < comVisCol.length; i++)
			{
				component.setColumnCollapsed(namesCol[i],new Boolean(comVisCol[i].toString()));
				component.setColumnWidth(namesCol[i],Integer.valueOf(widthCol[i].toString()));
			}
		}
		else
		{
			for(int i = 0; i < comVisCol.length; i++)
			{
				component.setColumnWidth(namesCol[i],Integer.valueOf(widthCol[i].toString()));
			}
		}
		
		component.setSortAscending(new Boolean(entry.value(KEY_IS_ASCENDING).toString()));
	}
}
