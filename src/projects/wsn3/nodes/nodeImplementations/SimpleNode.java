/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projects.wsn3.nodes.nodeImplementations;

import java.awt.Color;

import projects.wsn3.nodes.messages.WsnMsg;
import projects.wsn3.nodes.timers.WsnMessageTimer;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;
import sinalgo.tools.Tools;

import java.util.Random;


public class SimpleNode extends Node {

    //Armazenar o no que sera usado para alcancar a Estacao-Base
    private Node proximoNoAteEstacaoBase;
    
    //Armazena o numero de sequencia da ultima mensagem recebida
    private Integer sequenceNumber = 0;

    private Node origem;
    private Random random = new Random();
    private int sequencia = 0;
    private int tempoEnvio = 0;
    private int tempoRound = 0;

    @Override
    public void handleMessages(Inbox inbox) {
        while (inbox.hasNext()) {
            Message message = inbox.next();
            if (message instanceof WsnMsg) {
                Boolean encaminhar = Boolean.TRUE;
                WsnMsg wsnMessage = (WsnMsg) message;                
                if (wsnMessage.forwardingHop.equals(this)) { // A mensagem voltou. O no deve descarta-la
                    encaminhar = Boolean.FALSE;
                } else if (wsnMessage.tipoMsg == 0) { // A mensagem e um flood. Devemos atualizar a rota
                    if (proximoNoAteEstacaoBase == null) {
                        
                        System.out.println("Node-" + this.ID + " recebe pacote de Sink-" + wsnMessage.origem.ID);
                        Tools.appendToOutput("Node-" + this.ID + " recebe pacote de Sink-" + wsnMessage.origem.ID + "\n");

                        proximoNoAteEstacaoBase = inbox.getSender();
                        sequenceNumber = wsnMessage.sequenceID;
                        this.setColor(Color.PINK);
                    } else if (sequenceNumber < wsnMessage.sequenceID) {
                    //Recurso simples para evitar loop.
                        //Exemplo: Noh A transmite em brodcast. Noh B recebe a
                        //msg e retransmite em broadcast.
                        //Consequentemente, noh A ira receber a msg. Sem esse
                        //condicional, noh A iria retransmitir novamente, gerando um loop
                        sequenceNumber = wsnMessage.sequenceID;
                    } else {
                        encaminhar = Boolean.FALSE;
                    }
                } else if (wsnMessage.tipoMsg == 1){
                    encaminhar = Boolean.FALSE;
                    this.send(wsnMessage, proximoNoAteEstacaoBase);
                }
                if (encaminhar) {
                    //Devemos alterar o campo forwardingHop(da mensagem) para armazenar o
                	//noh que vai encaminhar a mensagem.
                	System.out.println(this.ID+" recebe dados de "+wsnMessage.origem.ID);
                    wsnMessage.forwardingHop = this;
                    this.broadcast(wsnMessage);
                }
            }
        }
    }

    @Override
    public void preStep() {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
       if (this.proximoNoAteEstacaoBase != null){
        if (tempoEnvio < tempoRound){
            WsnMsg wsnMessage = new WsnMsg(sequencia, this, this.origem, this, 1);
            this.sequencia++;
            this.send(wsnMessage, proximoNoAteEstacaoBase);
            this.tempoRound = 0;
        }
        this.tempoRound++;
       }
    }

    /*@NodePopupMethod(menuText = "Construir Arvore de Roteamento")
    public void construirRoteamento() {
        this.proximoNoAteEstacaoBase = this;
        WsnMsg wsnMessage = new WsnMsg(1, this, null, this, 0);
        WsnMessageTimer timer = new WsnMessageTimer(wsnMessage);
        timer.startRelative(1, this);
    }*/

    @Override
    public void init() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        tempoEnvio = 100 + random.nextInt(200);
        this.setColor(Color.GREEN);
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
