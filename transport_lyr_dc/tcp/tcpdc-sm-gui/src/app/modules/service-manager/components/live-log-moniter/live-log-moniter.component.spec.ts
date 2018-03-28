/*
 * Author: sveera
 */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import { LiveLogMoniterComponent } from './live-log-moniter.component';
import { AbstractLiveLogger } from './abstract-live-logger.component';
import { WebsocketServerSocketFactoryService } from '../../services/websocket.service';
import { FooterComponent } from '../footer/footer.component';
import { BannerComponent } from '../banner/banner.component';
import { AbstractTemplateComponent } from '../abstract-template/abstract-template.component';

class MockWebsocketServerSocketFactoryService {
  connect(): Observable<any> {
    return Observable.of({

    });
  }
}

fdescribe('Test cases realted to ' + LiveLogMoniterComponent.name, () => {
  let component: LiveLogMoniterComponent;
  let fixture: ComponentFixture<LiveLogMoniterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [BannerComponent, FooterComponent, AbstractTemplateComponent, LiveLogMoniterComponent],
      providers: [
        { provide: WebsocketServerSocketFactoryService, useClass: MockWebsocketServerSocketFactoryService }
      ]
    })
      .compileComponents();
    fixture = TestBed.createComponent(LiveLogMoniterComponent);
  }));

  fit('Is ' + LiveLogMoniterComponent.name + ' not null', () => {
    component = fixture.componentInstance;
    fixture.whenStable().then(() => {
      fixture.detectChanges();
      expect(component).not.toBeNull();
      const componentRootElement: HTMLElement = fixture.debugElement.nativeElement;
      console.log('componentRootElement' + componentRootElement.constructor.name);      
      expect(componentRootElement.querySelector('table[class=\'liveLogMenuPanel\'] thead tr th').textContent.trim())
        .toBe('Selected TCP Plugin Service for log monitering :');
    });
  });
});
