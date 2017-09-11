/*
 * Author: sveera
 */
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { BannerComponent } from './common/components/banner.component';
import { DashboardComponent } from './dashboard/components/dashboard.component';
import { PluginScriptComponent } from './dashboard/components/pluginscript.component';
import { FooterComponent } from './common/components/footer.component';
import { PluginScriptService } from '../service/pluginScript.service';
import { WebSocketService } from '../service/websocket.service';
// Imports for loading & configuring the in-memory web api
import { InMemoryWebApiModule } from 'angular-in-memory-web-api';
import { InMemoryDataService }  from '../service/mock.pluginScript.service';
//import { CommonModule } from './common/common.module';
//import { DashboardModule } from './dashboard/dashboard.module';


@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        HttpModule
        //Remove the below code after replacing with real webservice url .
        //,InMemoryWebApiModule.forRoot(InMemoryDataService)
        //,CommonModule,
        //DashboardModule

    ],
    declarations: [
        BannerComponent,
        FooterComponent,
        PluginScriptComponent,
        DashboardComponent
    ],
    providers: [PluginScriptService,WebSocketService],
    bootstrap: [
        BannerComponent,
        FooterComponent,
        DashboardComponent
    ]

})
export class AppModule { }