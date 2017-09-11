
/*
 * Author: sveera
 */
import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { environment } from './environments/environment';

//import { CommonModule } from './app/modules/common/common.module';
//import { DashboardModule } from './app/modules/dashboard/dashboard.module';
import { AppModule } from './app/modules/app.module';

if (environment.production) {
  enableProdMode();
}

platformBrowserDynamic().bootstrapModule(AppModule);
