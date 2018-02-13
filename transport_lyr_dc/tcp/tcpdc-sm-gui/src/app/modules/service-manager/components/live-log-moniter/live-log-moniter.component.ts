/*
 * Author: sveera
 */
import { Component } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { AbstractLiveLogger } from './abstract-live-logger.component';

@Component({
  selector: 'app-live-log-moniter',
  templateUrl: './live-log-moniter.component.html'
})
export class LiveLogMoniterComponent extends AbstractLiveLogger {
  manufacturer: String;
  modelId: String;
  version: String;
  private liveLoggerUrlPath: String = '/liveLoggerConnector';

  protected getWebsocketConnectionUrl(activatedRoute: ActivatedRoute): String {
    const paramMap: ParamMap = activatedRoute.snapshot.paramMap;
    this.manufacturer = paramMap.get('manufacturer');
    this.modelId = paramMap.get('modelId');
    this.version = paramMap.get('version');
    console.log('Selected Plugin script for live log monitering operation is ' + 'manufacturer : ' + this.manufacturer +
      ' modelId : ' + this.modelId + ' version : ' + this.version);
    return this.liveLoggerUrlPath + '/' + this.manufacturer + '/' + this.modelId + '/' + this.version;
  }

}
