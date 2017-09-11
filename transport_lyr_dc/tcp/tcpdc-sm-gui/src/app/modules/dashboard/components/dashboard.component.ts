/*
 * Author: sveera
 */
import { Component, OnInit } from '@angular/core';
import { PluginScript } from './pluginscript';
import { PluginScriptService } from '../../../service/pluginScript.service';
import { WebSocketService } from '../../../service/websocket.service';

@Component({
    selector: 'dashboard',
    templateUrl: '../views/dashboard.html'
    // providers: [PluginScriptService]
})
export class DashboardComponent implements OnInit {
    private pluginScripts: PluginScript[];
    private pluginScriptsGroups: PluginScript[][];

    constructor(private pluginScriptService: PluginScriptService, private webSocketService: WebSocketService) {
        this.pluginScriptService = pluginScriptService;
    }

    ngOnInit(): void {
        this.pluginScriptService.getScriptInstalledScripts().then((pluginScripts) => {
            this.pluginScripts = pluginScripts;
            console.log("Available PluginScripts are ");
            console.log(this.pluginScripts);
            // console.log("Available Plugin Script length is " + this.pluginScripts.length);
            if (this.pluginScripts != null && this.pluginScripts.length > 0) {
                let rowCount: number = Math.floor(this.pluginScripts.length / 4) + ((this.pluginScripts.length) % 4 == 0 ? 0 : 1);
                //console.log("rowCount " + rowCount);               
                this.pluginScriptsGroups = new Array<PluginScript[]>(rowCount);
                for (let index: number = 0; index < this.pluginScriptsGroups.length; index++) {
                    this.pluginScriptsGroups[index] = new Array<PluginScript>(4);
                    //console.log(this.pluginScriptsGroups[index]);
                }
                for (let index: number = 0; index < this.pluginScripts.length; index++) {
                    //   console.log("Plugin Script index " + index);
                    //   console.log("Rounded index value is " + Math.floor(index / 4));
                    var pluginScriptsLocal = this.pluginScriptsGroups[Math.floor(index / 4)];
                    pluginScriptsLocal[index % 4] = this.pluginScripts[index];
                    //   console.log("pluginScriptsLocal " + pluginScriptsLocal)
                }
                this.webSocketService.connectForLiveConnectedDevicesInfo((message: MessageEvent) => {
                    //console.log("onMessage ", JSON.parse(message.data));
                    let pluginScript: PluginScript = JSON.parse(message.data) as PluginScript;
                    for (let index = 0; index < this.pluginScripts.length; index++) {
                        let ps: PluginScript = this.pluginScripts[index];
                        if (ps != null && pluginScript.manufacturer == ps.manufacturer && pluginScript.modelId == ps.modelId) {
                            ps.connectedDevices = pluginScript.connectedDevices;
                            break;
                        }
                    }
                });
            }
        });
    }

}