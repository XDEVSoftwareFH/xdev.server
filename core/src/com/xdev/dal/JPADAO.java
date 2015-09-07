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

package com.xdev.dal;


import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Criteria;
import org.hibernate.Session;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import com.googlecode.genericdao.dao.jpa.JPABaseDAO;
import com.googlecode.genericdao.search.ExampleOptions;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.ISearch;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.jpa.JPAAnnotationMetadataUtil;
import com.googlecode.genericdao.search.jpa.JPASearchProcessor;
import com.xdev.communication.EntityManagerUtils;


@SuppressWarnings("unchecked")
public class JPADAO<T, ID extends Serializable> extends JPABaseDAO implements GenericDAO<T, ID>
{
	private final Class<T> persistentClass;


	public JPADAO(final Class<T> persistentClass)
	{
		this.persistentClass = persistentClass;
		this.setSearchProcessor(new JPASearchProcessor(new JPAAnnotationMetadataUtil()));
	}


	@Override
	protected EntityManager em()
	{
		return EntityManagerUtils.getEntityManager();
	}


	public void beginTransaction()
	{
		em().getTransaction().begin();
	}


	public void rollback()
	{
		em().getTransaction().rollback();
	}


	public void commit()
	{
		em().getTransaction().commit();
	}


	public CriteriaQuery<T> buildCriteriaQuery(final Class<T> exampleType)
	{
		final CriteriaBuilder cb = em().getCriteriaBuilder();
		return cb.createQuery(exampleType);
	}


	public Criteria buildHibernateCriteriaQuery(final Class<T> exampleType)
	{
		return em().unwrap(Session.class).createCriteria(exampleType);
	}
	
	
	public Criteria buildHibernateCriteriaQuery(final Class<T> exampleType, final String alias)
	{
		return em().unwrap(Session.class).createCriteria(exampleType,alias);
	}


	@Override
	public int count(ISearch search)
	{
		if(search == null)
		{
			search = new Search();
		}
		return _count(this.persistentClass,search);
	}


	@Override
	public T find(final ID id)
	{
		return _find(this.persistentClass,id);
	}


	@Override
	public T[] find(final ID... ids)
	{
		return _find(this.persistentClass,ids);
	}


	@Override
	public List<T> findAll()
	{
		return _all(this.persistentClass);
	}


	@Override
	public void flush()
	{
		_flush();
	}


	@Override
	public T getReference(final ID id)
	{
		return _getReference(this.persistentClass,id);
	}


	@Override
	public T[] getReferences(final ID... ids)
	{
		return _getReferences(this.persistentClass,ids);
	}


	@Override
	public boolean isAttached(final T entity)
	{
		return _contains(entity);
	}


	@Override
	public void refresh(final T... entities)
	{
		_refresh(entities);
	}


	@Override
	public boolean remove(final T entity)
	{
		return _removeEntity(entity);
	}


	@Override
	public void remove(final T... entities)
	{
		_removeEntities((Object[])entities);
	}


	@Override
	public boolean removeById(final ID id)
	{
		return _removeById(this.persistentClass,id);
	}


	@Override
	public void removeByIds(final ID... ids)
	{
		_removeByIds(this.persistentClass,ids);
	}


	@Override
	public T merge(final T entity)
	{
		return _merge(entity);
	}


	@Override
	public T[] merge(final T... entities)
	{
		return _merge(this.persistentClass,entities);
	}


	@Override
	public void persist(final T... entities)
	{
		_persist(entities);
	}


	@Override
	public T save(final T entity)
	{
		return _persistOrMerge(entity);
	}


	@Override
	public T[] save(final T... entities)
	{
		return _persistOrMerge(this.persistentClass,entities);
	}


	@Override
	public <RT> List<RT> search(final ISearch search)
	{
		if(search == null)
		{
			return (List<RT>)findAll();
		}
		return _search(this.persistentClass,search);
	}


	@Override
	public <RT> SearchResult<RT> searchAndCount(final ISearch search)
	{
		if(search == null)
		{
			final SearchResult<RT> result = new SearchResult<RT>();
			result.setResult((List<RT>)findAll());
			result.setTotalCount(result.getResult().size());
			return result;
		}
		return _searchAndCount(this.persistentClass,search);
	}


	@Override
	public <RT> RT searchUnique(final ISearch search)
	{
		return (RT)_searchUnique(this.persistentClass,search);
	}


	@Override
	public Filter getFilterFromExample(final T example)
	{
		return _getFilterFromExample(example);
	}


	@Override
	public Filter getFilterFromExample(final T example, final ExampleOptions options)
	{
		return _getFilterFromExample(example,options);
	}
}
