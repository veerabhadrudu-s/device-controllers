
/*
 * Author: sveera
 */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LiveLogMoniterComponent } from './live-log-moniter.component';
import { RouterTestingModule } from '@angular/router/testing';
import { AbstractTemplateComponent } from '../abstract-template/abstract-template.component';
import { FooterComponent } from '../footer/footer.component';
import { BannerComponent } from '../banner/banner.component';
import { BaseHrefProviderService } from '../../services/base.href.service';
import { WebsocketServerSocketFactoryService } from '../../services/websocket.service';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/of';

class MockWebsocketServerSocketFactoryService {
    public connect(url: String): Observable<any> {
        return Observable.of({

        });
    }
}

fdescribe('Test cases related to ' + LiveLogMoniterComponent.name, () => {

    let liveLogMoniterComponentFixture: ComponentFixture<LiveLogMoniterComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [RouterTestingModule],
            declarations: [BannerComponent, FooterComponent, AbstractTemplateComponent, LiveLogMoniterComponent],
            providers: [BaseHrefProviderService,
                { provide: WebsocketServerSocketFactoryService, useClass: MockWebsocketServerSocketFactoryService }
            ]
        }).compileComponents();
        liveLogMoniterComponentFixture = TestBed.createComponent(LiveLogMoniterComponent);
    }));

    fit('Is ' + LiveLogMoniterComponent.name + ' not null', async(() => {
        const liveLogMoniterComponent: LiveLogMoniterComponent = liveLogMoniterComponentFixture.componentInstance;
        liveLogMoniterComponentFixture.whenStable().then(() => {
            liveLogMoniterComponentFixture.detectChanges();
            return liveLogMoniterComponentFixture.whenStable();
        }).then(() => {
            expect(liveLogMoniterComponent).not.toBeNull();
        });
    }));

});

