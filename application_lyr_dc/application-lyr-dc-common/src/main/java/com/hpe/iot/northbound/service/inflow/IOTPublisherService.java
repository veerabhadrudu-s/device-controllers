package com.hpe.iot.northbound.service.inflow;

/**
 * @author sveera
 *
 * @param <Req>
 * @param <Res>
 */
public interface IOTPublisherService<Req, Res> {

	public Res receiveDataFromDevice(Req request, String containerName);

}
