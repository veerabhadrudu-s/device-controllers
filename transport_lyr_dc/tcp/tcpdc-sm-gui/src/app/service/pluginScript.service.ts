/*
 * Author: sveera
 */
import { Injectable } from "@angular/core";
import { Headers, Http, Response } from '@angular/http';
import 'rxjs/add/operator/toPromise';

import { PluginScript } from "../modules/dashboard/components/pluginscript";
import { ALL_PLUGIN_SCRIPTS } from "../modules/dashboard/components/mock.pluginscript-data";

@Injectable()
export class PluginScriptService {

    private headers = new Headers({ 'Content-Type': 'application/json' });
    //private readplugins = 'api/getPluginScripts';  // URL to web api
    private readplugins = 'http://10.3.239.75/TCPDC/getPluginScripts';
    //private readplugins = 'http://185.170.48.164/TCPDC/getPluginScripts';

    constructor(private http: Http) { }


    getScriptInstalledScripts(): Promise<PluginScript[]> {
        var promiseResponse: Promise<Response> = this.http.get(this.readplugins).toPromise();
        return promiseResponse.
            then((response) => {
               // console.log("Response Json from Server ");
               // console.log(response.json());
               // console.log("Response Json Data from Server ");
               // console.log(response.json().data);
                return response.json().data as PluginScript[]
            })
            .catch(this.handleError);
    }

    private handleError(error: any): Promise<any> {
        console.error('An error occurred', error); // for demo purposes only
        return Promise.reject(error.message || error);
    }
}