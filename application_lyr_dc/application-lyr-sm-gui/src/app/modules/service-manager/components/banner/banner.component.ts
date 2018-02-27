/*
 * Author: sveera
 */
import { Component, OnInit } from '@angular/core';
import { BaseHrefProviderService } from '../../services/base.href.service';

@Component({
    selector: 'app-banner',
    templateUrl: './banner.html'
})
export class BannerComponent implements OnInit {

    dcName: String;

    constructor(private baseHrefProviderService: BaseHrefProviderService) { }

    ngOnInit(): void {
        this.dcName = this.baseHrefProviderService.getBaseHref();
        console.log('DC name is ' + this.dcName);
    }

}
