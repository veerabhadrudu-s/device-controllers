/*
 * Author: sveera
 */

import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { Location } from '@angular/common';
import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';
import { PluginScript } from '../plugin/pluginscript';
import { WebsocketServerSocketFactoryService } from '../../services/websocket.service';

@Component({
  selector: 'app-live-log-moniter',
  templateUrl: './live-log-moniter.component.html'
})
export class LiveLogMoniterComponent implements OnInit, OnDestroy {
  manufacturer: String;
  modelId: String;
  version: String;
  liveLogMessages: String[] = [];
  private liveLoggerUrlPath: String = '/liveLoggerConnector';
  private socketSubscription: Subscription;


  constructor(private websocketServerSocketFactoryService: WebsocketServerSocketFactoryService,
    private location: Location, private activatedRoute: ActivatedRoute) { }

  ngOnInit() {
    this.initializeLiveLoggerComponent();
  }

  private initializeLiveLoggerComponent() {
    const paramMap: ParamMap = this.activatedRoute.snapshot.paramMap;
    this.manufacturer = paramMap.get('manufacturer');
    this.modelId = paramMap.get('modelId');
    this.version = paramMap.get('version');
    console.log('Selected Plugin script for live log monitering operation is ' + 'manufacturer : ' + this.manufacturer +
      ' modelId : ' + this.modelId + ' version : ' + this.version);
    const observableResult: Observable<any> = this.websocketServerSocketFactoryService.connect(
      this.liveLoggerUrlPath + '/' + this.manufacturer + '/' + this.modelId + '/' + this.version);
    this.socketSubscription = observableResult.subscribe(
      (logMessageJson: any) => {
        console.log('Received live log message - ');
        console.log(logMessageJson);
        this.liveLogMessages.push(JSON.parse(JSON.stringify(logMessageJson)).logMessage);
      },
      error => console.error(error)
    );
  }

  goBackToDashboard(): void {
    this.socketSubscription.unsubscribe();
    this.liveLogMessages = [];
    this.location.back();
  }

  clearLogs() {
    this.liveLogMessages = [];
  }

  ngOnDestroy(): void {
    console.log(this.constructor.name + ' Instance destroyed ');
  }

}
