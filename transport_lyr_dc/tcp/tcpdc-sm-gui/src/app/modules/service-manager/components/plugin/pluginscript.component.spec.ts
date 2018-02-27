/*
 * Author: sveera
 */
import { PluginScriptComponent } from './pluginscript.component';
import {
    async,
    inject,
    TestBed,
    ComponentFixture
} from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { PluginScript } from './pluginscript';

fdescribe('Test Plugin Script Component Display', () => {

    let fixture: ComponentFixture<PluginScriptComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [RouterTestingModule],
            declarations: [
                PluginScriptComponent
            ]
        });
        fixture = TestBed.createComponent(PluginScriptComponent);
        fixture.detectChanges();
    });

    fit('Display Plugin Script Info', async(inject([], () => {
        const pluginScriptComponent: PluginScriptComponent = fixture.componentInstance;
        pluginScriptComponent.pluginScript = {
            manufacturer: 'Sample',
            modelId: 'Sample Model',
            version: '1.0',
            connectedDevices: 1000,
            description: ''
        };
        fixture.whenStable()
            .then(() => {
                fixture.detectChanges();
                return fixture.whenStable();
            })
            .then(() => {
                const compiled = fixture.debugElement.nativeElement;
                expect(compiled.querySelector('h4').innerText).toEqual('Sample Sample Model 1.0');
                expect(compiled.querySelector('p[class=\'tag\']').innerText).toEqual('Live Connected Devices : 1000');
                expect(compiled.querySelector('p[class=\'text_column\']').innerText).toEqual('');
            });
    })));
});
