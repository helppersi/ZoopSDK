module.exports = {
	startDiscovery: () => {
		cordova.exec(null, null, "ZoopSDK", "startDiscovery", []);
	},
	enableDeviceBluetoothAdapter: () => {
		cordova.exec(null, null, "ZoopSDK", "enableDeviceBluetoothAdapter", []);
	},
	requestZoopDeviceSelection: (device) => {
		cordova.exec(null, null, "ZoopSDK", "requestZoopDeviceSelection", [device]);
	},
	finishDiscovery: () => {
		cordova.exec(null, null, "ZoopSDK", "finishDiscovery", []);
	},


	showDeviceListForUserSelection: () => {
		cordova.exec(null, null, "ZoopSDK", "showDeviceListForUserSelection", []);
	},
	updateDeviceListForUserSelecion: () => {
		cordova.exec(null, null, "ZoopSDK", "updateDeviceListForUserSelecion", []);
	},
	bluetoothIsNotEnabledNotification: () => {
		cordova.exec(null, null, "ZoopSDK", "bluetoothIsNotEnabledNotification", []);
	},
	deviceSelectedResult: () => {
		cordova.exec(null, null, "ZoopSDK", "deviceSelectedResult", []);
	}
}
