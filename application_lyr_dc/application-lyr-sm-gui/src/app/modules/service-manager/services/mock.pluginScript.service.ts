/*
 * Author: sveera
 */
import { InMemoryDbService } from 'angular-in-memory-web-api';
import { PluginScript } from '../components/plugin/pluginscript';
import { MockPluginScriptData } from './mock.pluginscript.data';

export class MockHTTPGetServices implements InMemoryDbService {
    createDb() {
        // Return variable name should match with rest endpoint last path value.
        // In below example last endpoint path value is getPluginScripts.
        // http://10.3.239.75:8080/tcpdc/getPluginScripts'
        const getPluginScripts = { 'data': MockPluginScriptData.MOCKED_PLUGIN_SCRIPT_DATA };

        const uploadPluginScript = { 'status': 'Plugin Script uploaded sucessfully.' };

        const json = { 'status': 'Plugin Script uploaded sucessfully.' };

        return { getPluginScripts, uploadPluginScript, json };
    }
}
