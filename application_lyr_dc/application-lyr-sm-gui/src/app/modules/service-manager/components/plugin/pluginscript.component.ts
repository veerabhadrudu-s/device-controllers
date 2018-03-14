/*
 * Author: sveera
 */
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Input } from '@angular/core';

import { PluginScript } from './pluginscript';

@Component({
    selector: 'app-pluginscript',
    templateUrl: './pluginscript.html'
})
export class PluginScriptComponent implements OnInit, OnDestroy {

    @Input()
    public pluginScript: PluginScript;

    ngOnInit() {
        console.log(this.constructor.name + ' Instance initialized');
    }

    ngOnDestroy(): void {
        console.log(this.constructor.name + ' Instance destroyed ');
    }

}
