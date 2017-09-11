/*
 * Author: sveera
 */
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { DashboardComponent } from './components/dashboard.component';
import { PluginScriptComponent } from './components/pluginscript.component';

@NgModule({
  declarations: [
    PluginScriptComponent,
    DashboardComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule
  ],
  providers: [],
  bootstrap: [    
    DashboardComponent
  ]
})
export class DashboardModule { }
