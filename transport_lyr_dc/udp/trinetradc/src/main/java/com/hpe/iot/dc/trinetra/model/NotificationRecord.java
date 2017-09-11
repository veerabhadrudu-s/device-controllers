package com.hpe.iot.dc.trinetra.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sveera
 *
 */
public class NotificationRecord {

	private final String latitude, longitude, date, time, odometer;
	private final Map<String, Object> customInfo = new HashMap<>();

	public NotificationRecord(String latitude, String longitude, String date, String time, String odometer) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.date = date;
		this.time = time;
		this.odometer = odometer;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public String getDate() {
		return date;
	}

	public String getTime() {
		return time;
	}

	public String getOdometer() {
		return odometer;
	}

	public Map<String, Object> getCustomInfo() {
		return customInfo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((customInfo == null) ? 0 : customInfo.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
		result = prime * result + ((odometer == null) ? 0 : odometer.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
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
		NotificationRecord other = (NotificationRecord) obj;
		if (customInfo == null) {
			if (other.customInfo != null)
				return false;
		} else if (!customInfo.equals(other.customInfo))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (latitude == null) {
			if (other.latitude != null)
				return false;
		} else if (!latitude.equals(other.latitude))
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		if (odometer == null) {
			if (other.odometer != null)
				return false;
		} else if (!odometer.equals(other.odometer))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NotificationRecord [latitude=" + latitude + ", longitude=" + longitude + ", date=" + date + ", time="
				+ time + ", odometer=" + odometer + ", customInfo=" + customInfo + "]";
	}

}
