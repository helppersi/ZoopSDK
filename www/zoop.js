module.exports = {
	startDiscovery: () => {
		cordova.exec(null, null, "ZoopSDK", "startDiscovery", []);
	}
}
