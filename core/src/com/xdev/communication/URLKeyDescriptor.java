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

package com.xdev.communication;


import java.io.Serializable;
import java.util.Arrays;


/**
 * @author XDEV Software (JW)
 * 		
 */
public final class URLKeyDescriptor implements Serializable
{
	private final String	viewName;
	private final String	propertyName;
	private final int		hash;
							
							
	public URLKeyDescriptor(final String viewName, final String propertyName)
	{
		this.viewName = viewName;
		this.propertyName = propertyName;
		this.hash = Arrays.hashCode(new Object[]{viewName,propertyName});
	}


	public String getPropertyName()
	{
		return this.propertyName;
	}


	public String getViewName()
	{
		return this.viewName;
	}


	@Override
	public int hashCode()
	{
		return this.hash;
	}


	@Override
	public boolean equals(final Object obj)
	{
		if(this == obj)
		{
			return true;
		}

		return obj instanceof URLKeyDescriptor && this.hash == ((URLKeyDescriptor)obj).hash;
	}

}
