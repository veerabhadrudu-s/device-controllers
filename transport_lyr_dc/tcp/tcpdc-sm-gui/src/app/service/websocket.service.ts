/*
 * Author: sveera
 */

import { Injectable } from "@angular/core";
import { $WebSocket, WebSocketConfig } from "angular2-websocket/angular2-websocket";

@Injectable()
export class WebSocketService {

    private webSocketUrl:string="ws://10.3.239.75/TCPDC/monitorLiveConnectedDevices";
    //private webSocketUrl: string = "ws://185.170.48.164/TCPDC/monitorLiveConnectedDevices";

    connectForLiveConnectedDevicesInfo(messageHandler:any): void {
        let connectedWebSocket = new $WebSocket(this.webSocketUrl, null, { initialTimeout: 500, maxTimeout: 300000, reconnectIfNotNormalClose: true });
        connectedWebSocket.onOpen((event: Event)=>{
            console.log("Websocket Connection Successfully opened with server");
        });
        connectedWebSocket.onMessage(messageHandler);
    }



}
