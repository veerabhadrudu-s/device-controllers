/*
 * Author: sveera
 */
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { PluginScript } from '../plugin/pluginscript';
import { PluginScriptService } from '../../services/pluginScript.service';
import { WebsocketServerSocketFactoryService } from '../../services/websocket.service';

@Component({
    selector: 'app-dashboard',
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
        this.initializeDashBoardComponent();
        console.log(this.constructor.name + ' Instance initialized');
    }

    private initializeDashBoardComponent() {
        this.pluginScriptService.getScriptInstalledScripts().then((pluginScripts) => {
            this.logAvailablePlugins(pluginScripts);
            if (this.pluginScripts != null && this.pluginScripts.length > 0) {
                this.convertOneDimentionalArrayToTwoDimensional();
                this.connectOverWebsocketForLiveDeviceCountUpdate();
            }
        });
    }

    private logAvailablePlugins(pluginScripts: PluginScript[]) {
        this.pluginScripts = pluginScripts;
        console.log('Available PluginScripts are ');
        console.log(this.pluginScripts);
        console.log('Available Plugin Script length is ' + this.pluginScripts.length);
    }

    private convertOneDimentionalArrayToTwoDimensional() {
        const columnCount = 4;
        const rowCount: number = Math.floor(this.pluginScripts.length / columnCount) +
            ((this.pluginScripts.length) % columnCount === 0 ? 0 : 1);
        // console.log('rowCount ' + rowCount);
        this.pluginScriptsGroups = new Array<PluginScript[]>(rowCount);
        for (let index = 0; index < this.pluginScriptsGroups.length; index++) {
            this.pluginScriptsGroups[index] = new Array<PluginScript>(columnCount);
            // console.log(this.pluginScriptsGroups[index]);
        }
        for (let index = 0; index < this.pluginScripts.length; index++) {
            // console.log('Plugin Script index ' + index);
            // console.log('Rounded index value is ' + Math.floor(index / 4));
            const pluginScriptsLocal = this.pluginScriptsGroups[Math.floor(index / columnCount)];
            pluginScriptsLocal[index % columnCount] = this.pluginScripts[index];
            // console.log('pluginScriptsLocal ' + pluginScriptsLocal);
        }
    }

    private connectOverWebsocketForLiveDeviceCountUpdate() {
        const stream = this.websocketServerSocketFactoryService.connect(this.monitorLiveConnectedDevicesSocketUrl);
        this.socketSubscription = stream.subscribe((message: any) => {
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

    ngOnDestroy(): void {
        this.socketSubscription.unsubscribe();
        console.log(this.constructor.name + ' Instance destroyed ');
    }

}
