package projects.trabalhofinal.nodes.nodeImplementations;


import java.awt.Color;

import projects.wsn3.nodes.messages.WsnMsg;

import sinalgo.configuration.WrongConfigurationException;

import sinalgo.nodes.Node;
import sinalgo.nodes.Node.NodePopupMethod;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;
import sinalgo.tools.Tools;
import projects.wsn3.nodes.messages.WsnMsg;
import projects.wsn3.nodes.timers.WsnMessageTimer;
 



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
        WsnMsg wsnMessage = new WsnMsg(1, this, null, this, 0);
        WsnMessageTimer timer = new WsnMessageTimer(wsnMessage);
        timer.startRelative(1, this);
    }

    @Override
    public void init() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        this.setColor(Color.ORANGE);
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

}




