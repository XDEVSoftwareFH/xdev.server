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

package com.xdev.validation.constraints.validators;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.xdev.validation.constraints.UpperCase;


/**
 * @author XDEV Software
 *
 */

public class UpperCaseValidator implements ConstraintValidator<UpperCase, CharSequence>
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize(final UpperCase constraintAnnotation)
	{
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid(final CharSequence value, final ConstraintValidatorContext context)
	{
		if(value == null)
		{
			return true;
		}
		
		final String string = value.toString();
		return string.toUpperCase().equals(string);
	}
}
