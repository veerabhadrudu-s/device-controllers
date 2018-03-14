/*
 * Author: sveera
 */
import { Injectable } from '@angular/core';
import { Headers, Http, Response, RequestOptions } from '@angular/http';
import 'rxjs/add/operator/toPromise';
import { PluginScript } from '../components/plugin/pluginscript';
import { Observable } from 'rxjs/Observable';
import { BaseHrefProviderService } from './base.href.service';


@Injectable()
export class PluginScriptService {

    private servicePortNumber: Number = 8080;
    private readplugins = 'http://' + window.location.hostname + ':' + this.servicePortNumber +
        '/' + this.baseHrefProviderService.getBaseHref() + 'dc' + '/getPluginScripts';
    private uploadPlugin = 'http://' + window.location.hostname + ':' + this.servicePortNumber +
        '/' + this.baseHrefProviderService.getBaseHref() + 'dc' + '/uploadPluginScript';

    constructor(private baseHrefProviderService: BaseHrefProviderService, private http: Http) { }

    getInstalledPlugins(): Promise<PluginScript[]> {
        const promiseResponse: Promise<Response> = this.http.get(this.readplugins).toPromise();
        return promiseResponse.
            then((response) => {
                console.log('Response Json from Server ');
                console.log(response.json());
                console.log('Response Json Data from Server ');
                console.log(response.json().data);
                return response.json().data as PluginScript[];
            })
            .catch(this.handleError);
    }

    uploadPluginScript(fileToBeUploaded: File): Observable<Response> {
        const formData: FormData = new FormData();
        formData.append('file', fileToBeUploaded, fileToBeUploaded.name);
        const headers = new Headers();
        headers.append('Accept', 'application/json');
        const options = new RequestOptions({ headers: headers });
        return this.http.post(this.uploadPlugin, formData, options);
    }

    private handleError(error: any): Promise<any> {
        console.error('An error occurred', error); // for demo purposes only
        return Promise.reject(error.message || error);
    }
}
