/*
 * Author: sveera
 */
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { BannerComponent } from './components/banner.component';
import { FooterComponent } from './components/footer.component';

@NgModule({
  declarations: [
    BannerComponent,
    FooterComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule
  ],
  providers: []
  ,
  bootstrap: [
    BannerComponent,
    FooterComponent
  ]
})
export class CommonModule { }
