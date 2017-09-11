package com.hpe.iot.service.initializer.groovy;

/**
 * @author sveera
 *
 */
public class DependancyNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 34177651894233054L;

	public DependancyNotFoundException(String message) {
		super(message);
	}

}
