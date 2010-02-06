/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

/**
 * This class allows the storage of data so as to share value that cannot walk through request chain.<br/><br/> This class is shared by all User and so take care of Data Security and Concurrency.<br/> So as to perform the first point, the stored data are decorated with User name and a time stamp that indicate the time where the value has been stored. Moreover, the methode <b>retreiveObject(String key, String cmsUserUUID)</b> will remove the value from the map so as to diseable multi-read of the same value.<br/> The second point is performed by using some Concurrent Objects and by monitoring the main method of the class.<br/><br/> This class follows the design pattern Singleton, so the only created instance must be acceded in a static wy by <b>WaitingDataArea.getInstance()</b>
 * @author          Sébastien Bianco
 */
public class WaitingDataArea {
	
	/** The log object for this class. */
	private static final Logger LOGGER = Logger.getLogger(WaitingDataArea.class);

	/**
	 * The single instance of the class
	 * @uml.property  name="instance"
	 * @uml.associationEnd  
	 */
	private static WaitingDataArea instance = null;

	/**
	 * private default constructor to diseable it from other class
	 */
	private WaitingDataArea() {
	}

	/**
	 * Get the single instance of WaitingDataArea class. If this instance doesn't exist, it will be created.
	 * @return        the single instance of the class
	 * @uml.property  name="instance"
	 */
	public static WaitingDataArea getInstance() {
	
		if (instance == null) {
			instance = new WaitingDataArea();
		}
	
		return instance;
	}

	/**
	 * This class implement the container that will store the real element into the Map, decorated with the user name and a time stamp
	 * @author        Sébastien Bianco
	 */
	private class WaitingDataAreaMapElement {

		/**
		 * The timestamp corresponding to the moment of the storage of the data
		 * @uml.property  name="_timestamp"
		 */
		private long _timestamp;

		/**
		 * the user id that has stored this element
		 * @uml.property  name="_cmsUserUUID"
		 */
		private String _cmsUserUUID;

		/**
		 * The object to store
		 * @uml.property  name="_object"
		 */
		private Object _object;

		/**
		 * @param userUUID
		 * @param _object
		 */
		public WaitingDataAreaMapElement(String userUUID, Object _object) {
			super();
			_cmsUserUUID = userUUID;
			this._object = _object;
			// get the current system time
			this._timestamp = System.currentTimeMillis();
		}

		/**
		 * @return        the _cmsUserUUID
		 * @uml.property  name="_cmsUserUUID"
		 */
		public String get_cmsUserUUID() {
			return _cmsUserUUID;
		}

		/**
		 * @param userUUID        the _cmsUserUUID to set
		 * @uml.property  name="_cmsUserUUID"
		 */
		public void set_cmsUserUUID(String userUUID) {
			_cmsUserUUID = userUUID;
		}

		/**
		 * @return        the _object
		 * @uml.property  name="_object"
		 */
		public Object get_object() {
			return _object;
		}

		/**
		 * @param _object        the _object to set
		 * @uml.property  name="_object"
		 */
		public void set_object(Object _object) {
			this._object = _object;
		}

		/**
		 * @return        the _timestamp
		 * @uml.property  name="_timestamp"
		 */
		public long get_timestamp() {
			return _timestamp;
		}

		/**
		 * @param _timestamp        the _timestamp to set
		 * @uml.property  name="_timestamp"
		 */
		public void set_timestamp(long _timestamp) {
			this._timestamp = _timestamp;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result
					+ ((_cmsUserUUID == null) ? 0 : _cmsUserUUID.hashCode());
			result = PRIME * result
					+ ((_object == null) ? 0 : _object.hashCode());
			result = PRIME * result + (int) (_timestamp ^ (_timestamp >>> 32));
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final WaitingDataAreaMapElement other = (WaitingDataAreaMapElement) obj;
			if (_cmsUserUUID == null) {
				if (other._cmsUserUUID != null)
					return false;
			} else if (!_cmsUserUUID.equals(other._cmsUserUUID))
				return false;
			if (_object == null) {
				if (other._object != null)
					return false;
			} else if (!_object.equals(other._object))
				return false;
			if (_timestamp != other._timestamp)
				return false;
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "[U:" + _cmsUserUUID + " - T:" + _timestamp + " - O:"
					+ _object.toString() + "]";
		}
	}

	/**
	 * The map that will store the data
	 */
	private Map<String, WaitingDataAreaMapElement> _mapOfStoredObject = new ConcurrentHashMap<String, WaitingDataAreaMapElement>();

	/**
	 * Put a new value into the map of stored object. The key is concatened with
	 * the user uuid.<br/> If an object with the same exist, then the existing
	 * object is overwritten<br/><br/>
	 * If the value is set to null, delete the stored object.
	 * 
	 * @param key
	 *            the associated key of the value
	 * @param objectToStore
	 *            the object to store into the map
	 * @param cmsUserUUID
	 *            the user UUID that store the current object
	 */
	public synchronized void storeObject(String key, Object objectToStore,
			String cmsUserUUID) {
		LOGGER.debug("WF | store Object key="+key+" - Object="+objectToStore + " - user="+cmsUserUUID);
		String realKey = this.concatKeyAndUser(key, cmsUserUUID);
	
		//check if null element
		if(objectToStore == null){
			this._mapOfStoredObject.remove(realKey);
			return;
		}
		
		//remove object if exist to be sure to overwrite it 
		if(this._mapOfStoredObject.containsKey(realKey)){
			this._mapOfStoredObject.remove(realKey);
		}
		//store object
		this._mapOfStoredObject.put(realKey,
				new WaitingDataAreaMapElement(cmsUserUUID, objectToStore));
	}

	/**
	 * Concat the key and the CmsUserUUID so as to create the real key where
	 * will be stored data
	 * 
	 * @param key
	 *            the key of the object to store/get
	 * @param cmsUserUUID
	 *            the uuid of the user that want to access data
	 * @return a concatened String of the two previous
	 */
	private String concatKeyAndUser(String key, String cmsUserUUID) {
		return cmsUserUUID + "-" + key;
	}

	/**
	 * Retreive an object that has been stored previously. This method delete
	 * the stored object from the map
	 * 
	 * @param key
	 *            the key of the data that has been stored
	 * @param cmsUserUUID
	 *            the uuid of the user that has stored the data
	 * @return The previously stored object, <b>null</b> if this object has not
	 *         been found.
	 */
	public synchronized Object retreiveObject(String key, String cmsUserUUID) {
		String realKey = this.concatKeyAndUser(key, cmsUserUUID);
		WaitingDataAreaMapElement containerOfTheStoredObject = null;
		synchronized (this) {
			containerOfTheStoredObject = this._mapOfStoredObject.get(realKey);
		}

		// check that an object has been found
		if (containerOfTheStoredObject == null) {
			return null;
		} else {
			// delete the element from the map
			synchronized (this) {
				this._mapOfStoredObject.remove(realKey);
			}
			// return the stored object
			LOGGER.debug("WF | get Object key="+key+" - Object="+containerOfTheStoredObject.get_object());
			return containerOfTheStoredObject.get_object();
		}
	}
}
