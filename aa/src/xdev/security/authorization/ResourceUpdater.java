/*
 * XDEV Enterprise Framework
 * 
 * Copyright (c) 2011 - 2014, XDEV Software and/or its affiliates. All rights reserved.
 * Use is subject to license terms.
 */
package xdev.security.authorization;

import net.jadoth.collections.types.XGettingCollection;
import net.jadoth.collections.types.XGettingEnum;


/**
 * Functional type that updates {@link Resource} instances.
 * For more details, see {@link #updateResource(Resource, String, XGettingEnum)}.
 *
 * @author XDEV Software (TM)
 */
@FunctionalInterface
public interface ResourceUpdater
{
	/**
	 * Updates the passed {@link Resource} instance for the given resource name and collection of child resources,
	 * where updating can mean anything from just validating to actually changing the passed resource's state with
	 * the passed content.
	 * @param resource the {@link Resource} instance to be updated.
	 * @param resourceName the identifying name of the passed {@link Resource} instance.
	 * @param children the resource's defined children to be used for the updating process (potentially empty).
	 */
	public void updateResource(Resource resource, String resourceName, XGettingEnum<Resource> children);



	/**
	 * Updating preparation callback hook that gets called once on the beginning of very updating process, before any
	 * authorization instances are updated.
	 * The default implementation of this method is empty.
	 * An example usage for this method is to create and store rollback information on the passed resources.
	 *
	 * @param existingResources the resources already existing before the updating process.
	 */
	public default void prepareResourceUpdate(final XGettingCollection<Resource> existingResources)
	{
		// no-op in default implementation
	}

	/**
	 * Update committing callback hook that gets called once at the end of very successful updating process, after all
	 * authorization instances are updated.
	 * The default implementation of this method is empty.
	 * An example usage for this method is to relay committing of changes to some other data structure or application
	 * part.
	 *
	 * @param updatedResources the updated and potentially newly created resources.
	 */
	public default void commitResourceUpdate(final XGettingCollection<Resource> updatedResources)
	{
		// no-op in default implementation
	}

	/**
	 * Updating exception handling callback hook that gets called once if any {@link Exception} is encountered during
	 * the updating process.
	 * The default implementation of this method is empty.
	 * An example usage for this method is to rollback (revert) all mutated {@link Resource} instances that have
	 * already existed before the updating process.
	 *
	 * @param updatedResources the updated and potentially newly created, empty or inconsistent resources.
	 */
	public default void rollbackResourceUpdate(final XGettingCollection<Resource> updatedResources, final Exception cause)
	{
		// no-op in default implementation
	}

	/**
	 * Updating preparation callback hook that gets called once at the end of the updating process in any case
	 * (both success and encountered exception).
	 * The default implementation of this method is empty.
	 * An example usage for this method is to clear any internally stored meta data (e.g. rollback information).
	 */
	public default void cleanupResourceUpdate()
	{
		// no-op in default implementation
	}

}
