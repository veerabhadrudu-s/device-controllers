/*
 * Author: sveera
 */
import { Component } from '@angular/core';
import { Input } from '@angular/core';

import {PluginScript} from './pluginscript';

@Component({
    selector: 'pluginscript',
    templateUrl: '../views/pluginscript.html'
})
export class PluginScriptComponent {  

    @Input()
    private pluginScript:PluginScript;       

}