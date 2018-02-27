import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadPluginComponent } from './upload-plugin.component';

describe('UploadPluginComponent', () => {
  let component: UploadPluginComponent;
  let fixture: ComponentFixture<UploadPluginComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UploadPluginComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UploadPluginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
