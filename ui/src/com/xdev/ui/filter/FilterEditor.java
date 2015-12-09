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

package com.xdev.ui.filter;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.data.Container.Filter;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.themes.ValoTheme;
import com.xdev.res.StringResourceUtils;
import com.xdev.ui.filter.FilterField.FilterFieldChangeListener;
import com.xdev.ui.filter.OperatorHandler.FilterContext;


/**
 * @author XDEV Software
 *		
 */
public class FilterEditor
{
	protected static class PropertyEntry
	{
		private final Object	propertyID;
		private final Class<?>	propertyType;
		private final String	caption;


		public PropertyEntry(final Object propertyID, final Class<?> propertyType,
				final String caption)
		{
			this.propertyID = propertyID;
			this.propertyType = propertyType;
			this.caption = caption;
		}


		/**
		 * @return the propertyID
		 */
		public Object getPropertyId()
		{
			return this.propertyID;
		}


		/**
		 * @return the propertyType
		 */
		public Class<?> getPropertyType()
		{
			return this.propertyType;
		}


		/**
		 * @return the caption
		 */
		public String getCaption()
		{
			return this.caption;
		}


		@Override
		public String toString()
		{
			return this.caption;
		}
	}

	private final ContainerFilterComponent	containerFilterComponent;

	private final ComboBox					propertyComboBox;
	private final ComboBox					operatorComboBox;

	private final FilterFieldChangeListener	filterFieldChangeListener;
	private PropertyEntry					selectedPropertyEntry;
	private Operator						selectedOperator;
	private FilterField<?>[]				valueEditors;

	private final Button					removeFilterButton;
	private final Button					addFilterButton;


	public FilterEditor(final ContainerFilterComponent containerFilterComponent)
	{
		this.containerFilterComponent = containerFilterComponent;

		final List<PropertyEntry> propertyEntries = getPropertyEntries();
		
		this.propertyComboBox = createPropertyComboBox();
		this.propertyComboBox.addItems(propertyEntries);
		this.propertyComboBox.addValueChangeListener(event -> propertySelectionChanged());

		this.operatorComboBox = createOperatorComboBox();
		this.operatorComboBox.addValueChangeListener(event -> operatorSelectionChanged());
		this.operatorComboBox.setVisible(false);

		this.filterFieldChangeListener = event -> containerFilterComponent.updateContainerFilter();

		this.removeFilterButton = createRemoveFilterButton();
		this.removeFilterButton
				.addClickListener(event -> containerFilterComponent.removeFilterEditor(this));

		this.addFilterButton = createAddFilterButton();
		this.addFilterButton
				.addClickListener(event -> containerFilterComponent.addFilterEditorAfter(this));
	}


	protected ContainerFilterComponent getContainerFilterComponent()
	{
		return this.containerFilterComponent;
	}


	protected ComboBox createPropertyComboBox()
	{
		final ComboBox propertyComboBox = new ComboBox();
		propertyComboBox.setTextInputAllowed(false);
		propertyComboBox.setInputPrompt(StringResourceUtils
				.getResourceString("ContainerFilterComponent.selectOption",this));
		propertyComboBox.setNullSelectionAllowed(false);
		return propertyComboBox;
	}


	protected ComboBox getPropertyComboBox()
	{
		return this.propertyComboBox;
	}


	protected ComboBox createOperatorComboBox()
	{
		final ComboBox operatorComboBox = new ComboBox();
		operatorComboBox.setTextInputAllowed(false);
		operatorComboBox.setInputPrompt(StringResourceUtils
				.getResourceString("ContainerFilterComponent.selectOption",this));
		operatorComboBox.setNullSelectionAllowed(false);
		operatorComboBox.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		return operatorComboBox;
	}


	protected ComboBox getOperatorComboBox()
	{
		return this.operatorComboBox;
	}


	protected Button createRemoveFilterButton()
	{
		final Button removeFilterButton = new Button();
		removeFilterButton.setIcon(FontAwesome.MINUS_SQUARE);
		removeFilterButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		removeFilterButton.setDescription(StringResourceUtils
				.getResourceString("ContainerFilterComponent.removeFilter",this));
		return removeFilterButton;
	}


	protected Button getRemoveFilterButton()
	{
		return this.removeFilterButton;
	}


	protected Button createAddFilterButton()
	{
		final Button addFilterButton = new Button();
		addFilterButton.setIcon(FontAwesome.PLUS_SQUARE);
		addFilterButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		addFilterButton.setDescription(
				StringResourceUtils.getResourceString("ContainerFilterComponent.addFilter",this));
		return addFilterButton;
	}


	protected Button getAddFilterButton()
	{
		return this.addFilterButton;
	}


	protected List<PropertyEntry> getPropertyEntries()
	{
		return Arrays.stream(this.containerFilterComponent.getFilterableProperties())
				.map(id -> new PropertyEntry(id,this.containerFilterComponent.getPropertyType(id),
						this.containerFilterComponent.getPropertyCaption(id)))
				.collect(Collectors.toList());
	}


	protected void propertySelectionChanged()
	{
		removeValueEditors();

		final Object value = this.propertyComboBox.getValue();
		if(value instanceof PropertyEntry)
		{
			this.selectedPropertyEntry = (PropertyEntry)value;
			this.operatorComboBox.removeAllItems();
			this.operatorComboBox.addItems(Arrays.stream(Operator.values())
					.filter(op -> op.getHandler()
							.isPropertyTypeSupported(this.selectedPropertyEntry.getPropertyType()))
					.collect(Collectors.toList()));
			this.operatorComboBox.setVisible(true);
		}
		else
		{
			this.selectedPropertyEntry = null;
			this.operatorComboBox.setVisible(false);
		}

		this.containerFilterComponent.layoutFilters();
	}


	protected void operatorSelectionChanged()
	{
		final List<Object> lastValues = removeValueEditors();

		final Object value = this.operatorComboBox.getValue();
		if(value instanceof Operator)
		{
			this.selectedOperator = (Operator)value;
			final FilterContext context = new FilterContext(
					this.selectedPropertyEntry.getPropertyId(),this.containerFilterComponent);
			this.valueEditors = this.selectedOperator.getHandler().createValueEditors(context,
					this.selectedPropertyEntry.getPropertyType());
			for(int i = 0, c = Math.min(lastValues.size(),this.valueEditors.length); i < c; i++)
			{
				this.valueEditors[i].setFilterValue(lastValues.get(i));
			}
			for(int i = 0; i < this.valueEditors.length; i++)
			{
				this.valueEditors[i].addFilterFieldChangeListener(this.filterFieldChangeListener);
			}
		}
		else
		{
			this.selectedOperator = null;
		}

		this.containerFilterComponent.layoutFilters();
	}


	private List<Object> removeValueEditors()
	{
		final List<Object> lastValues = new ArrayList<>();

		if(this.valueEditors != null)
		{
			for(final FilterField<?> valueEditor : this.valueEditors)
			{
				lastValues.add(valueEditor.getFilterValue());
				valueEditor.removeFilterFieldChangeListener(this.filterFieldChangeListener);
			}

			this.valueEditors = null;
		}

		return lastValues;
	}


	protected FilterField<?>[] getValueEditors()
	{
		return this.valueEditors;
	}


	public Filter getFilter()
	{
		if(this.selectedPropertyEntry == null || this.selectedOperator == null
				|| this.valueEditors == null)
		{
			return null;
		}

		final FilterContext context = new FilterContext(this.selectedPropertyEntry.getPropertyId(),
				this.containerFilterComponent);
		return this.selectedOperator.getHandler().createFilter(context,this.valueEditors);
	}
}
