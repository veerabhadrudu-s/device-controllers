/*
 * Author: sveera
 */
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BrowserModule } from '@angular/platform-browser';
import { HttpModule } from '@angular/http';
import { FormsModule } from '@angular/forms';
import { Location, LocationStrategy, PathLocationStrategy } from '@angular/common';
import { WebSocketService } from 'angular2-websocket-service';
import { InMemoryWebApiModule } from 'angular-in-memory-web-api';
import { BannerComponent } from './components/banner/banner.component';
import { FooterComponent } from './components/footer/footer.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { PluginScriptComponent } from './components/plugin/pluginscript.component';
import { PluginScriptService } from './services/pluginScript.service';
import { MockPluginScriptService } from './services/mock.pluginScript.service';
import { WebsocketServerSocketFactoryService } from './services/websocket.service';
import { UploadPluginComponent } from './components/upload-plugin/upload-plugin.component';
import { LiveLogMoniterComponent } from './components/live-log-moniter/live-log-moniter.component';
import { AppRouterModule } from '../app-router/app-router.module';
import { AbstractTemplateComponent } from './components/abstract-template/abstract-template.component';
import { PluginDeployMoniterComponent } from './components/live-log-moniter/plugin-deploy-moniter.component';


@NgModule({
  imports: [
    CommonModule,
    BrowserModule,
    FormsModule,
    HttpModule,
    AppRouterModule
    // Comment the below code after replacing with real webservice url .
    // , InMemoryWebApiModule.forRoot(MockPluginScriptService)
  ],
  declarations: [
    BannerComponent,
    DashboardComponent,
    PluginScriptComponent,
    FooterComponent,
    UploadPluginComponent,
    LiveLogMoniterComponent,
    AbstractTemplateComponent,
    PluginDeployMoniterComponent

  ],
  providers: [
    WebSocketService,
    Location, { provide: LocationStrategy, useClass: PathLocationStrategy },
    PluginScriptService,
    WebsocketServerSocketFactoryService
  ]
})
export class ServiceManagerModule { }
