import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PluginDeployMoniterComponent } from './plugin-deploy-moniter.component';

describe('PluginDeployMoniterComponent', () => {
  let component: PluginDeployMoniterComponent;
  let fixture: ComponentFixture<PluginDeployMoniterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PluginDeployMoniterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PluginDeployMoniterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
