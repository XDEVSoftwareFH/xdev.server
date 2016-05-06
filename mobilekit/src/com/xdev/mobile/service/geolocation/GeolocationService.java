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
 *
 * For further information see
 * <http://www.rapidclipse.com/en/legal/license/license.html>.
 */

package com.xdev.mobile.service.geolocation;


import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.vaadin.annotations.JavaScript;
import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.Page;
import com.xdev.mobile.service.MobileService;
import com.xdev.mobile.service.MobileServiceDescriptor;
import com.xdev.mobile.service.MobileServiceError;

import elemental.json.JsonArray;
import elemental.json.JsonObject;


/**
 * This service provides information about the device's location, such as
 * latitude and longitude. Common sources of location information include Global
 * Positioning System (GPS) and location inferred from network signals such as
 * IP address, RFID, WiFi and Bluetooth MAC addresses, and GSM/CDMA cell IDs.
 * There is no guarantee that the API returns the device's actual location.
 * <p>
 * <b>WARNING</b>:<br>
 * Collection and use of geolocation data raises important privacy issues. Your
 * app's privacy policy should discuss how the app uses geolocation data,
 * whether it is shared with any other parties, and the level of precision of
 * the data (for example, coarse, fine, ZIP code level, etc.). Geolocation data
 * is generally considered sensitive because it can reveal user's whereabouts
 * and, if stored, the history of their travels. Therefore, in addition to the
 * app's privacy policy, you should strongly consider providing a just-in-time
 * notice before the app accesses geolocation data (if the device operating
 * system doesn't do so already). That notice should provide the same
 * information noted above, as well as obtaining the user's permission (e.g., by
 * presenting choices for OK and No Thanks). For more information, please see
 * the <a href=
 * "http://cordova.apache.org/docs/en/latest/guide/appdev/privacy/index.html">
 * Privacy Guide</a>.
 *
 * @author XDEV Software
 *
 */

@MobileServiceDescriptor("geolocation-descriptor.xml")
@JavaScript("geolocation.js")
public class GeolocationService extends MobileService
{
	
	/**
	 * Returns the geolocation service.<br>
	 * To activate the service it has to be registered in the mobile.xml.
	 *
	 * <pre>
	 * {@code
	 * ...
	 * <service>com.xdev.mobile.service.geolocation.GeolocationService</service>
	 * ...
	 * }
	 * </pre>
	 *
	 * @return the geolocation service if available
	 */
	public static GeolocationService getInstance()
	{
		return getMobileService(GeolocationService.class);
	}

	private final Map<String, ServiceCall<Position>>	getCalls	= new HashMap<>();
	private final Map<String, ServiceCall<Geolocation>>	watchcalls	= new HashMap<>();
	private final Map<String, Position>					waitMap		= new HashMap<>();


	public GeolocationService(final AbstractClientConnector connector)
	{
		super(connector);

		this.addFunction("geolocation_get_success",this::geolocation_get_success);
		this.addFunction("geolocation_get_error",this::geolocation_get_error);

		this.addFunction("geolocation_watch_success",this::geolocation_watch_success);
		this.addFunction("geolocation_watch_error",this::geolocation_watch_error);

		this.addFunction("geolocation_get_future_success",this::geolocation_get_future_success);
		this.addFunction("geolocation_get_future_error",this::geolocation_get_future_error);
	}


	/**
	 * Asynchronously acquires the current position.
	 *
	 * @param successCallback
	 *            The function to call when the position data is available
	 * @param errorCallback
	 *            The function to call when there is an error getting the
	 *            heading position.
	 */
	public synchronized void getCurrentPosition(final Consumer<Position> successCallback,
			final Consumer<MobileServiceError> errorCallback)
	{
		final String id = generateCallerID();
		final ServiceCall<Position> call = ServiceCall.async(successCallback,errorCallback);
		this.getCalls.put(id,call);

		final StringBuilder js = new StringBuilder();
		js.append("geolocation_get('").append(id).append("');");

		Page.getCurrent().getJavaScript().execute(js.toString());
	}


	private void geolocation_get_success(final JsonArray arguments)
	{
		final String id = arguments.getString(0);
		final ServiceCall<Position> call = this.getCalls.remove(id);
		if(call != null)
		{
			final Gson gson = new Gson();
			final JsonObject jsonObject = arguments.getObject(1);
			final Position position = gson.fromJson(jsonObject.toJson(),Position.class);
			call.success(position);
		}
	}


	private void geolocation_get_error(final JsonArray arguments)
	{
		final String id = arguments.getString(0);
		final ServiceCall<Position> call = this.getCalls.remove(id);
		if(call != null)
		{
			call.error(new MobileServiceError(this,arguments.get(1).asString()));
		}
	}


	/**
	 *
	 * Asynchronously watches the geolocation for changes to geolocation. When a
	 * change occurs, the successCallback is called with the new location.
	 *
	 * @param successCallback
	 *            The function to call each time the location data is available
	 * @param errorCallback
	 *            The function to call when there is an error getting the
	 *            location data.
	 * @param timeout
	 */

	public synchronized void watchPosition(final Consumer<Geolocation> successCallback,
			final Consumer<MobileServiceError> errorCallback, final int timeout)
	{
		final String id = generateCallerID();
		final ServiceCall<Geolocation> call = ServiceCall.async(successCallback,errorCallback);
		this.watchcalls.put(id,call);

		final StringBuilder js = new StringBuilder();
		js.append("geolocation_watch('").append(id).append("',");
		appendTimeout(js,timeout);
		js.append(");");

		Page.getCurrent().getJavaScript().execute(js.toString());
	}


	private void appendTimeout(final StringBuilder js, final int timeout)
	{
		js.append("{ ");
		js.append("timeout: ").append(timeout).append(" }");
	}


	private void geolocation_watch_success(final JsonArray arguments)
	{
		final String id = arguments.getString(0);
		final ServiceCall<Geolocation> call = this.watchcalls.get(id);
		if(call != null)
		{
			final Gson gson = new Gson();
			final JsonObject jsonObject = arguments.getObject(1);
			final Position position = gson.fromJson(jsonObject.toJson(),Position.class);
			final double watchID = arguments.getNumber(2);
			final Geolocation geolocation = new Geolocation(position,watchID);
			call.success(geolocation);
		}
	}


	private void geolocation_watch_error(final JsonArray arguments)
	{
		final String id = arguments.getString(0);
		final ServiceCall<Geolocation> call = this.watchcalls.get(id);
		if(call != null)
		{
			call.error(new MobileServiceError(this,arguments.get(1).asString()));
		}
	}


	/**
	 * Clears the specified heading watch.
	 *
	 * @param watchID
	 */
	public void clearWatchPosition(final double watchID)
	{
		final StringBuilder js = new StringBuilder();
		js.append("geolocation_clear_watch(");
		js.append(watchID).append(");");

		Page.getCurrent().getJavaScript().execute(js.toString());
	}


	private void geolocation_get_future_success(final JsonArray arguments)
	{
		final Gson gson = new Gson();
		final JsonObject jsonObject = arguments.getObject(1);
		final Position position = gson.fromJson(jsonObject.toJson(),Position.class);

		this.waitMap.put(this.toString(),position);
		this.notify();
	}


	private void geolocation_get_future_error(final JsonArray arguments)
	{
		// XXX ???
		System.out.println(arguments.getString(1));
	}
}
