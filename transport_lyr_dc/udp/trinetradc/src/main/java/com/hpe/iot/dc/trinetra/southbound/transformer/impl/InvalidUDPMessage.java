package com.hpe.iot.dc.trinetra.southbound.transformer.impl;

/**
 * @author sveera
 *
 */
public class InvalidUDPMessage extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidUDPMessage(String message) {
		super(message);
	}

}
