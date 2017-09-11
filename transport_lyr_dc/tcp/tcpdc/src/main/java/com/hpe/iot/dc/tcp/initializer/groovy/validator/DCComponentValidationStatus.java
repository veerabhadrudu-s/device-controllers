/**
 * 
 */
package com.hpe.iot.dc.tcp.initializer.groovy.validator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sveera
 *
 */
public class DCComponentValidationStatus {

	private final boolean isInValidDCComponentModel;
	private final List<Class<?>> missingClassTypes;

	public DCComponentValidationStatus(boolean isInValidDCComponentModel, List<Class<?>> missingClassTypes) {
		super();
		this.isInValidDCComponentModel = isInValidDCComponentModel;
		this.missingClassTypes = missingClassTypes;
	}

	public DCComponentValidationStatus(boolean isInValidDCComponentModel) {
		super();
		this.isInValidDCComponentModel = isInValidDCComponentModel;
		this.missingClassTypes = new ArrayList<>();
	}

	public boolean isInValidDCComponentModel() {
		return isInValidDCComponentModel;
	}

	public List<Class<?>> getMissingClassTypes() {
		return missingClassTypes;
	}

	@Override
	public String toString() {
		return "DCComponentValidatorStatus [isInValidDCComponentModel=" + isInValidDCComponentModel
				+ ", missingClassTypes=" + missingClassTypes + "]";
	}
}
