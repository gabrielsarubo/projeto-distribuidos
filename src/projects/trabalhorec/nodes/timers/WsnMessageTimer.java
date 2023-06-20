/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projects.trabalhorec.nodes.timers;

import projects.trabalhorec.nodes.messages.WsnMsg;
import projects.trabalhorec.nodes.nodeImplementations.SinkNode;
import sinalgo.nodes.timers.Timer;

/**
 *
 * @author gabrielsarubo
 */
public class WsnMessageTimer extends Timer {

    private WsnMsg message = null;

    public WsnMessageTimer(WsnMsg message) {
        this.message = message;
    }

    @Override
    public void fire() {
    	// identificar qual No de destino esta chamando esta funcao fire()
    	if (this.message.origem instanceof SinkNode) {
    		System.out.println("O timer do SinkNode-"+message.origem.ID+" foi disparado.\nIniciando broadcast da mensagem deste Sink para os Nos vizinhos...");
    		
    		// Construir uma arvore de roteamento/estabelecer rotas a partir de um SinkNode especifico
    		// Fazer um broadcast da mensagem para todos os Nos vizinhos
    		// este metodo envia uma msg e recebe um pacote
    		
    		// Colocar ((SinkNode)node) para referenciar o No em que este Timer foi chamado
    		((SinkNode)node).broadcast(message);
    	}
    }

}