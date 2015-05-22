/*
 * XDEV Enterprise Framework
 * 
 * Copyright (c) 2011 - 2014, XDEV Software and/or its affiliates. All rights reserved.
 * Use is subject to license terms.
 */
package xdev.security.authorization;

import static net.jadoth.Jadoth.notNull;

import java.util.function.Function;

import net.jadoth.collections.EqHashTable;
import net.jadoth.collections.types.XGettingCollection;
import net.jadoth.collections.types.XGettingMap;

/**
 * A registry type that provides a means for centralized lookup and iteration of known {@link Permission} instances.
 * Note that a registry conceptually only provides reading access, not modifying access to its contents. For
 * modifying operations, see {@link PermissionManager}.
 *
 * @author XDEV Software (TM)
 */
public interface PermissionRegistry
{
	/**
	 * Looks up the {@link Permission} instance representing the passed {@link Resource} instance with the passed
	 * factor value. If no suitable {@link Permission} instance can be found, <tt>null</tt> is returned.
	 *
	 * @param  resource the {@link Resource} instance for which the associated {@link Permission} instance shall be
	 *         returned.
	 * @param  factor the factor value of the {@link Resource} instance for which the associated {@link Permission}
	 *         instance shall be returned.
	 * @return the associated {@link Permission} instance specified by the passed values.
	 */
	public Permission permission(Resource resource, Integer factor);

	/**
	 * Returns the lock instance that is internally used by this registry instance.
	 *
	 * @return the lock.
	 */
	public Object lockPermissionRegistry();



	///////////////////////////////////////////////////////////////////////////
	// default methods  //
	/////////////////////

	/**
	 * Looks up the {@link Permission} instance representing the passed {@link Resource} instance with a factor of 0.
	 * If no suitable {@link Permission} instance can be found, <tt>null</tt> is returned.
	 *
	 * @param  resource the {@link Resource} instance for which the associated {@link Permission} instance shall be
	 *         returned.
	 * @return the associated {@link Permission} instance specified by the passed {@link Resource} instance.
	 */
	public default Permission permission(final Resource resource)
	{
		return this.permission(resource, 0);
	}



	/**
	 * Creates a new {@link PermissionRegistry} instance using the passed map instance as its internal datastructure
	 * and the passed registryLock instance as the synchronization lock for accessing the registry.
	 *
	 * @param registry the map instance to be used as the internal datastructure.
	 * @param registryLock the locking instance to be used to synchronize on for accessing the registry.
	 * @return a new {@link PermissionRegistry} instance using the passed instances.
	 */
	public static PermissionRegistry New(
		final XGettingMap<Resource, ? extends XGettingMap<Integer, Permission>> registry    ,
		final Object                                                            registryLock
	)
	{
		return new Implementation(
			notNull(registry)    ,
			notNull(registryLock)
		);
	}

	/**
	 * Creates a new {@link PermissionRegistry} instance using the passed map instance as its internal datastructure
	 * and an internally created instance as the synchronization lock for accessing the registry.
	 *
	 * @param registry the map instance to be used as the internal datastructure.
	 * @return a new {@link PermissionRegistry} instance using the passed instances.
	 */
	public static PermissionRegistry New(
		final XGettingMap<Resource, ? extends XGettingMap<Integer, Permission>> registry
	)
	{
		return New(registry, new Object());
	}

	/**
	 * Creates a new {@link PermissionRegistry} instance using the passed {@link Permission} instances to derive its
	 * internal datastructure from and the passed registryLock instance as the synchronization lock for
	 * accessing the registry.
	 *
	 * @param permissions the {@link Permission} instances to derive the internal datastructure from.
	 * @param registryLock the locking instance to be used to synchronize on for accessing the registry.
	 * @return a new {@link PermissionRegistry} instance using the passed instances.
	 */
	public static PermissionRegistry New(
		final XGettingCollection<? extends Permission> permissions ,
		final Object                                   registryLock
	)
	{
		return New(
			Implementation.buildRegistry(notNull(permissions)),
			notNull(registryLock)
		);
	}

	/**
	 * Creates a new {@link PermissionRegistry} instance using the passed {@link Permission} instance to derive its
	 * internal datastructure from and a newly instantiated object to be used as the synchronization lock.
	 *
	 * @param permissions the map instance to be used as the internal datastructure.
	 * @return a new {@link PermissionRegistry} instance using the passed instance.
	 */
	public static PermissionRegistry New(final XGettingCollection<? extends Permission> permissions)
	{
		return New(permissions, new Object());
	}



	/**
	 * A simple {@link PermissionRegistry} default implementation that synchronizes on a provided lock instance for
	 * accessing the internal registry in order to avoid concurrency issues while the internal datastructure is
	 * rebuilt.
	 *
	 * @author XDEV Software (TM)
	 */
	public final class Implementation implements PermissionRegistry
	{
		///////////////////////////////////////////////////////////////////////////
		// static methods  //
		///////////////////

		static final EqHashTable<Resource, EqHashTable<Integer, Permission>> buildRegistry(
			final XGettingCollection<? extends Permission> permissions
		)
		{
			// (28.05.2014 TM)TODO: make HashEqualator configurable. Consolidate with Manager.
			final EqHashTable<Resource, EqHashTable<Integer, Permission>> registry = EqHashTable.New();
			final Function<Resource, EqHashTable<Integer, Permission>>    supplier = r -> EqHashTable.New();

			for(final Permission permission : permissions)
			{
				registry
				.ensure(permission.resource(), supplier)
				.add(permission.factor(), permission)
				;
			}

			return registry;
		}



		///////////////////////////////////////////////////////////////////////////
		// instance fields //
		////////////////////

		private final XGettingMap<Resource, ? extends XGettingMap<Integer, Permission>> registry    ;
		private final Object                                                            registryLock;



		///////////////////////////////////////////////////////////////////////////
		// constructors //
		/////////////////

		/**
		 * Implementation detail constructor that might change in the future.
		 */
		Implementation(
			final XGettingMap<Resource, ? extends XGettingMap<Integer, Permission>> registry    ,
			final Object                                                            registryLock
		)
		{
			super();
			this.registry     = registry    ;
			this.registryLock = registryLock;
		}



		///////////////////////////////////////////////////////////////////////////
		// override methods //
		/////////////////////

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final synchronized Permission permission(final Resource resource, final Integer factor)
		{
			synchronized(this.registryLock)
			{
				final XGettingMap<Integer, Permission> resourceMap = this.registry.get(resource);
				return resourceMap != null ? resourceMap.get(factor) :null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final Object lockPermissionRegistry()
		{
			return this.registryLock;
		}

	}

}
