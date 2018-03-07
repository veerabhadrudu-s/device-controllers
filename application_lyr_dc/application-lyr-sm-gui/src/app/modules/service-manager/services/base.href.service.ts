/*
 * Author: sveera
 */
import { Injectable, Inject, OnInit } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { Location, LocationStrategy, PathLocationStrategy } from '@angular/common';

@Injectable()
export class BaseHrefProviderService {

    private baseHref: String;

    constructor(private location: Location) {
        //  This service manager UI is common for all application layer protocol DC's.
        //  In order to differentiate and deploy individual service manager UI for each DC,
        //  We use angular cli command $ ng build--prod--base - href=/<dc name>/
        //  This base ref is accessible through this servcie method getBaseHref().
        //  BaseHrefProviderService service injected all over the DC to find out type of DC servcie manager.
        //  Example: $ ng build--prod--base - href=/kafka/, will gives dc name as kafka.
        //  Using this name we form remaning Rest Url's dynamically.
        // --base-href is not provided we assume default name as mqtt
        console.log('base href ' + this.location.prepareExternalUrl(''));
        this.baseHref = this.location.prepareExternalUrl('') === '/' || this.location.prepareExternalUrl('') === '' ? 'mqtt' :
            this.location.prepareExternalUrl('').split('/')[1];
    }

    getBaseHref(): String {
        return this.baseHref;
    }
}

