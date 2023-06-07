package projects.trabalhofinal.nodes.nodeImplementations;


import java.awt.Color;
import java.awt.Graphics;

import projects.trabalhofinal.nodes.messages.WsnMsg;

import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.Node.NodePopupMethod;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;
import sinalgo.tools.Tools;
import utils.Util;
import projects.trabalhofinal.nodes.messages.WsnMsg;
import projects.trabalhofinal.nodes.timers.WsnMessageTimer;
 
/**
 * @author gabrielsarubo
 */

public class SinkNode extends Node {

    @Override
    public void handleMessages(Inbox inbox) {
        while (inbox.hasNext()) {
            Message message = inbox.next();
            if (message instanceof WsnMsg) {
                WsnMsg wsnMessage = (WsnMsg) message;                
                if (wsnMessage.tipoMsg == 1) {
                    System.out.println("Sink-" + this.ID + " recebe mensagem " + wsnMessage.sequenceID + " de Node-" + wsnMessage.origem.ID);
                    Tools.appendToOutput("Sink-" + this.ID + " recebe mensagem " + wsnMessage.sequenceID + " de Node-" + wsnMessage.origem.ID + "\n");
                }
            }
        }
    }

    @Override
    public void preStep() {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
       
    }

    @NodePopupMethod(menuText = "Construir Arvore de Roteamento")
    public void construirRoteamento() {
    	System.out.println("Arvore de roteamento construida para Sink-"+this.ID);

    	/**
    	 * new WsnMsg()
    	 * 1: representa o ID da sequencia
    	 * this: aponta para o No de origem, neste caso No de origem eh este proprio Sink
    	 * null: aponta para o No de destino, neste caso, nao existe um destino
    	 * this: aponta para o No que vai reencaminhar esta mensagem, neste caso, este mesmo Sink
    	 * 0: msg do tipo 0 serve para estabelecimento de rotas
    	 */
        WsnMsg wsnMessage = new WsnMsg(1, this, null, this, 0, this.getColor());
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
    	super.drawNodeAsSquareWithText(g, pt, highlight, "Sink", 10, Color.BLACK);
    }

}




