/*
 * Author: sveera
 */
export class PluginScript {

    public manufacturer: String;
    public modelId: String;
    public version: String;
    public pluginScriptFileName: String;

    constructor(manufacturer: String, modelId: String, version: String, pluginScriptFileName: String) {
        this.manufacturer = manufacturer;
        this.modelId = modelId;
        this.version = version;
        this.pluginScriptFileName = pluginScriptFileName;
    }
}
