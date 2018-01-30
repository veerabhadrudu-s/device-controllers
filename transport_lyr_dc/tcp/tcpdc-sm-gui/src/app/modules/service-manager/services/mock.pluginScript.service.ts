/*
 * Author: sveera
 */
import { InMemoryDbService } from 'angular-in-memory-web-api';
import { PluginScript } from '../components/plugin/pluginscript';

export class MockPluginScriptService implements InMemoryDbService {
    createDb() {
        const mockedScripts: PluginScript[] = [
            new PluginScript('MMI', 'SafeMate', '1.0', 10000, 'This a Personal Safety Tracker used for child and women safety.'),
            new PluginScript('Sample', 'SModel', '1.0', 10000, 'This a Sample Test Device.'),
            new PluginScript('Sample1', 'SModel1', '1.0', 0, ''),
            new PluginScript('Sample2', 'SModel2', '1.0', 0, ''),
            new PluginScript('Sample3', 'SModel3', '1.0', 0, ''),
            new PluginScript('Sample4', 'SModel4', '1.0', 0, ''),
            new PluginScript('Sample5', 'SModel5', '1.0', 0, ''),
            new PluginScript('Sample6', 'SModel6', '1.0', 0, ''),
            new PluginScript('Sample7', 'SModel7', '1.0', 0, ''),
            new PluginScript('Sample8', 'SModel8', '1.0', 0, ''),
            new PluginScript('Sample9', 'SModel9', '1.0', 0, ''),
            new PluginScript('Sample10', 'SModel10', '1.0', 0, ''),
            new PluginScript('Sample11', 'SModel11', '1.0', 0, '')
        ];
        // Return variable name should match with rest endpoint last path value.
        // In below example last endpoint path value is getPluginScripts.
        // http://10.3.239.75:8080/tcpdc/getPluginScripts'
        const getPluginScripts = { 'data': mockedScripts };
        return { getPluginScripts };
    }
}
