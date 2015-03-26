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
 
package com.xdev.ui;


import com.vaadin.data.Property;
import com.vaadin.ui.ProgressBar;


/**
 * Shows the current progress of a long running task.
 * <p>
 * The default mode is to show the current progress internally represented by a
 * floating point value between 0 and 1 (inclusive). The progress bar can also
 * be in an indeterminate mode showing an animation indicating that the task is
 * running but without providing any information about the current progress.
 *
 * @author XDEV Software
 *
 */
public class XdevProgressBar extends ProgressBar
{
	/**
	 * Creates a new progress bar initially set to 0% progress.
	 */
	public XdevProgressBar()
	{
		super();
	}
	
	
	/**
	 * Creates a new progress bar with the given initial value.
	 * 
	 * @param progress
	 *            the initial progress value
	 */
	public XdevProgressBar(final float progress)
	{
		super(progress);
	}
	
	
	/**
	 * Creates a new progress bar bound to the given data source.
	 * 
	 * @param dataSource
	 *            the property to bind this progress bar to
	 */
	public XdevProgressBar(final Property<?> dataSource)
	{
		super(dataSource);
	}
}
