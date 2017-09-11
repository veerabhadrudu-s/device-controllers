/**
 * 
 */
package com.hpe.iot.dc.tcp.client.notify;

/**
 * @author sveera
 *
 */
public interface ClientHandShakeNotifier {

	void handshakeCompleted(long... deviceIds);

}
