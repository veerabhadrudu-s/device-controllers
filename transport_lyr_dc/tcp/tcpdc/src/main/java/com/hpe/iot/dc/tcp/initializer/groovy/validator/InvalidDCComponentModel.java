package com.hpe.iot.dc.tcp.initializer.groovy.validator;

import java.util.List;

/**
 * @author sveera
 *
 */
public class InvalidDCComponentModel extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidDCComponentModel(List<Class<?>> missingClassTypes) {
		super("Following Class Types are missing in the script file " + missingClassTypes.toString());
	}

}
