/*
 * Author: sveera
 */
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { Location } from '@angular/common';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { WebsocketServerSocketFactoryService } from '../../services/websocket.service';
import { AbstractLiveLogger } from './abstract-live-logger.component';
import { BaseHrefProviderService } from '../../services/base.href.service';

@Component({
  selector: 'app-plugin-deploy-moniter',
  templateUrl: './plugin-deploy-moniter.component.html'
})
export class PluginDeployMoniterComponent extends AbstractLiveLogger {

  scriptFileName: String;
  private liveLoggerUrlPath: String = '/pluginScriptDeploymentStatus';

  constructor(baseHrefProviderService: BaseHrefProviderService,
    websocketServerSocketFactoryService: WebsocketServerSocketFactoryService,
    location: Location, activatedRoute: ActivatedRoute) {
    super(baseHrefProviderService, websocketServerSocketFactoryService, location, activatedRoute);
  }


  protected getWebsocketConnectionUrl(activatedRoute: ActivatedRoute): String {
    const paramMap: ParamMap = activatedRoute.snapshot.paramMap;
    this.scriptFileName = paramMap.get('scriptFileName');
    return this.liveLoggerUrlPath + '/' + this.scriptFileName;
  }

}
