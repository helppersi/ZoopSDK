module.exports = {
	startDiscovery: () => {
		cordova.exec(null, null, "ZoopPlugin", "startDiscovery", []);
	}
}
