import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LiveLogMoniterComponent } from './live-log-moniter.component';

describe('LiveLogMoniterComponent', () => {
  let component: LiveLogMoniterComponent;
  let fixture: ComponentFixture<LiveLogMoniterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LiveLogMoniterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LiveLogMoniterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
