/*
 * Author: sveera
 */
import { TestBed, ComponentFixture } from '@angular/core/testing';
import { PluginScriptComponent } from './pluginscript.component';
import { RouterTestingModule } from '@angular/router/testing';
import { PluginScript } from './pluginscript';

fdescribe('Plugin script component', () => {

    let pluginScriptComponentFixture: ComponentFixture<PluginScriptComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [RouterTestingModule],
            declarations: [PluginScriptComponent]
        });
        pluginScriptComponentFixture = TestBed.createComponent(PluginScriptComponent);
    });

    fit('Is Plugin Script not null', () => {
        const pluginScriptComponent: PluginScriptComponent = pluginScriptComponentFixture.componentInstance;
        expect(pluginScriptComponent).not.toBeNull();
    });

    fit('Display Plugin Script Info', () => {
        pluginScriptComponentFixture.whenStable().then(() => {
            pluginScriptComponentFixture.componentInstance.pluginScript
                = new PluginScript('Sample', 'Model', '1.0', 'Sample.groovy');
            pluginScriptComponentFixture.detectChanges();
            return pluginScriptComponentFixture.whenStable();
        }).then(() => {
            const nativeElement: Element = pluginScriptComponentFixture.debugElement.nativeElement;
            console.log('Native element ' + nativeElement.constructor.name);
            expect(nativeElement.querySelector('div h4').innerHTML).toEqual('Sample Model 1.0');
            expect(nativeElement.querySelector('div p[class=\'tag\']').innerHTML)
                .toEqual('Script File Name : Sample.groovy');
        });
    });
});
