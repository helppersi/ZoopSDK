module.exports = {
	exibe: function(mensagem, sucesso, erro){
		cordova.exec(sucesso, erro, "ZoopPlugin", "exibe", [mensagem]);
	}
}
