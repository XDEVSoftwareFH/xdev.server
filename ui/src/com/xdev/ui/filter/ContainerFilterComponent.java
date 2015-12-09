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


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.And;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.xdev.res.StringResourceUtils;
import com.xdev.ui.XdevGridLayout;


/**
 * @author XDEV Software
 *		
 */
public class ContainerFilterComponent extends CustomComponent
{
	private final VerticalLayout		rootLayout;
	private final TextFilterField		searchTextField;
	private final Button				addFilterButton;
	private final XdevGridLayout		filterLayout;

	private Container.Filterable		container;
	private Collection<?>				filterableProperties	= Collections.emptyList();
	private Collection<?>				searchableProperties	= Collections.emptyList();

	private SearchFilterGenerator		searchFilterGenerator	= new SearchFilterGenerator.Default();

	private boolean						caseSensitive			= false;
	private char						wordWildcard			= '*';
	private char						letterWildcard			= '_';

	private String						searchText				= "";

	private final List<FilterEditor>	filterEditors			= new ArrayList<>();


	/**
	 *
	 */
	public ContainerFilterComponent()
	{
		this.searchTextField = createSearchTextField();
		this.searchTextField.addFilterFieldChangeListener(event -> {
			final String text = (String)event.getFilterValue();
			this.searchText = text != null ? text : "";
			updateContainerFilter();
		});

		this.addFilterButton = createAddFilterButton();
		this.addFilterButton.addClickListener(event -> addFilterEditor(0));

		final HorizontalLayout headerLayout = new HorizontalLayout(this.searchTextField,
				this.addFilterButton);
		headerLayout.setMargin(false);
		headerLayout.setSpacing(true);

		this.searchTextField.setWidth(100,Unit.PERCENTAGE);
		headerLayout.setExpandRatio(this.searchTextField,1f);

		this.filterLayout = new XdevGridLayout();
		this.filterLayout.setMargin(false);
		this.filterLayout.setSpacing(true);
		this.filterLayout.setColumnExpandRatio(2,1f);
		this.filterLayout.setVisible(false);

		this.rootLayout = new VerticalLayout(headerLayout,this.filterLayout);
		this.rootLayout.setMargin(false);
		this.rootLayout.setSpacing(true);

		headerLayout.setWidth(100,Unit.PERCENTAGE);
		this.rootLayout.setExpandRatio(headerLayout,1f);

		this.filterLayout.setWidth(100,Unit.PERCENTAGE);
		this.rootLayout.setExpandRatio(this.filterLayout,1f);

		setCompositionRoot(this.rootLayout);
	}


	protected TextFilterField createSearchTextField()
	{
		final TextFilterField searchTextField = new TextFilterField();
		return searchTextField;
	}


	public TextFilterField getSearchTextField()
	{
		return this.searchTextField;
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


	public Button getAddFilterButton()
	{
		return this.addFilterButton;
	}


	public void setContainer(final Container.Filterable container,
			final Object... filterableProperties)
	{
		this.container = container;
		this.filterableProperties = filterableProperties != null && filterableProperties.length > 0
				? Arrays.asList(filterableProperties) : container.getContainerPropertyIds();
		setSearchableProperties(this.filterableProperties.stream()
				.filter(p -> getPropertyType(p) == String.class).toArray());
				
		reset();
	}


	/**
	 * @return the container
	 */
	public Container.Filterable getContainer()
	{
		return this.container;
	}


	public void setSearchableProperties(final Object... searchableProperties)
	{
		this.searchableProperties = Arrays.asList(searchableProperties);

		updateSearchTextFieldInputPrompt();
	}


	protected void updateSearchTextFieldInputPrompt()
	{
		final String res = StringResourceUtils
				.getResourceString("ContainerFilterComponent.searchTextFieldInputPrompt",this);
		final String properties = this.searchableProperties.stream()
				.map(id -> getPropertyCaption(id)).collect(Collectors.joining(", "));
		final String prompt = MessageFormat.format(res,properties);
		this.searchTextField.setInputPrompt(prompt);
	}


	protected Object[] getFilterableProperties()
	{
		return this.filterableProperties.toArray();
	}


	protected Object[] getSearchableProperties()
	{
		return this.searchableProperties.toArray();
	}


	public void setSearchFilterGenerator(final SearchFilterGenerator searchFilterGenerator)
	{
		this.searchFilterGenerator = searchFilterGenerator;
	}


	public SearchFilterGenerator getSearchFilterGenerator()
	{
		return this.searchFilterGenerator;
	}


	public void setCaseSensitive(final boolean caseSensitive)
	{
		this.caseSensitive = caseSensitive;
	}


	public boolean isCaseSensitive()
	{
		return this.caseSensitive;
	}


	public void setWordWildcard(final char wordWildcard)
	{
		this.wordWildcard = wordWildcard;
	}


	public char getWordWildcard()
	{
		return this.wordWildcard;
	}


	public void setLetterWildcard(final char letterWildcard)
	{
		this.letterWildcard = letterWildcard;
	}


	public char getLetterWildcard()
	{
		return this.letterWildcard;
	}


	protected void reset()
	{
		if(this.container != null)
		{
			this.container.removeAllContainerFilters();
		}
		this.searchTextField.setValue("");
		this.filterEditors.clear();
		this.filterLayout.removeAllComponents();
	}


	protected void updateContainerFilter()
	{
		if(this.container == null)
		{
			return;
		}

		final Filter newFilter = createFilter();
		final Collection<Filter> oldFilters = this.container.getContainerFilters();
		if(newFilter == null)
		{
			if(oldFilters.size() > 0)
			{
				this.container.removeAllContainerFilters();
			}
		}
		else if(oldFilters.isEmpty())
		{
			this.container.addContainerFilter(newFilter);
		}
		else if(oldFilters.size() > 1 || !oldFilters.iterator().next().equals(newFilter))
		{
			this.container.removeAllContainerFilters();
			this.container.addContainerFilter(newFilter);
		}
	}


	protected Filter createFilter()
	{
		final Filter searchFilter = createSearchFilter();
		final Filter valueFilter = createValueFilter();
		if(searchFilter != null && valueFilter != null)
		{
			return new And(searchFilter,valueFilter);
		}
		else if(searchFilter != null)
		{
			return searchFilter;
		}
		else if(valueFilter != null)
		{
			return valueFilter;
		}
		return null;
	}


	protected Filter createSearchFilter()
	{
		if(this.searchFilterGenerator != null)
		{
			return this.searchFilterGenerator.createSearchFilter(this);
		}

		return null;
	}


	protected Filter createValueFilter()
	{
		if(this.filterEditors.isEmpty())
		{
			return null;
		}

		final List<Filter> valueFilters = this.filterEditors.stream()
				.map(editor -> editor.getFilter()).filter(Objects::nonNull)
				.collect(Collectors.toList());
		if(valueFilters.isEmpty())
		{
			return null;
		}

		final int count = valueFilters.size();
		if(count == 1)
		{
			return valueFilters.get(0);
		}

		return new And(valueFilters.toArray(new Filter[count]));
	}


	protected String getSearchText()
	{
		return this.searchText;
	}


	protected FilterEditor addFilterEditorAfter(final FilterEditor filterEditor)
	{
		return addFilterEditor(this.filterEditors.indexOf(filterEditor) + 1);
	}


	protected FilterEditor addFilterEditor(final int index)
	{
		final FilterEditor editor = createFilterEditor();
		this.filterEditors.add(index,editor);
		layoutFilters();
		return editor;
	}
	
	
	protected FilterEditor createFilterEditor()
	{
		return new FilterEditor(this);
	}


	protected void removeFilterEditor(final FilterEditor filterEditor)
	{
		this.filterEditors.remove(filterEditor);
		layoutFilters();
	}


	protected FilterEditor[] getFilterEditors()
	{
		return this.filterEditors.toArray(new FilterEditor[this.filterEditors.size()]);
	}


	protected void layoutFilters()
	{
		this.filterLayout.removeAllComponents();
		this.filterLayout.setRows(1);

		int row = 0;

		for(final FilterEditor filterEditor : this.filterEditors)
		{
			this.filterLayout.addComponent(filterEditor.getPropertyComboBox(),0,row);
			this.filterLayout.addComponent(filterEditor.getOperatorComboBox(),1,row);
			this.filterLayout.addComponent(filterEditor.getRemoveFilterButton(),3,row);
			this.filterLayout.addComponent(filterEditor.getAddFilterButton(),4,row);

			final FilterField<?>[] valueEditors = filterEditor.getValueEditors();
			if(valueEditors != null)
			{
				for(final FilterField<?> valueEditor : valueEditors)
				{
					this.filterLayout.addComponent(valueEditor,2,row);

					row++;
				}
			}
			else
			{
				row++;
			}
		}

		this.filterLayout.setVisible(row > 0);

		updateContainerFilter();
	}


	protected String getPropertyCaption(final Object propertyId)
	{
		return propertyId.toString();
	}


	protected Class<?> getPropertyType(final Object propertyId)
	{
		return this.container.getType(propertyId);
	}
}
