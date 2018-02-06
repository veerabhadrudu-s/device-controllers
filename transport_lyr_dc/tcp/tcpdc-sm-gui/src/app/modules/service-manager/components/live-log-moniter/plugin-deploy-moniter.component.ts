/*
 * Author: sveera
 */
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { Location } from '@angular/common';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { WebsocketServerSocketFactoryService } from '../../services/websocket.service';

@Component({
  selector: 'app-plugin-deploy-moniter',
  templateUrl: './plugin-deploy-moniter.component.html'
})
export class PluginDeployMoniterComponent implements OnInit, OnDestroy {

  scriptFileName: String;
  liveLogMessages: String[] = [];
  private liveLoggerUrlPath: String = '/pluginScriptDeploymentStatus';
  private socketSubscription: Subscription;


  constructor(private websocketServerSocketFactoryService: WebsocketServerSocketFactoryService,
    private location: Location, private activatedRoute: ActivatedRoute) { }

  ngOnInit() {
    this.initializeLiveLoggerComponent();
  }

  private initializeLiveLoggerComponent() {
    const paramMap: ParamMap = this.activatedRoute.snapshot.paramMap;
    this.scriptFileName = paramMap.get('scriptFileName');
    const observableResult: Observable<any> = this.websocketServerSocketFactoryService.connect(
      this.liveLoggerUrlPath + '/' + this.scriptFileName);
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
