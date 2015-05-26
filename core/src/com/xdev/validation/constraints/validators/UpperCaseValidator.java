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

package com.xdev.validation.constraints.validators;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.xdev.validation.constraints.UpperCase;


/**
 * @author XDEV Software
 *
 */

public class UpperCaseValidator implements ConstraintValidator<UpperCase, String>
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
	public boolean isValid(final String value, final ConstraintValidatorContext context)
	{
		if(value == null)
		{
			return true;
		}

		return value.toUpperCase().equals(value);
	}

}
