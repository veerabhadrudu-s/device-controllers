import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadPluginComponent } from './upload-plugin.component';
import { PluginScriptService } from '../../services/pluginScript.service';
import { PluginScript } from '../plugin/pluginscript';
import { MockPluginScriptData } from '../../services/mock.pluginscript.data';
import { RouterTestingModule } from '@angular/router/testing';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/of';

class MockPluginSriptService {

  getScriptInstalledScripts(): Promise<PluginScript[]> {
    return Promise.resolve(MockPluginScriptData.MOCKED_PLUGIN_SCRIPT_DATA);
  }

  uploadPluginScript(fileToBeUploaded: File): Observable<Response> {
    return Observable.of(new Response({ 'status': 'Plugin Script uploaded sucessfully.' }));
  }

}

fdescribe('UploadPluginComponent', () => {
  let component: UploadPluginComponent;
  let fixture: ComponentFixture<UploadPluginComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [UploadPluginComponent],
      providers: [{ provide: PluginScriptService, useClass: MockPluginSriptService }]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UploadPluginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  fit('should create', () => {
    expect(component).toBeTruthy();
  });
});
