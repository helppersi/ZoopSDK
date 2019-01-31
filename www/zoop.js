module.exports = {
	startDiscovery: () => {
		cordova.exec(sucesso, erro, "ZoopPlugin", "startDiscovery", []);
	}
}
