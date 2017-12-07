/**
 * 
 */
package com.hpe.iot.dc.mmi.vt15.tcp.client.payload.converters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.tcp.client.model.ClientDeviceData;
import com.hpe.iot.dc.tcp.client.payload.converter.ClientMessageGenerator;

/**
 * @author sveera
 *
 */
public class MMIVT15ClientToServerMessageGenerator implements ClientMessageGenerator {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String CONNECTION_PKT_TYP = "connection_packet";
	private static final String TRACKING_PKT_TYP = "tracking_packet";
	private static final String SERIAL_PKT_TYP = "serial_packet";
	private static final String TRACKING_HISTORY_BULK_PKT_TYP = "bulk_tracking_packet";

	public static final String CONNECTION_PKT_DATA = "$$CLIENT_1ZF,%d,15,1_35TS2B0164M,45.118.182.112,17499,internet,"
			+ "T1:10 S,T2:1 M,Ad1:9164061023,Ad2:9164061023,TOF:0 S,,OSC:75 KM,OST:0 S,GPS:NO,Ignition:ON,*33";
	public static final String SERIAL_DATA_PKT_DATA = "$$CLIENT_1NS,%d,20,28.23434,77.232340,171118094016,A,4,04617037,*18\r\n";
	public static final String TRACKING_LIVE_PKT_DATA = "$$CLIENT_1ZF,%d,1,12.962985,77.576484,140127165433,"
			+ "A,22,10,2200,140,6,0.8,0,0,6000,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,12530,4200,*26\r\n";
	public static final String TRACKING_HISTORY_PKT_DATA = "$$CLIENT_1ZF,%d,101,12.962985,77.576484,140127165433,"
			+ "A,22,10,2200,140,6,0.8,0,0,6000,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,12530,4200,*26\r\n";
	public static final String TRACKING_HISTORY_PKT_DATA1 = "$$CLIENT_1ZF,%d,101,12.976572,77.549683,170302192827,"
			+ "A,13,0,191781,327,8,0.980000,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,105,319,*36\n\r";
	public static final String TRACKING_HISTORY_PKT_DATA2 = "$$CLIENT_1ZF,%d,101,12.976572,77.549683,170302192927,"
			+ "A,13,0,191781,327,7,1.190000,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,105,319,*30\n\r";
	public static final String TRACKING_HISTORY_PKT_DATA3 = "$$CLIENT_1ZF,%d,101,12.976572,77.549683,170302193027,"
			+ "A,10,0,191781,327,7,1.190000,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,105,319,*3B\n\r";
	public static final String TRACKING_HISTORY_PKT_DATA4 = "$$CLIENT_1ZF,%d,101,12.976572,77.549683,170302193126,"
			+ "A,10,0,191781,327,7,1.160000,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,105,315,*38\n\r";
	public static final String TRACKING_HISTORY_PKT_DATA5 = "$$CLIENT_1ZF,%d,101,12.976503,77.549469,170302193226,"
			+ "A,10,0,191781,324,7,1.150000,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,105,310,*3E\n\r";
	public static final String TRACKING_HISTORY_PKT_DATA6 = "$$CLIENT_1ZF,%d,101,12.976502,77.549606,170302193325,"
			+ "A,10,0,191781,180,8,0.980000,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,0,105,310,*30\n\r";
	public static final String TRACKING_HISTORY_BULK_PKT_DATA = TRACKING_HISTORY_PKT_DATA1 + TRACKING_HISTORY_PKT_DATA2
			+ TRACKING_HISTORY_PKT_DATA3 + TRACKING_HISTORY_PKT_DATA4 + TRACKING_HISTORY_PKT_DATA5
			+ TRACKING_HISTORY_PKT_DATA6;

	@Override
	public ClientDeviceData generateMessagePacket(long deviceId, String messageType) {
		return generateDeviceDataForMessageType(deviceId, messageType);
	}

	private ClientDeviceData generateDeviceDataForMessageType(long deviceId, String messageType) {

		ClientDeviceData clientDeviceData = null;
		switch (messageType) {
		case CONNECTION_PKT_TYP:
			clientDeviceData = new ClientDeviceData(deviceId, String.format(CONNECTION_PKT_DATA,deviceId).getBytes(), messageType);
			break;
		case TRACKING_PKT_TYP:
			clientDeviceData = new ClientDeviceData(deviceId, String.format(TRACKING_LIVE_PKT_DATA,deviceId).getBytes(), messageType);
			break;
		case SERIAL_PKT_TYP:
			clientDeviceData = new ClientDeviceData(deviceId, String.format(SERIAL_DATA_PKT_DATA,deviceId).getBytes(), messageType);
			break;
		case TRACKING_HISTORY_BULK_PKT_TYP:
			clientDeviceData = new ClientDeviceData(deviceId, String
					.format(TRACKING_HISTORY_BULK_PKT_DATA, deviceId, deviceId, deviceId, deviceId, deviceId, deviceId)
					.getBytes(), messageType);
			break;
		}
		logger.trace("Returning " + clientDeviceData + " for messageType " + messageType);
		return clientDeviceData;
	}
}
