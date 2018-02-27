/*
 * Author: sveera
 */
import { WebsocketServerSocketFactoryService } from '../../services/websocket.service';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs/Subscription';
import { OnInit, OnDestroy } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { BaseHrefProviderService } from '../../services/base.href.service';

export abstract class AbstractLiveLogger implements OnInit, OnDestroy {

    dcName: String;
    liveLogMessages: String[] = [];
    private socketSubscription: Subscription;

    constructor(private baseHrefProviderService: BaseHrefProviderService,
        private websocketServerSocketFactoryService: WebsocketServerSocketFactoryService,
        private location: Location, private activatedRoute: ActivatedRoute) { }

    ngOnInit(): void {
        this.dcName = this.baseHrefProviderService.getBaseHref();
        this.initializeLiveLoggerComponent();
    }

    private initializeLiveLoggerComponent() {
        const observableResult: Observable<any> = this.websocketServerSocketFactoryService.connect(
            this.getWebsocketConnectionUrl(this.activatedRoute));
        this.socketSubscription = observableResult.subscribe(
            (logMessageJson: any) => {
                console.log('Received live log message - ');
                console.log(logMessageJson);
                this.liveLogMessages.push(JSON.parse(JSON.stringify(logMessageJson)).logMessage);
            },
            error => console.error(error)
        );
    }

    protected abstract getWebsocketConnectionUrl(activatedRoute: ActivatedRoute): String;

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
