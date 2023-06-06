/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projects.sample7.nodes.nodeImplementations;

import java.awt.Color;
import projects.sample7.nodes.messages.Packet;
import projects.sample7.nodes.timers.TimerScheduler;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;

public class MyNode extends Node {

    @Override
    public void handleMessages(Inbox inbox) {
    	while (inbox.hasNext()) {
            Message message = inbox.next();
            if (message instanceof Packet) {
            	Packet networkMessage = (Packet) message;
           		System.out.println(this.ID+" recebe resposta de "+networkMessage.origem.ID);
           		
            }            
        }
    }

    @Override
    public void preStep() {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @NodePopupMethod(menuText = "Send message to other node")
    public void construirRoteamento() {        
        Packet networkMessage = new Packet(1, this, null, this, 0);
        TimerScheduler timer = new TimerScheduler(networkMessage);
        timer.startRelative(50, this);
    }

    @Override
    public void init() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    	this.setColor(Color.blue);    	
    
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
