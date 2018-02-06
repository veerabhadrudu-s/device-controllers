/*
 * Author: sveera
 */
import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { LiveLogMoniterComponent } from '../service-manager/components/live-log-moniter/live-log-moniter.component';
import { DashboardComponent } from '../service-manager/components/dashboard/dashboard.component';
import { PluginDeployMoniterComponent } from '../service-manager/components/live-log-moniter/plugin-deploy-moniter.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'liveLogMoniter/:manufacturer/:modelId/:version', component: LiveLogMoniterComponent },
  { path: 'pluginScriptDeploymentStatus/:scriptFileName', component: PluginDeployMoniterComponent }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [RouterModule]
})
export class AppRouterModule { }
