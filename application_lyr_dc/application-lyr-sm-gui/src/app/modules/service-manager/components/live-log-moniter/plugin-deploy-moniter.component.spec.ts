/*
 * Author: sveera
 */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PluginDeployMoniterComponent } from './plugin-deploy-moniter.component';
import { BaseHrefProviderService } from '../../services/base.href.service';
import { WebsocketServerSocketFactoryService } from '../../services/websocket.service';
import { RouterTestingModule } from '@angular/router/testing';
import { AbstractTemplateComponent } from '../abstract-template/abstract-template.component';
import { BannerComponent } from '../banner/banner.component';
import { FooterComponent } from '../footer/footer.component';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/of';


class MockWebsocketServerSocketFactoryService {
  public connect(url: String): Observable<any> {
    return Observable.of({

    });
  }
}
fdescribe('Test cases related to ' + PluginDeployMoniterComponent.name, () => {

  let pluginDeployMoniterComponentFixture: ComponentFixture<PluginDeployMoniterComponent>;

  beforeEach(async(
    () => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule],
        declarations: [BannerComponent, FooterComponent, AbstractTemplateComponent, PluginDeployMoniterComponent],
        providers: [BaseHrefProviderService,
          { provide: WebsocketServerSocketFactoryService, useClass: MockWebsocketServerSocketFactoryService }
        ]
      }).compileComponents();

      pluginDeployMoniterComponentFixture = TestBed.createComponent(PluginDeployMoniterComponent);
    }));

  fit('Is ' + PluginDeployMoniterComponent.name + ' not null', async(() => {
    const pluginDeployMoniterComponent: PluginDeployMoniterComponent = pluginDeployMoniterComponentFixture.componentInstance;
    pluginDeployMoniterComponentFixture.whenStable().then(() => {
      pluginDeployMoniterComponentFixture.detectChanges();
      return pluginDeployMoniterComponentFixture.whenStable();
    }).then(() => {
      expect(pluginDeployMoniterComponent).not.toBeNull();
    });
  }));
});
