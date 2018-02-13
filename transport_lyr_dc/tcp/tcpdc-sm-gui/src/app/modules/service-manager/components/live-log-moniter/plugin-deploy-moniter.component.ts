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

@Component({
  selector: 'app-plugin-deploy-moniter',
  templateUrl: './plugin-deploy-moniter.component.html'
})
export class PluginDeployMoniterComponent extends AbstractLiveLogger {

  scriptFileName: String;
  private liveLoggerUrlPath: String = '/pluginScriptDeploymentStatus';


  protected getWebsocketConnectionUrl(activatedRoute: ActivatedRoute): String {
    const paramMap: ParamMap = activatedRoute.snapshot.paramMap;
    this.scriptFileName = paramMap.get('scriptFileName');
    return this.liveLoggerUrlPath + '/' + this.scriptFileName;
  }

}
