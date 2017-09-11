package com.hpe.iot.dc.trinetra.southbound.transformer.impl;

import java.util.Arrays;

/**
 * @author sveera
 *
 */
public class KnownMessageTypeFrame {

	private String messageType;
	private String[] dataFrameHex;

	public KnownMessageTypeFrame(String messageType, String[] dataFrameHex) {
		super();
		this.messageType = messageType;
		this.dataFrameHex = dataFrameHex;
	}

	public String getMessageType() {
		return messageType;
	}

	public String[] getDataFrameHex() {
		return dataFrameHex;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(dataFrameHex);
		result = prime * result + ((messageType == null) ? 0 : messageType.hashCode());
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
		KnownMessageTypeFrame other = (KnownMessageTypeFrame) obj;
		if (!Arrays.equals(dataFrameHex, other.dataFrameHex))
			return false;
		if (messageType == null) {
			if (other.messageType != null)
				return false;
		} else if (!messageType.equals(other.messageType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "KnownMessageTypeFrame [messageType=" + messageType + ", dataFrameHex=" + Arrays.toString(dataFrameHex)
				+ "]";
	}

}
