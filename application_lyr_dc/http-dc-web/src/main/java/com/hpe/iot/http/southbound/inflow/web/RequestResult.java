/**
 * 
 */
package com.hpe.iot.http.southbound.inflow.web;

/**
 * @author sveera
 *
 */
public class RequestResult {
	private final String processingStatus;
	private final String otherInformation;
	private final String exceptionReason;

	public RequestResult(String processingStatus, String otherInformation, String exceptionReason) {
		super();
		this.processingStatus = processingStatus;
		this.otherInformation = otherInformation;
		this.exceptionReason = exceptionReason;
	}

	public RequestResult(String processingStatus) {
		super();
		this.processingStatus = processingStatus;
		this.otherInformation = "";
		this.exceptionReason = "";
	}

	public String getProcessingStatus() {
		return processingStatus;
	}

	public String getExceptionReason() {
		return exceptionReason;
	}

	public String getOtherInformation() {
		return otherInformation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((exceptionReason == null) ? 0 : exceptionReason.hashCode());
		result = prime * result + ((otherInformation == null) ? 0 : otherInformation.hashCode());
		result = prime * result + ((processingStatus == null) ? 0 : processingStatus.hashCode());
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
		RequestResult other = (RequestResult) obj;
		if (exceptionReason == null) {
			if (other.exceptionReason != null)
				return false;
		} else if (!exceptionReason.equals(other.exceptionReason))
			return false;
		if (otherInformation == null) {
			if (other.otherInformation != null)
				return false;
		} else if (!otherInformation.equals(other.otherInformation))
			return false;
		if (processingStatus == null) {
			if (other.processingStatus != null)
				return false;
		} else if (!processingStatus.equals(other.processingStatus))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Result [processingStatus=" + processingStatus + ", otherInformation=" + otherInformation
				+ ", exceptionReason=" + exceptionReason + "]";
	}

}
