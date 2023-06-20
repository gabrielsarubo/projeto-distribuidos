/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projects.trabalhorec.nodes.nodeImplementations;

import java.awt.Color;

import projects.trabalhorec.nodes.messages.WsnMsg;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;

import utils.Util;

public class SensorNode extends Node {

	// Armazenar o no que sera usado para alcancar a Estacao-Base
	private Node proximoNoAteEstacaoBase;

	// Armazena o No Estacao-Base atual que este Sensor deve mandar msg
	private Node estacaoBasePai;

	private int sequencia = 0;
	private int tempoEnvio = 0;
	private int tempoRound = 0;
	
	private int nSaltosDesdeOrigem;

	@Override
	public void handleMessages(Inbox inbox) {
		while (inbox.hasNext()) {
			Message message = inbox.next();

			if (message instanceof WsnMsg) {
				Boolean shouldBroadcast = Boolean.TRUE;// deve transmitir em Broacast?
				WsnMsg wsnMessage = (WsnMsg) message;// Converter message para um objeto do tipo WsnMsg

				// A mensagem eh uma transmissao broadcast,
				// portanto, este No devera atualizar a rota
				if (wsnMessage.tipoMsg == 0) {
					/*
					 * Se a condicao abaixo for verdade:
					 *   (proximoNoAteEstacaoBase == null)
					 * isso siginifca que este No Sensor nao possui um
					 * proximo No Ate Estacao-Base, logo, ele esta "perdido",
					 * nao possui um caminho de volta para o Sink
					 * 
					 * Tambem pode significar que a Mensagem tipo-0 esta "indo" para os outros Nos
					 * Sensores e tentando Estabelecer uma Rota (ou arvore de roteamento)
					 */
					if (proximoNoAteEstacaoBase == null) {
						wsnMessage.nSaltosDesdeOrigem++;
						this.nSaltosDesdeOrigem = wsnMessage.nSaltosDesdeOrigem;
						estacaoBasePai = wsnMessage.origem;
						proximoNoAteEstacaoBase = inbox.getSender();// proximo No ate Estacao-Base vai ser o No que
																	// enviou a mensagem para este SensorNode
						this.setColor(wsnMessage.origem.getColor());
					} else if (wsnMessage.nSaltosDesdeOrigem >= (this.nSaltosDesdeOrigem-1)) {
						/**
						 * Caso o programa entre nesta condicao, isso quer dizer que:
						 * - a mensagem eh do tipo-0, ou seja, um broadcast
						 * - proximoNoAteEstacaoBase != null, ou seja, existe uma "referencia" para algum Sink
						 * 	 (perceba que essa referencia pode ser para um Sink que existe ou nao existe mais)
						 * - o numero de saltos desde a origem da Mensagem atual eh maior ou igual
						 *   ao numero de saltos atual deste No Sensor
						 *   
						 *   caso a ultima condicao acima for verdade, entao deve-se parar
						 *   de encaminhar (em broadcast) esta mensagem
						 */
						shouldBroadcast = Boolean.FALSE;
					} else {
						/**
						 * Caso nenhuma das condicoes acima for verdade, isso quer dizer que:
						 * - ainda existe uma "referencia" para algum Sink
						 * - a mensagem esta vindo de um Sink MAIS PERTO
						 *   OU a mensagem esta vindo do MESMO Sink, e se esse for o caso,
						 *   o algoritmo entra em ELSE, e este Sensor deve apenas parar de encaminhar
						 *   esta mensagem
						 *   
						 *   se a mensagem esta vindo de um Sink MAIS PERTO
						 *   && a origem da Mensagem vem de um Sink diferente do atual,
						 *   entao defina a origem desta Mensamge (novo Sink)
						 *   como a nova estacao base para onde este Sensor deve enviar mensagens tipo-1
						 */
						if (wsnMessage.origem.ID != this.estacaoBasePai.ID) {
							wsnMessage.nSaltosDesdeOrigem++;
							this.nSaltosDesdeOrigem = wsnMessage.nSaltosDesdeOrigem;
							estacaoBasePai = wsnMessage.origem;
							proximoNoAteEstacaoBase = inbox.getSender();
							this.setColor(wsnMessage.origem.getColor());
						} else {
							shouldBroadcast = Boolean.FALSE;
						}
					}
				} else if (wsnMessage.tipoMsg == 1) {
					// Se entrar neste IF, isso significa que a Mensagem esta "voltando" para o No Estacao-Base
					// ou seja, este No Sensor (ou outro No) quer "enviar" uma Mensagem ao No Estacao-Base
					shouldBroadcast = Boolean.FALSE;
					
					// previnir que este sensor envie uma mensagem ao proximoNo
					// enquanto nao houver um proximo No ate estacao-base
					if (proximoNoAteEstacaoBase != null) {
						this.send(wsnMessage, proximoNoAteEstacaoBase);
					}
				}

				// Se shouldBroadcast for TRUE, entao este No Sensor continua transmitindo a
				// Mensagem aos vizinhos
				// caso contrario, este No Sensor nao transmite a Mensagem para os Nos vizinhos
				if (shouldBroadcast) {
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
		 * Obs.: Este metodo executa todo round e tbm eh executado antes de handleMessages()
		 * Ele se comporta como um "contador", o qual so vai parar quando o round do simulador
		 * atingir a mesma qtd de rounds deste No (armazenado em tempoEnvio)
		 * 
		 * Eh este metodo que vai enviar a Mensagem "de volta" ao Sink indefinidamente
		 * 
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
		// msg tipo-0, ou seja, receber um pacote em broadcast
		if (proximoNoAteEstacaoBase != null) {
			if (tempoEnvio < tempoRound) {
				WsnMsg wsnMessage = new WsnMsg(sequencia, this, 1);
				sequencia++;
				send(wsnMessage, proximoNoAteEstacaoBase);
				tempoRound = 0;
			}
			tempoRound++;
		}
		
		/**
		 * Independente se ja existe um proximoNoAteEstacaoBase ou nao
		 * este contador ira resetar este No Sensor quando o simulador fizer 100 rounds
		 * desde quando este Sensor foi criado, ou seja, quando este Sensor tiver "100 rounds de idade"
		 * -- a logica para "resetar este No Sensor" eh definindo null para "proximoNoAteEstacaoBase" --
		 * 
		 * Vale lembrar que "this.tempoRound" sera resetado automaticamente quando for o momento
		 * deste Sensor enviar uma Mensagem tipo-1 para o Sink
		 */
		if (this.tempoRound == 100) {
			proximoNoAteEstacaoBase = null;

			this.setColor(Color.BLACK);
		}
	}

	@Override
	public void init() {
		// throw new UnsupportedOperationException("Not supported yet."); //To change
		// body of generated methods, choose Tools | Templates.
		// o tempo (em rounds) de envio estara sempre entre 50 e 149
		tempoEnvio = 101 + Util.nextInt(51);
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
		/**
		 * Ao final de cada "step" criar um timer a cada 50 rounds que executa uma tarefa
		 * essa tarefa envia uma Mensagem com send() para "estacaoBasePai", ou seja,
		 * o Sink Node ligado a esse Sensor, apos esse Sink receber a Msg
		 * ele retorna a este Sensor que o Sink "ainda esta vivo"
		 * 
		 * Se ainda estiver vivo, este sensor nao faz nada
		 * senao, este Sensor deve "limpar" seus atributos para que este possa
		 * receber um novo Sink
		 */
	}

	@Override
	public void checkRequirements() throws WrongConfigurationException {
		// throw new UnsupportedOperationException("Not supported yet."); //To change
		// body of generated methods, choose Tools | Templates.
	}

	@NodePopupMethod(menuText = "Mostrar Atributos deste No-Sensor")
	public void mostrarAtributosDesteSensor() {
		System.out.println("----\nExibindo atributos de SensorNode-"+this.ID);
		System.out.println("Proximo No ate estacao-base = " + proximoNoAteEstacaoBase);
		System.out.println("nSaltosDesdeOrigem = "+nSaltosDesdeOrigem);
		System.out.println("tempoEnvio, envia msg a cada = "+tempoEnvio+" rounds");
		System.out.println("tempoRound (tempo de vida) = "+tempoRound);
	}

}
