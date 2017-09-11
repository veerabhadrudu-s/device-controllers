package com.hpe.iot.dc.southbound.crypto;

import java.util.Base64;

/**
 * @author sveera
 *
 */
public class DataDecoder {

	public byte[] decode(String encodedString) {
		return Base64.getDecoder().decode(encodedString);
	}

}
