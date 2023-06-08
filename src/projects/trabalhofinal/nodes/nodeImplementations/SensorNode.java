/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projects.trabalhofinal.nodes.nodeImplementations;

import java.awt.Color;

import projects.trabalhofinal.nodes.messages.WsnMsg;
import projects.trabalhofinal.nodes.timers.WsnMessageTimer;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;
import sinalgo.tools.Tools;

import java.util.Random;


public class SensorNode extends Node {

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
                WsnMsg wsnMessage = (WsnMsg) message;// Converter message para um objeto do tipo WsnMsg
                
                if (wsnMessage.forwardingHop.equals(this)) { // A mensagem voltou. O no deve descarta-la
                    encaminhar = Boolean.FALSE;
                } else if (wsnMessage.tipoMsg == 0) { // A mensagem e um flood, deve-se atualizar a rota
                    if (proximoNoAteEstacaoBase == null) {// Se entrar nesta condicao significa que a Mensagem esta "indo" para os outros Nos Sensores
                        System.out.println("SensorNode-" + this.ID + " recebe pacote de SinkNode-" + wsnMessage.origem.ID);
                        //Tools.appendToOutput("SensorNode-" + this.ID + " recebe pacote de Sink-" + wsnMessage.origem.ID + "\n");

                        proximoNoAteEstacaoBase = inbox.getSender();// proximo No ate Estacao-Base vai ser o No que enviou a mensagem para este SensorNode
                        sequenceNumber = wsnMessage.sequenceID;
                        System.out.println("O proximo no ate estacao-base do SensorNode-"+this.ID+" eh o Node-"+proximoNoAteEstacaoBase.ID);

                        this.setColor(wsnMessage.corOrigem);
                    } else if (sequenceNumber < wsnMessage.sequenceID) {// Se entrar nessa condicao significa que a Mensagem esta "voltando" para o SinkNode E o sequenceNumber eh menor que o ID (de sequencia) da mensagem
                    	// Recurso simples para evitar loop.
                        // Exemplo: Noh A transmite em brodcast. Noh B recebe a msg e retransmite em broadcast.
                        // Consequentemente, noh A ira receber a msg. Sem esse condicional, noh A iria retransmitir novamente, gerando um loop
                        sequenceNumber = wsnMessage.sequenceID;
                    } else {// Se entrar aqui eh porque a Mensagem esta voltando E o sequenceNumber deste No Sensor eh maior que o ID da msg
                    	// Evitar que a Mensagem continue sendo encaminhada
                        encaminhar = Boolean.FALSE;
                    }
                } else if (wsnMessage.tipoMsg == 1){
                	// Se entrar neste IF, isso significa que a Mensagem esta "voltando" para o No Estacao-Base
                    encaminhar = Boolean.FALSE;
                    this.send(wsnMessage, proximoNoAteEstacaoBase);
                }
                
                // Se encaminhar for TRUE, entao este No Sensor continua transmitindo a Mensagem aos vizinhos
                // caso contrario, este No Sensor vai parar de transmitir a Mensagem para os Nos vizinhos
                if (encaminhar) {
                    // Apontar o campo forwardingHop (da mensagem) para o No que vai encaminhar a mensagem
                	// System.out.println(this.ID+" recebe dados de "+wsnMessage.origem.ID);
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
            WsnMsg wsnMessage = new WsnMsg(sequencia, this, this.origem, this, 1, Color.red);
            this.sequencia++;
            this.send(wsnMessage, proximoNoAteEstacaoBase);
            this.tempoRound = 0;
        }
        this.tempoRound++;
       }
    }

    @Override
    public void init() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    	// o tempo (em rounds) de envio sera sempre entre 100 e 200
        tempoEnvio = 100 + random.nextInt(200);
        this.setColor(Color.BLACK);
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
