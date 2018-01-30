
/*
 * Author: sveera
 */
import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { environment } from './environments/environment';
import { ServiceManagerModule } from './app/modules/service-manager/service-manager.module';

if (environment.production) {
  enableProdMode();
}

platformBrowserDynamic().bootstrapModule(ServiceManagerModule);
