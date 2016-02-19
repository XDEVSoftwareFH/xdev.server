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
 */

package com.xdev.util;


import java.lang.reflect.Field;

import org.hibernate.MappingException;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;

import com.xdev.communication.EntityManagerUtils;


/**
 *
 * @author XDEV Software (JW)
 *		
 */
public class HibernateEntityIDResolver implements EntityIDResolver
{
	private static HibernateEntityIDResolver instance;
	
	
	public static HibernateEntityIDResolver getInstance()
	{
		if(instance == null)
		{
			instance = new HibernateEntityIDResolver();
		}
		
		return instance;
	}
	
	private final Configuration config;
	
	
	private HibernateEntityIDResolver()
	{
		this.config = HibernateMetaDataUtils
				.getConfiguration(EntityManagerUtils.getEntityManager());
	}
	
	
	@Override
	public Property getEntityIDProperty(final Class<?> entityClass)
	{
		String className = entityClass.getName();
		if(className.contains("_$$_"))
		{
			className = className.substring(0,className.indexOf("_$$_"));
		}
		final PersistentClass clazz = this.config.getClassMapping(className);
		final Property idProperty = clazz.getDeclaredIdentifierProperty();
		
		return idProperty;
	}
	
	
	public Property getEntityReferenceProperty(final Class<?> entityClassA,
			final Class<?> entityClassB)
	{
		
		return null;
	}
	
	
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.xdev.util.EntityIDResolver#getEntityIDPropertyValue(java.lang.Class)
	 */
	@Override
	public Object getEntityIDPropertyValue(final Object entity)
	{
		final Property idProperty = getEntityIDProperty(entity.getClass());
		String className = entity.getClass().getName();
		if(className.contains("_$$_"))
		{
			className = className.substring(0,className.indexOf("_$$_"));
		}
		final PersistentClass clazz = this.config.getClassMapping(className);
		Field idField = null;
		try
		{
			idField = clazz.getMappedClass().getDeclaredField(idProperty.getName());
			idField.setAccessible(true);
			return idField.get(entity);
		}
		catch(MappingException | NoSuchFieldException | SecurityException | IllegalArgumentException
				| IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}
}
