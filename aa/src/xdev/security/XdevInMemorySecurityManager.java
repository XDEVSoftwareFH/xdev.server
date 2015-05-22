/*
 * XDEV Enterprise Framework
 * 
 * Copyright (c) 2011 - 2014, XDEV Software and/or its affiliates. All rights reserved.
 * Use is subject to license terms.
 */
package xdev.security;

import static net.jadoth.Jadoth.notNull;

import java.io.File;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.jadoth.collections.types.XMap;
import xdev.security.authentication.AuthenticationFailedException;
import xdev.security.authentication.Authenticator;
import xdev.security.authentication.CredentialsUsernamePassword;
import xdev.security.authentication.InMemoryAuthenticator;
import xdev.security.authentication.XmlInMemoryAuthenticatorProvider;
import xdev.security.authorization.AuthorizationManager;
import xdev.security.authorization.AuthorizationRegistry;
import xdev.security.authorization.Permission;
import xdev.security.authorization.Resource;
import xdev.security.authorization.Role;
import xdev.security.authorization.Subject;


/**
 * More specific {@link XdevSecurityManager} type that defines {@link Authenticator} type parameters to
 * {@link CredentialsUsernamePassword} as the credentials type and {@link Boolean} as the result type for easier
 * usage of simple use cases.
 *
 * @author XDEV Software (TM)
 */
public interface XdevInMemorySecurityManager extends XdevSecurityManager<CredentialsUsernamePassword, Boolean>
{
	/**
	 * A specialized version of {@link #authenticate(CredentialsUsernamePassword)} that wraps the passed
	 * username and password in a new {@link CredentialsUsernamePassword} instance.
	 *
	 * @param username the username to be used in the credentials.
	 * @param password the password to be used in the credentials.
	 * @return the relayed return value of {@link #authenticate(CredentialsUsernamePassword)}.
	 * @throws AuthenticationFailedException relays from {@link #authenticate(CredentialsUsernamePassword)}
	 */
	public default boolean authenticate(final String username, final String password) throws AuthenticationFailedException
	{
		return this.authenticate(CredentialsUsernamePassword.New(username, password));
	}

	/**
	 * A variation of {@link #login(String, String, Consumer, Consumer, BiConsumer)} that simply throws along any
	 * encountered exception
	 *
	 * @param username      the username to be used in the login process for authentication and {@link Subject} lookup.
	 * @param password      the password to be used in the login process for authentication.
	 * @param successAction the action to be executed upon a successful authentication.
	 * @param failAction    the action to be executed upon a unsuccessful authentication.
	 */
	public default void login(
		final String                    username     ,
		final String                    password     ,
		final Consumer<? super Subject> successAction,
		final Consumer<? super String>  failAction
	)
	{
		this.login(username, password, successAction, failAction, (u, e) -> { throw e; });
	}

	/**
	 * Specialized method for abstracting a typical login process:<br>
	 * If the passed username and password are successfully authenticated, the {@link Subject} instance identified
	 * by the username is determined and given to the passed <tt>successAction</tt> {@link Consumer}. Otherwise, the
	 * username is given to the passed <tt>failAction</tt> {@link Consumer}.<br>
	 * If any {@link RuntimeException} occurs during either of these actions, it is relayed to the passed
	 * <tt>exceptionHandler</tt> {@link BiConsumer} along with the username whose processing caused the exception.
	 * <p>
	 * In the simplest case, the passed exception handler can just pass the exception along.
	 * Also see {@link #login(String, String, Consumer, Consumer)}.
	 *
	 * @param username      the username to be used in the login process for authentication and {@link Subject} lookup.
	 * @param password      the password to be used in the login process for authentication.
	 * @param successAction the action to be executed upon a successful authentication.
	 * @param failAction    the action to be executed upon a unsuccessful authentication.
	 * @param exceptionHandler the callback logic to handle any occurring {@link RuntimeException} exception.
	 * @see #login(String, String, Consumer, Consumer)
	 */
	public default void login(
		final String                                               username        ,
		final String                                               password        ,
		final Consumer<? super Subject>                            successAction   ,
		final Consumer<? super String>                             failAction      ,
		final BiConsumer<? super String, ? super RuntimeException> exceptionHandler
	)
	{
		try
		{
			if(this.authenticate(username, password))
			{
				successAction.accept(this.subject(username));
			}
			else
			{
				failAction.accept(username);
			}
		}
		catch(final RuntimeException e)
		{
			exceptionHandler.accept(username, e);
			return;
		}
	}



	/**
	 * Creates a new {@link XdevInMemorySecurityManager} instance using the passed {@link Authenticator} and
	 * {@link AuthorizationManager} instances as its internal delegates.
	 *
	 * @param authenticator        the authenticator to be used.
	 * @param authorizationManager the authorizationManager to be used.
	 * @return a new {@link XdevInMemorySecurityManager} instance
	 */
	public static XdevInMemorySecurityManager New(
		final Authenticator<CredentialsUsernamePassword, Boolean> authenticator       ,
		final AuthorizationManager                                authorizationManager
	)
	{
		return new Implementation(
			notNull(authenticator),
			notNull(authorizationManager)
		);
	}

	/**
	 * Creates a new  {@link XdevInMemorySecurityManager} instance by using default implementations to
	 * load the required configurations from the passed xml {@link File}.
	 *
	 * @param xmlFile the file assumed to contain an XML content valid for default processing.
	 * @return a new  {@link XdevInMemorySecurityManager} instance
	 * @see XmlInMemoryAuthenticatorProvider#provideAuthenticatorFromFile(File)
	 * @see AuthorizationManager#NewFromXmlFile(File)
	 */
	public static XdevInMemorySecurityManager NewFromXml(final File xmlFile)
	{
		final InMemoryAuthenticator authenticator  =
			XmlInMemoryAuthenticatorProvider.provideAuthenticatorFromFile(xmlFile)
		;
		final AuthorizationManager authorizationManager = AuthorizationManager.NewFromXmlFile(xmlFile);

		return New(authenticator, authorizationManager);
	}



	/**
	 * Default {@link XdevInMemorySecurityManager} implementation that wraps delegate {@link Authenticator} and
	 * {@link AuthorizationManager} instances.
	 *
	 * @author XDEV Software (TM)
	 */
	public final class Implementation implements XdevInMemorySecurityManager
	{
		///////////////////////////////////////////////////////////////////////////
		// instance fields //
		////////////////////

		private final Authenticator<CredentialsUsernamePassword, Boolean> authenticator       ;
		private final AuthorizationManager                                authorizationManager;



		///////////////////////////////////////////////////////////////////////////
		// constructors //
		/////////////////

		/**
		 * Implementation detail constructor that might change in the future.
		 */
		Implementation(
			final Authenticator<CredentialsUsernamePassword, Boolean> authenticator       ,
			final AuthorizationManager                                authorizationManager

		)
		{
			super();
			this.authenticator        = authenticator       ;
			this.authorizationManager = authorizationManager;
		}



		///////////////////////////////////////////////////////////////////////////
		// override methods //
		/////////////////////

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final Boolean authenticate(final CredentialsUsernamePassword credentials) throws AuthenticationFailedException
		{
			return this.authenticator.authenticate(credentials);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final Permission providePermission(final Resource resource, final Integer factor)
		{
			return this.authorizationManager.providePermission(resource, factor);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final XMap<String, Role> roles()
		{
			return this.authorizationManager.roles();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final XMap<String, Subject> subjects()
		{
			return this.authorizationManager.subjects();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final Permission providePermission(final Resource resource)
		{
			return this.authorizationManager.providePermission(resource);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final Permission permission(final Resource resource, final Integer factor)
		{
			return this.authorizationManager.permission(resource, factor);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final Role role(final String roleName)
		{
			return this.authorizationManager.role(roleName);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final Subject subject(final String subjectName)
		{
			return this.authorizationManager.subject(subjectName);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final Object lockPermissionRegistry()
		{
			return this.authorizationManager.lockPermissionRegistry();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final Object lockRoleRegistry()
		{
			return this.authorizationManager.lockRoleRegistry();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final Object lockSubjectRegistry()
		{
			return this.authorizationManager.lockSubjectRegistry();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final Permission permission(final Resource resource)
		{
			return this.authorizationManager.permission(resource);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final AuthorizationRegistry authorizationRegistry()
		{
			return this.authorizationManager.authorizationRegistry();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void reloadAuthorizations()
		{
			this.authorizationManager.reloadAuthorizations();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final Resource resource(final String name)
		{
			return this.authorizationManager.resource(name);
		}

	}

}
