package com.hpe.iot.dc.northbound.service.outflow;

/**
 * @author sveera
 *
 * @param <Req>
 * @param <Res>
 */
public interface DevicePublisherService<Req, Res> {

	public Res sendRequestToDevice(Req request);

}
