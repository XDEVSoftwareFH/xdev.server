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


import java.util.List;

import com.vaadin.server.Resource;
import com.vaadin.ui.Audio;


/**
 * The Audio component translates into an HTML5 &lt;audio&gt; element and as
 * such is only supported in browsers that support HTML5 media markup. Browsers
 * that do not support HTML5 display the text or HTML set by calling
 * {@link #setAltText(String)}.
 *
 * A flash-player fallback can be implemented by setting HTML content allowed (
 * {@link #setHtmlContentAllowed(boolean)} and calling
 * {@link #setAltText(String)} with the flash player markup. An example of flash
 * fallback can be found at the <a href=
 * "https://developer.mozilla.org/En/Using_audio_and_video_in_Firefox#Using_Flash"
 * >Mozilla Developer Network</a>.
 *
 * Multiple sources can be specified. Which of the sources is used is selected
 * by the browser depending on which file formats it supports. See <a
 * href="http://en.wikipedia.org/wiki/HTML5_video#Table">wikipedia</a> for a
 * table of formats supported by different browsers.
 *
 * @author XDEV Software
 *
 */
public class XdevAudio extends Audio
{
	/**
	 *
	 */
	public XdevAudio()
	{
		super();
	}
	
	
	/**
	 * @param caption
	 *            The caption of the audio component.
	 */
	public XdevAudio(final String caption)
	{
		super(caption);
	}
	
	
	/**
	 * @param caption
	 *            The caption of the audio component
	 * @param source
	 *            The audio file to play.
	 */
	public XdevAudio(final String caption, final Resource source)
	{
		super(caption,source);
	}
	
	
	/**
	 * @return the first source pointed to in this media
	 */
	public Resource getSource()
	{
		final List<Resource> sources = getSources();
		if(sources != null && sources.size() > 0)
		{
			return sources.get(0);
		}
		return null;
	}
}
