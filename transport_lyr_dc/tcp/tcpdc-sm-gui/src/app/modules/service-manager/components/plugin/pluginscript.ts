/*
 * Author: sveera
 */
export class PluginScript {

    public manufacturer: String;
    public modelId: String;
    public version: String;
    public connectedDevices: number;
    public description: String;

    constructor(manufacturer: String, modelId: String, version: String, connectedDevices: number, description: String) {
        this.manufacturer = manufacturer;
        this.modelId = modelId;
        this.version = version;
        this.connectedDevices = connectedDevices;
        this.description = description;
    }
}
