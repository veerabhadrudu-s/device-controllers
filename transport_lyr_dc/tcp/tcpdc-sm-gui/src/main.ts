import {MockPluginScriptService} from './app/modules/service-manager/services/mock.pluginScript.service';
import {InMemoryWebApiModule} from 'angular-in-memory-web-api';

/*
 * Author: sveera
 */
import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { environment } from './environments/environment';
import { AppModule } from './app/app.module';


if (environment.production) {
  enableProdMode();
}

platformBrowserDynamic().bootstrapModule(AppModule);

if (!environment.production) {
  InMemoryWebApiModule.forRoot(MockPluginScriptService)
}
