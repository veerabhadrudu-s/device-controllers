/**
 * 
 */
package com.hpe.iot.dc.tcp.southbound.model;

/**
 * @author sveera
 *
 */
public interface TCPOptions {

	ProcessingTaskType getProcessingTaskType();

	int getBufferCapacity();
	
	int getSocketBacklogCount();

}
