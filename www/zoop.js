module.exports = {
	startDiscovery: function(mensagem, sucesso, erro){
		cordova.exec(sucesso, erro, "ZoopSDK", "startDiscovery", []);
	}
}
