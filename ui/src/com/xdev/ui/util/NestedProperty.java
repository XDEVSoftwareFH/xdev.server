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

package com.xdev.ui.util;


import java.util.Arrays;
import java.util.stream.Collectors;

import javax.persistence.metamodel.Attribute;


public class NestedProperty<K, V> implements KeyValueType<K, V>
{
	private final K			property;
	private final V			value;
	private final Class<V>	type;


	public NestedProperty(final K property, final V value, final Class<V> type)
	{
		this.property = property;
		this.type = type;
		this.value = value;
	}


	public static <K, V> KeyValueType<K, V> of(final K key, final V value, final Class<V> type)
	{
		final KeyValueType<K, V> pair = new NestedProperty<>(key,value,type);
		return pair;
	}


	public static <K, V> KeyValueType<K, V> of(final K key, final Class<V> type)
	{
		final KeyValueType<K, V> pair = new NestedProperty<>(key,null,type);
		return pair;
	}


	public static <K> KeyValueType<K, Object> of(final K key)
	{
		final KeyValueType<K, Object> pair = new NestedProperty<>(key,null,Object.class);
		return pair;
	}


	/**
	 *
	 * @param attributes
	 * @return
	 * @since 3.0
	 */
	public static KeyValueType<String, ?> of(final Attribute<?, ?>... attributes)
	{
		return of(path(attributes),attributes[attributes.length - 1].getJavaType());
	}


	public static String path(final Attribute<?, ?>... attributes)
	{
		return Arrays.stream(attributes).map(Attribute::getName).collect(Collectors.joining("."));
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public K getKey()
	{
		return this.property;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public V getValue()
	{
		return this.value;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<V> getType()
	{
		return this.type;
	}
}
