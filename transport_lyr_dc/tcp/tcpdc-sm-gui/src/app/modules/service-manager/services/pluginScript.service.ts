/*
 * Author: sveera
 */
import { Injectable } from '@angular/core';
import { Headers, Http, Response } from '@angular/http';
import 'rxjs/add/operator/toPromise';
import { PluginScript } from '../components/plugin/pluginscript';
import { Location } from '@angular/common';


@Injectable()
export class PluginScriptService {

    private headers = new Headers({ 'Content-Type': 'application/json' });
    private readplugins = 'http://' + window.location.hostname + ':8080' + '/tcpdc/getPluginScripts';

    constructor(private http: Http, private location: Location) { }

    getScriptInstalledScripts(): Promise<PluginScript[]> {
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

    private handleError(error: any): Promise<any> {
        console.error('An error occurred', error); // for demo purposes only
        return Promise.reject(error.message || error);
    }
}
