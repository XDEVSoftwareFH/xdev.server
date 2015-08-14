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

package com.xdev.communication;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;

import org.hibernate.FlushMode;
import org.hibernate.Session;

import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionExpiredException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;


/**
 * Manages Session propagation.
 *
 * @author XDEV Software
 *		
 */
public interface VaadinSessionStrategy
{
	public void handleRequest(VaadinRequest request, VaadinService service);
	
	
	public void requestEnd(VaadinRequest request, VaadinService service, VaadinSession session);
	
	
	
	/**
	 * Request / Response propagation to avoid session per operation anti
	 * pattern.
	 *
	 * @author XDEV Software
	 *
	 */
	public class PerRequest implements VaadinSessionStrategy
	{
		
		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.xdev.communication.VaadinSessionPerStrategy#handleRequest(com.
		 * vaadin.server.VaadinRequest)
		 */
		@Override
		public void handleRequest(final VaadinRequest request, final VaadinService service)
		{
			final EntityManagerFactory factory = EntityManagerUtil.getEntityManagerFactory();
			VaadinSession session;
			try
			{
				session = service.findVaadinSession(request);
			}
			catch(ServiceException | SessionExpiredException e)
			{
				throw new RuntimeException(e);
			}
			
			final EntityManager manager = factory.createEntityManager();
			
			// instantiate conversationable wrapper with entity manager.
			final Conversationable.Implementation conversationable = new Conversationable.Implementation();
			conversationable.setEntityManager(manager);
			
			// Begin a database transaction, start the unit of work
			manager.getTransaction().begin();
			
			// Add the EntityManager to the vaadin session
			session.setAttribute(EntityManagerUtil.ENTITY_MANAGER_ATTRIBUTE,conversationable);
		}
		
		
		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.xdev.communication.VaadinSessionStrategy#requestEnd(com.vaadin.
		 * server.VaadinRequest, com.vaadin.server.VaadinService,
		 * com.vaadin.server.VaadinSession)
		 */
		@Override
		public void requestEnd(final VaadinRequest request, final VaadinService service,
				final VaadinSession session)
		{
			final EntityManager em = EntityManagerUtil.getEntityManager();
			if(em != null)
			{
				if(EntityManagerUtil.getConversation() != null)
				{
					/*
					 * Keep the session and with it the persistence context
					 * alive during user think time while a conversation is
					 * active. The next request will automatically be handled by
					 * an appropriate conversation managing strategy.
					 */
					if(EntityManagerUtil.getConversation().isActive())
					{
						try
						{
							// end unit of work
							em.getTransaction().commit();
						}
						catch(final RollbackException e)
						{
							em.getTransaction().rollback();
						}
					}
				}
				else
				{
					try
					{
						EntityManagerUtil.closeEntityManager();
					}
					catch(final Exception e)
					{
						if(em != null)
						{
							final EntityTransaction tx = EntityManagerUtil.getTransaction();
							if(tx != null && tx.isActive())
							{
								EntityManagerUtil.rollback();
							}
						}
					}
				}
			}
			
		}
	}
	
	
	
	/**
	 * Extended persistence context pattern.
	 *
	 * @author XDEV Software
	 *		
	 */
	public class PerConversation implements VaadinSessionStrategy
	{
		/**
		 *
		 */
		public PerConversation()
		{
			final EntityManager em = EntityManagerUtil.getEntityManager();
			if(em != null)
			{
				/*
				 * Prepare conversation - disable AUTO flush mode for each
				 * transaction commit, to achieve the conversation unit of work
				 * context.
				 */
				em.unwrap(Session.class).setFlushMode(FlushMode.MANUAL);
			}
		}
		
		
		/*
		 * (non-Javadoc)
		 *
		 * @see com.xdev.communication.VaadinSessionStrategy#handleRequest(com.
		 * vaadin .server.VaadinRequest)
		 */
		@Override
		public void handleRequest(final VaadinRequest request, final VaadinService service)
		{
			final EntityManager em = EntityManagerUtil.getEntityManager();
			if(em != null)
			{
				/*
				 * Begin a database transaction, reconnects Session - continues
				 * the unit of work
				 */
				em.getTransaction().begin();
			}
		}
		
		
		/*
		 * (non-Javadoc)
		 *
		 * @see com.xdev.communication.VaadinSessionStrategy#requestEnd(com.
		 * vaadin. server.VaadinRequest, com.vaadin.server.VaadinService,
		 * com.vaadin.server.VaadinSession)
		 */
		@Override
		public void requestEnd(final VaadinRequest request, final VaadinService service,
				final VaadinSession session)
		{
			final EntityManager em = EntityManagerUtil.getEntityManager();
			final Conversation conversation = EntityManagerUtil.getConversation();
			if(conversation != null)
			{
				if(conversation.isActive())
				{
					// Event was not the last request, continue conversation
					try
					{
						em.getTransaction().commit();
					}
					catch(final RollbackException e)
					{
						em.getTransaction().rollback();
					}
					
				}
				else
				{
					/*
					 * The event was the last request: flush (sync with db),
					 * commit, close
					 */
					em.flush();
					try
					{
						em.getTransaction().commit();
					}
					catch(final RollbackException e)
					{
						em.getTransaction().rollback();
					}
					em.close();
					
					// set flush mode back to default
					em.unwrap(Session.class).setFlushMode(FlushMode.AUTO);
				}
			}
			else
			{
				System.out.println("in conversation mode but conversation null");
			}
		}
	}
	
	
	
	/**
	 * Extended persistence context pattern.
	 *
	 * @author XDEV Software
	 *		
	 */
	public class PerConversationPessimistic implements VaadinSessionStrategy
	{
		/**
		 *
		 */
		public PerConversationPessimistic()
		{
			final EntityManager em = EntityManagerUtil.getEntityManager();
			if(em != null)
			{
				/*
				 * Prepare conversation - disable AUTO flush mode for each
				 * transaction commit, to achieve the conversation unit of work
				 * context.
				 */
				em.unwrap(Session.class).setFlushMode(FlushMode.MANUAL);
				/*
				 * Begin a database transaction, reconnects Session - continues
				 * the unit of work
				 */
				em.getTransaction().begin();
			}
		}
		
		
		/*
		 * (non-Javadoc)
		 *
		 * @see com.xdev.communication.VaadinSessionStrategy#handleRequest(com.
		 * vaadin .server.VaadinRequest)
		 */
		@Override
		public void handleRequest(final VaadinRequest request, final VaadinService service)
		{
		}
		
		
		/*
		 * (non-Javadoc)
		 *
		 * @see com.xdev.communication.VaadinSessionStrategy#requestEnd(com.
		 * vaadin. server.VaadinRequest, com.vaadin.server.VaadinService,
		 * com.vaadin.server.VaadinSession)
		 */
		@Override
		public void requestEnd(final VaadinRequest request, final VaadinService service,
				final VaadinSession session)
		{
			final EntityManager em = EntityManagerUtil.getEntityManager();
			final Conversation conversation = EntityManagerUtil.getConversation();
			if(conversation != null)
			{
				if(!conversation.isActive())
				{
					/*
					 * The event was the last request: flush (sync with db),
					 * commit, close
					 */
					em.flush();
					try
					{
						em.getTransaction().commit();
					}
					catch(final RollbackException e)
					{
						em.getTransaction().rollback();
					}
					em.close();
					
					// set flush mode back to default
					em.unwrap(Session.class).setFlushMode(FlushMode.AUTO);
				}
			}
		}
	}
	
}