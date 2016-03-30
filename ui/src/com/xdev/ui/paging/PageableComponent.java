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

package com.xdev.ui.paging;


public interface PageableComponent<ET>
{
	// Request next page from ScrollableResultSet
	public void nextPage();


	// Request previous page from ScrollableResultSet
	public void previousPage();


	public void lastPage();


	public void firstPage();


	// Request last page from ScrollableResultSet
	public int getTotalAmountOfPages();


	// ...
	public void setCurrentPage(int currentPage);


	public int getCurrentPage();


	public void setPageLength(int pageLength);


	public int getPageLength();
}
