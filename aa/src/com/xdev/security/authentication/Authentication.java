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

package com.xdev.security.authentication;


import com.vaadin.ui.UI;
import com.xdev.security.authorization.Subject;


public class Authentication
{
	
	public static Subject getUser()
	{
		final Subject subject = UI.getCurrent().getSession().getAttribute(Subject.class);
		return subject;
	}
	
	
	public static void setUser(final Subject user, final Object loginInfo)
	{
		// TODO enhance API to avoid type checks
		// if(loginInfo.hasPassedLogin())
		// {
		// UI.getCurrent().getSession().setAttribute(Subject.class,user);
		// }
		
		if(loginInfo != null)
		{
			UI.getCurrent().getSession().setAttribute(Subject.class,user);
		}
		else if(loginInfo instanceof Boolean)
		{
			if((boolean)loginInfo)
			{
				UI.getCurrent().getSession().setAttribute(Subject.class,user);
			}
		}
	}
	
}
