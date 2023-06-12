/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projects.trabalhofinal.nodes.nodeImplementations;

import java.awt.Color;

import projects.trabalhofinal.nodes.messages.WsnMsg;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;

import utils.Util;

public class SensorNode extends Node {

	// Armazenar o no que sera usado para alcancar a Estacao-Base
	private Node proximoNoAteEstacaoBase;

	// Armazena o numero de sequencia da ultima mensagem recebida
	private Integer sequenceNumber = 0;

	private Node origem;

	private int sequencia = 0;
	private int tempoEnvio = 0;
	private int tempoRound = 0;

	@Override
	public void handleMessages(Inbox inbox) {
		System.out.println("\n---\nDentro de SensorNode-"+this.ID);
    	//System.out.println("Tamanho do inbox do Sensor-"+this.ID+" = "+inbox.size());

		while (inbox.hasNext()) {
			Message message = inbox.next();

			if (message instanceof WsnMsg) {
				Boolean shouldBroadcast = Boolean.TRUE;// deve transmitir em Broacast?
				WsnMsg wsnMessage = (WsnMsg) message;// Converter message para um objeto do tipo WsnMsg

				if (wsnMessage.tipoMsg == 0) { // A mensagem e um flood, deve-se atualizar a rota
					/*
					 * Se a condicao abaixo for verdade, siginifca que este No Sensor nao possui um
					 * proximo No Ate Estacao-Base, logo, ele esta "perdido", nao possui um caminho
					 * de volta para o Sink
					 * 
					 * Tambem pode significar que a Mensagem tipo-0 esta "indo" para os outros Nos
					 * Sensores e tentando Estabelecer uma Rota (ou arvore de roteamento)
					 */
					if (proximoNoAteEstacaoBase == null) {
						System.out.println("SensorNode-" + this.ID + " recebe mensagem do tipo-0 de SinkNode-"
								+ wsnMessage.origem.ID);

						//System.out.println("inbox.getSender() = "+inbox.getSender());
						//System.out.println("wsnMessage.forwardingHop = "+wsnMessage.forwardingHop);
						proximoNoAteEstacaoBase = inbox.getSender();// proximo No ate Estacao-Base vai ser o No que
																	// enviou a mensagem para este SensorNode
						sequenceNumber = wsnMessage.sequenceID;
						// System.out.println("O proximo no ate estacao-base do SensorNode-"+this.ID+"
						// eh o Node-"+proximoNoAteEstacaoBase.ID);

						this.setColor(wsnMessage.origem.getColor());
					} else {
						/**
						 * Se entrou aqui, eh porque ja existe um proximo No ate Estacao-Base logo,
						 * proximoNoAteEstacaoBase != null
						 * 
						 * TODO Adiconar condicao que seja cumprida no caso de um Novo No Estaca-Base
						 * for adicionado e ele estiver mais perto que o No Estacao-Base deste No Sensor
						 * 
						 * Para verificar se o novo Sink esta mais perto que o Sink antigo, devemos
						 * comparar a qtd de saltos ate cada Sink; a Nova Estacao-Base devera ser aquela
						 * que esta a menos saltos deste No
						 * 
						 * Caso o Novo Sink esteja mais perto (a menos saltos deste No Sensor) devemos
						 * atualizar o proximoNoAteEstacaoBase
						 */

						if (sequenceNumber < wsnMessage.sequenceID) {
							/**
							 * Recurso simples para evitar loop. Exemplo: Noh A transmite em brodcast. Noh B
							 * recebe a msg e retransmite em broadcast. Consequentemente, noh A ira receber
							 * a msg. Sem esse condicional, noh A iria retransmitir novamente, gerando um
							 * loop
							 */
							sequenceNumber = wsnMessage.sequenceID;
						} else {
							/**
							 * Evitar que este No transmita em broadcast novamente para tanto, sera
							 * atribuido false para a variavel "shouldBroadcast" dessa forma, nao sera feito um
							 * novo broadcast
							 */
							shouldBroadcast = Boolean.FALSE;
						}
					}
				} else if (wsnMessage.tipoMsg == 1) {
					// Se entrar neste IF, isso significa que a Mensagem esta "voltando" para o No
					// Estacao-Base
					// ou seja, este No Sensor esta "enviando" uma Mensagem ao No Estacao-Base
					shouldBroadcast = Boolean.FALSE;
					this.send(wsnMessage, proximoNoAteEstacaoBase);
				}

				// Se shouldBroadcast for TRUE, entao este No Sensor continua transmitindo a
				// Mensagem aos vizinhos
				// caso contrario, este No Sensor nao transmite a Mensagem para os Nos vizinhos
				if (shouldBroadcast) {
					/*
					 * Colocar dentro da futura Mensagem uma referencia para este No Sensor
					 * que vai dizer ao No que receber esta msg "qual No encaminhou a msg"
					 * 
					 * TODO Mas o inbox.getSender() ja nao faz isso?
					 */
					//wsnMessage.forwardingHop = this;
					this.broadcast(wsnMessage);
				}
			}
		}
	}

	@Override
	public void preStep() {
		// throw new UnsupportedOperationException("Not supported yet."); //To change
		// body of generated methods, choose Tools | Templates.
		/**
		 * Precisa existir um proximoNoAteEstacaoBase para que este NoSensor possa
		 * enviar uma Mensagem para a Estacao-Base, e continuar fazendo isso pra sempre
		 * 
		 * Portanto, para que este Sensor possa enviar Mensagens para outra Estacao-Base
		 * por exemplo para uma mais proxima, eh necessario que o
		 * proximoNoAteEstacaoBase deste No seja atribuido um valor null, desta forma,
		 * este Sensor estara aberto para receber o Novo proximo No ate estacao Base
		 */

		// A condicao abaixo verifica se existe um Proximo No para Estacao-Base
		// Lembrando que esse Proximo No soh existira depois que este Sensor receber uma
		// msg tipo-0
		if (proximoNoAteEstacaoBase != null) {
			if (tempoEnvio < tempoRound) {
				WsnMsg wsnMessage = new WsnMsg(sequencia, this, this.origem, 1);
				sequencia++;
				send(wsnMessage, proximoNoAteEstacaoBase);
				tempoRound = 0;
			}
			tempoRound++;
		}
	}

	@Override
	public void init() {
		// throw new UnsupportedOperationException("Not supported yet."); //To change
		// body of generated methods, choose Tools | Templates.
		// o tempo (em rounds) de envio estara sempre entre 51 e 149
		tempoEnvio = 50 + Util.nextInt(100);
		System.out.println("SensorNode-"+this.ID+" envia pacotes a cada "+tempoEnvio+" rounds");
		this.setColor(Color.BLACK);		
	}

	@Override
	public void neighborhoodChange() {
		// throw new UnsupportedOperationException("Not supported yet."); //To change
		// body of generated methods, choose Tools | Templates.
	}

	@Override
	public void postStep() {
		// throw new UnsupportedOperationException("Not supported yet."); //To change
		// body of generated methods, choose Tools | Templates.
	}

	@Override
	public void checkRequirements() throws WrongConfigurationException {
		// throw new UnsupportedOperationException("Not supported yet."); //To change
		// body of generated methods, choose Tools | Templates.
	}

	@NodePopupMethod(menuText = "Ver Proximo No Ate Estacao-Base")
	public void verProximoNoAteEstacaoBase() {
		System.out.println("O proximo No ate estacao-base deste Sensor eh " + proximoNoAteEstacaoBase);
	}

}
