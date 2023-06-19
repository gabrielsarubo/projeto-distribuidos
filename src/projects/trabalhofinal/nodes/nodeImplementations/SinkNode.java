package projects.trabalhofinal.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;

import projects.trabalhofinal.nodes.messages.WsnMsg;

import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;
import utils.Util;
import projects.trabalhofinal.nodes.timers.WsnMessageTimer;
 
/**
 * @author gabrielsarubo
 */

public class SinkNode extends Node {
	
	private int tempoRound = 0;

    @Override
    public void handleMessages(Inbox inbox) {
        while (inbox.hasNext()) {
            Message message = inbox.next();
            if (message instanceof WsnMsg) {
                WsnMsg wsnMessage = (WsnMsg) message;
                // Se entrar na condicao abaixo, isso significa que a Mensagem voltou
                // para o SinkNode que originalmente envio a Mensagem aos Nos Sensores
                if (wsnMessage.tipoMsg == 1) {
                    System.out.println("Sink-" + this.ID + " recebe mensagem " + wsnMessage.sequenceID + " de Node-" + wsnMessage.origem.ID);
                    //Tools.appendToOutput("Sink-" + this.ID + " recebe mensagem " + wsnMessage.sequenceID + " de Node-" + wsnMessage.origem.ID + "\n");
                }
            }
        }
    }

    @Override
    public void preStep() {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        /**
         * A cada 50 rounds sera feito um novo broadcast,
         * ou seja, sera construido uma nova arvore de roteamento a partir deste Sink
         */
    	if (tempoRound == 50) {
    		this.construirRoteamento();
    		tempoRound = 0;
    	}
    	tempoRound++;
    }

    @NodePopupMethod(menuText = "Construir Arvore de Roteamento")
    public void construirRoteamento() {
    	System.out.println("Sera construida uma Arvore de Roteamento para o Sink-"+this.ID+" no proximo round.");

    	/**
    	 * new WsnMsg()
    	 * 1: representa o ID da sequencia
    	 * this: aponta para o No de origem, neste caso No de origem eh este proprio Sink
    	 * null: aponta para o No de destino, neste caso, nao existe um destino
    	 * 0: msg do tipo 0 serve para estabelecimento de rotas
    	 */
        WsnMsg wsnMessage = new WsnMsg(1, this, 0);
        WsnMessageTimer timer = new WsnMessageTimer(wsnMessage);
        timer.startRelative(1, this);
    }

    @Override
    public void init() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        this.setColor(Util.generateRandomColor());
    }

    @Override
    public void neighborhoodChange() {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void postStep() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void checkRequirements() throws WrongConfigurationException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
    	// TODO Auto-generated method stub
    	super.drawNodeAsSquareWithText(g, pt, highlight, "Sink-"+this.ID, 10, Color.BLACK);
    }

}




