/**
 * 
 */
package com.hpe.iot.dc.trinetra.model;

import java.util.List;

import com.hpe.iot.dc.model.DeviceData;

/**
 * @author sveera
 *
 */
public class Notification implements DeviceData {

	public static final String NOTIF_MESS_TYP = "Notification Message";

	private final int noOfRecords;
	private final List<NotificationRecord> notifications;

	public Notification(int noOfRecords, List<NotificationRecord> notifications) {
		super();
		this.noOfRecords = noOfRecords;
		this.notifications = notifications;
	}

	public int getNoOfRecords() {
		return noOfRecords;
	}

	public List<NotificationRecord> getNotifications() {
		return notifications;
	}

	@Override
	public String getDeviceDataInformation() {
		return NOTIF_MESS_TYP;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + noOfRecords;
		result = prime * result + ((notifications == null) ? 0 : notifications.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Notification other = (Notification) obj;
		if (noOfRecords != other.noOfRecords)
			return false;
		if (notifications == null) {
			if (other.notifications != null)
				return false;
		} else if (!notifications.equals(other.notifications))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Notification [noOfRecords=" + noOfRecords + ", notifications=" + notifications + "]";
	}

}
