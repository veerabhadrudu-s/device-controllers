/*
 * Author: sveera
 */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { UploadPluginComponent } from './upload-plugin.component';
import { BaseHrefProviderService } from '../../services/base.href.service';
import { RouterTestingModule } from '@angular/router/testing';
import { PluginScriptService } from '../../services/pluginScript.service';


class MockPluginScriptService { }

fdescribe('Test cases related to ' + UploadPluginComponent.name, () => {

  let uploadPluginComponentFixture: ComponentFixture<UploadPluginComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [UploadPluginComponent],
      providers: [BaseHrefProviderService, {
        provide: PluginScriptService, useclass: MockPluginScriptService
      }]
    });
    uploadPluginComponentFixture = TestBed.createComponent(UploadPluginComponent);
  });

  fit('Is ' + UploadPluginComponent.name + ' not null', () => {
    const uploadPluginComponent: UploadPluginComponent = uploadPluginComponentFixture.componentInstance;
    uploadPluginComponentFixture.whenStable().then(() => {
      uploadPluginComponentFixture.detectChanges();
      expect(uploadPluginComponent).not.toBeNull();
    });
  });
});
