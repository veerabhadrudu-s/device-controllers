/*
 * Author: sveera
 */
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppComponent } from './app.component';
import { ServiceManagerModule } from './modules/service-manager/service-manager.module';
import { AppRouterModule } from './modules/app-router/app-router.module';

@NgModule({
  imports: [
    ServiceManagerModule, AppRouterModule
  ],
  declarations: [AppComponent],
  bootstrap: [AppComponent]
})
export class AppModule { }
