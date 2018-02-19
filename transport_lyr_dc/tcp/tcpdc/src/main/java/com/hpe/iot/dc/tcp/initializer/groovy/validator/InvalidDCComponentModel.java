package com.hpe.iot.dc.tcp.initializer.groovy.validator;

import java.util.List;

/**
 * @author sveera
 *
 */
public class InvalidDCComponentModel extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidDCComponentModel(String scriptFileName, List<Class<?>> missingClassTypes) {
		super("Following Class Types " + missingClassTypes.toString() + " are missing in the script file "
				+ scriptFileName);
	}

	public InvalidDCComponentModel(String message) {
		super(message);
	}

}
