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

package com.xdev.util;


import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.log4j.Logger;


/**
 *
 * @author XDEV Software
 * @deprecated will be removed in a future release
 */
@Deprecated
public class JPAEntityReferenceResolver implements EntityReferenceResolver
{
	private static JPAEntityReferenceResolver instance;
	
	
	public static JPAEntityReferenceResolver getInstance()
	{
		if(instance == null)
		{
			instance = new JPAEntityReferenceResolver();
		}
		
		return instance;
	}
	
	
	private JPAEntityReferenceResolver()
	{
	}
	
	
	@Override
	public String getReferenceEntityAttributeName(final Class<?> referenceEntity,
			final Class<?> entity)
	{
		final ManagedType<?> managedType = JPAMetaDataUtils.getManagedType(entity);
		
		final List<String> matchingAttributeNames = managedType.getAttributes().stream()
				.filter(SingularAttribute.class::isInstance).map(SingularAttribute.class::cast)
				.filter(pa -> pa.getBindableJavaType().equals(referenceEntity)
						&& (pa.getPersistentAttributeType() == PersistentAttributeType.MANY_TO_ONE
								|| pa.getPersistentAttributeType() == PersistentAttributeType.ONE_TO_ONE))
				.map(Attribute::getName).collect(Collectors.toList());
		
		if(matchingAttributeNames.isEmpty())
		{
			Logger.getLogger(JPAEntityReferenceResolver.class)
					.info("No matching reference attribute for relation: "
							+ entity.getCanonicalName() + " -> "
							+ referenceEntity.getCanonicalName());
			
			return null;
		}
		
		if(matchingAttributeNames.size() > 1)
		{
			Logger.getLogger(JPAEntityReferenceResolver.class)
					.info("Multiple matching reference attributes for relation: "
							+ entity.getCanonicalName() + " -> "
							+ referenceEntity.getCanonicalName() + " ["
							+ matchingAttributeNames.stream().collect(Collectors.joining(", "))
							+ "]");
		}
		
		return matchingAttributeNames.get(0);
	}
}
