/*
 * Author: sveera
 */
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { PluginScript } from '../plugin/pluginscript';
import { PluginScriptService } from '../../services/pluginScript.service';
import { WebsocketServerSocketFactoryService } from '../../services/websocket.service';

@Component({
    selector: 'dashboard',
    templateUrl: './dashboard.html'
})
export class DashboardComponent implements OnInit, OnDestroy {
    public pluginScriptsGroups: PluginScript[][];
    private pluginScripts: PluginScript[] = [];
    private monitorLiveConnectedDevicesSocketUrl: String = '/monitorLiveConnectedDevices';
    private socketSubscription: Subscription;

    constructor(private pluginScriptService: PluginScriptService,
        private websocketServerSocketFactoryService: WebsocketServerSocketFactoryService) {
        this.pluginScriptService = pluginScriptService;
    }

    ngOnInit(): void {
        this.pluginScriptService.getScriptInstalledScripts().then((pluginScripts) => {
            this.pluginScripts = pluginScripts;
            // console.log('Available PluginScripts are ');
            // console.log(this.pluginScripts);
            // console.log('Available Plugin Script length is ' + this.pluginScripts.length);
            if (this.pluginScripts != null && this.pluginScripts.length > 0) {
                const rowCount: number = Math.floor(this.pluginScripts.length / 4) + ((this.pluginScripts.length) % 4 === 0 ? 0 : 1);
                // console.log('rowCount ' + rowCount);
                this.pluginScriptsGroups = new Array<PluginScript[]>(rowCount);
                for (let index = 0; index < this.pluginScriptsGroups.length; index++) {
                    this.pluginScriptsGroups[index] = new Array<PluginScript>(4);
                    // console.log(this.pluginScriptsGroups[index]);
                }
                for (let index = 0; index < this.pluginScripts.length; index++) {
                    // console.log('Plugin Script index ' + index);
                    // console.log('Rounded index value is ' + Math.floor(index / 4));
                    const pluginScriptsLocal = this.pluginScriptsGroups[Math.floor(index / 4)];
                    pluginScriptsLocal[index % 4] = this.pluginScripts[index];
                    // console.log('pluginScriptsLocal ' + pluginScriptsLocal);
                }
                const stream = this.websocketServerSocketFactoryService.connect(this.monitorLiveConnectedDevicesSocketUrl);
                stream.subscribe((message: any) => {
                    console.log('Received websocket message ', message);
                    const pluginScript: PluginScript = message as PluginScript;
                    for (let index = 0; index < this.pluginScripts.length; index++) {
                        const ps: PluginScript = this.pluginScripts[index];
                        if (ps != null && pluginScript.manufacturer === ps.manufacturer && pluginScript.modelId === ps.modelId) {
                            ps.connectedDevices = pluginScript.connectedDevices;
                            break;
                        }
                    }
                });
            }
        });
        console.log(this.constructor.name + ' Instance initialized');
    }

    ngOnDestroy(): void {
        this.socketSubscription.unsubscribe();
        console.log(this.constructor.name + ' Instance destroyed ');
    }

}
