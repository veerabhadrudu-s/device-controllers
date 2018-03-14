/*
 * Author: sveera
 */
import { DashboardComponent } from './dashboard.component';
import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AbstractTemplateComponent } from '../abstract-template/abstract-template.component';
import { BannerComponent } from '../banner/banner.component';
import { FooterComponent } from '../footer/footer.component';
import { BaseHrefProviderService } from '../../services/base.href.service';
import { PluginScript } from '../plugin/pluginscript';
import { PluginScriptService } from '../../services/pluginScript.service';
import { MockPluginScriptData } from '../../services/mock.pluginscript.data';
import { UploadPluginComponent } from '../upload-plugin/upload-plugin.component';
import { PluginScriptComponent } from '../plugin/pluginscript.component';


class MockPluginScriptService {
    getInstalledPlugins(): Promise<PluginScript[]> {
        return Promise.resolve(MockPluginScriptData.MOCKED_PLUGIN_SCRIPT_DATA);
    }
}


fdescribe('Test cases related to ' + DashboardComponent.name, () => {

    let dashboardComponentFixture: ComponentFixture<DashboardComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [RouterTestingModule],
            declarations: [AbstractTemplateComponent, BannerComponent,
                UploadPluginComponent, PluginScriptComponent, FooterComponent, DashboardComponent],
            providers: [BaseHrefProviderService,
                { provide: PluginScriptService, useClass: MockPluginScriptService }
            ]
        }).compileComponents().then(() => {
            dashboardComponentFixture = TestBed.createComponent(DashboardComponent);
        });
    }));

    fit('Is ' + DashboardComponent.name + ' not null', async(() => {
        const dashboardComponent: DashboardComponent = dashboardComponentFixture.componentInstance;
        dashboardComponent.ngOnInit();
        dashboardComponentFixture.whenStable().then(() => {
            dashboardComponentFixture.detectChanges();
            expect(dashboardComponent).not.toBeNull();
        });
    }));

});
