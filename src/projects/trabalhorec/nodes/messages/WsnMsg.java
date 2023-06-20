/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projects.trabalhorec.nodes.messages;

import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Message;

/**
 *
 * @author gabrielsarubo
 */
public class WsnMsg extends Message {
    // Identificador da mensagem
    public Integer sequenceID;
    // No de origem
    public Node origem;
    // Numero de saltos desde o No de Origem (Sink) -- contador global de saltos
    // Neste caso, 0 nao representa "zero saltos", mas sim um valor inicial, porem invalido
    // Portanto, nunca deve existir um No-Sensor A que esta a "0 saltos" de um No-Sink
    public int nSaltosDesdeOrigem = 0;
    // Tipo do Pacote: 0: Estabelecimento de rotas; 1: Pacotes de dados
    public Integer tipoMsg = 0;
   
    public WsnMsg(Integer seqID, Node origem, Integer tipo) {
        this.sequenceID = seqID;
        this.origem = origem;
        this.tipoMsg = tipo;
    }

    @Override
    public Message clone() {
        WsnMsg msg = new WsnMsg(this.sequenceID, this.origem, this.tipoMsg);
        msg.nSaltosDesdeOrigem = this.nSaltosDesdeOrigem;
        return msg;
    }
}
