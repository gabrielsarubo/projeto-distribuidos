/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projects.trabalhofinal.nodes.messages;

import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Message;

/**
 *
 * @author gabrielsarubo
 */
public class WsnMsg extends Message {
    // Identificador da mensagem
    public Integer sequenceID;
    // Tempo de vida do Pacote
    public Integer ttl;
    // No de destino
    public Node destino;
    // No de origem
    public Node origem;
    // No que re-encaminhou a mensagem
    //public Node forwardingHop;
    // Numero de saltos ate o destino
    public Integer saltosAteDestino;
    // Tipo do Pacote
    // 0: Estabelecimento de rotas; 1: Pacotes de dados
    public Integer tipoMsg = 0;
   
    public WsnMsg(Integer seqID, Node origem, Node destino, Integer tipo) {
        this.sequenceID = seqID;
        this.origem = origem;
        this.destino = destino;
        this.tipoMsg = tipo;
    }

    @Override
    public Message clone() {
        WsnMsg msg = new WsnMsg(this.sequenceID, this.origem, this.destino, this.tipoMsg);
        msg.ttl = this.ttl;
        msg.saltosAteDestino = saltosAteDestino;
        return msg;
    }
}
