package br.com.smartcitizen.smartmessage.model

import java.io.Serializable

data class Message(
    var mensagem_id: Long,
    var usuario_emissor_id: Long,
    var usuario_receptor_id: Long,
    var mensagem_conteudo: String,
    var mensagem_data: String
) : Serializable