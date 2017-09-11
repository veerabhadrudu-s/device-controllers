/*
 * Author: sveera
 */
export class PluginScript {

    manufacturer: String;
    modelId: String;
    connectedDevices: number;
    private description: String;

    constructor(manufacturer: String, modelId: String, connectedDevices: number, description: String) {
        this.manufacturer = manufacturer;
        this.modelId = modelId;
        this.connectedDevices = connectedDevices;
        this.description = description;
    }    
}