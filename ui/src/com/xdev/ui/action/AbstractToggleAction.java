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

package com.xdev.ui.action;


import com.vaadin.server.Resource;


/**
 * @author XDEV Software
 * 
 */
public abstract class AbstractToggleAction extends AbstractAction implements ToggleAction
{
	private boolean selected = false;
	
	
	/**
	 */
	public AbstractToggleAction()
	{
		super();
	}


	/**
	 * @param caption
	 */
	public AbstractToggleAction(final String caption)
	{
		super(caption);
	}
	
	
	/**
	 * @param icon
	 */
	public AbstractToggleAction(final Resource icon)
	{
		super(icon);
	}


	/**
	 * @param caption
	 * @param icon
	 */
	public AbstractToggleAction(final String caption, final Resource icon)
	{
		super(caption,icon);
	}


	@Override
	public boolean isSelected()
	{
		return this.selected;
	}


	@Override
	public void setSelected(final boolean selected)
	{
		if(this.selected != selected)
		{
			final Object oldValue = this.selected;

			this.selected = selected;

			firePropertyChange(SELECTED_PROPERTY,oldValue,selected);
		}
	}
}
