/*
 * Author: sveera
 */
import { Component } from '@angular/core';
import { Input } from '@angular/core';

import { PluginScript } from './pluginscript';

@Component({
    selector: 'pluginscript',
    templateUrl: './pluginscript.html'
})
export class PluginScriptComponent {

    @Input()
    public pluginScript: PluginScript;

}
