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

package com.xdev.ui.event;


import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.EventListener;

import com.vaadin.util.ReflectTools;
import com.xdev.ui.XdevUI;


/**
 * Event listener interface for listening to focus change events.
 * {@link #focusChanged(FocusChangeEvent)} is always called when the focused
 * component changes.
 *
 * @see XdevUI#addFocusChangeListener(FocusChangeListener)
 * 		
 * @author XDEV Software
 * 		
 */
public interface FocusChangeListener extends EventListener, Serializable
{
	public static final Method focusChangedMethod = ReflectTools
			.findMethod(FocusChangeListener.class,"focusChanged",FocusChangeEvent.class);
			
			
	/**
	 * Focused component has been changed.
	 *
	 * @param event
	 */
	public void focusChanged(FocusChangeEvent event);
}
