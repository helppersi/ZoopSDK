module.exports = {
	exibe: (mensagem) => {
		cordova.exec(sucesso, erro, "ZoopPlugin", "startDiscovery", []);
	}
}
