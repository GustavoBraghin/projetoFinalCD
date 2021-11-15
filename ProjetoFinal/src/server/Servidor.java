package server;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

/*OBJETIVOS DO SERVIDOR:
JUIZ DO JOGO

SABE QUANTOS PARTICIPANTES TEM (OK)
SABE QUANTOS PALITOS CADA PARTICIPANTE TEM (OK)
SABE QUANTOS PALITOS O PARTICIPANTE COLOCOU NA RODADA (OK)
VERIFICAR SE ALGUM PARTICIPANTE ACERTOU (OK)

ENVIAR A RESPOSTA NO CHAT (OK)

REFINAMENTOS:

- ARRUMAR OS SYSTEM OUT DA CLASSE PARA APARECER APENAS O NECESSÁRIO
- MELHORAR MENSAGENS DE INICIO DO JOGOS, INICIO DE NOVA RODADA, FINAL DO JOGO, ETC
- CRIAR FUNCOES PARA DEIXAR O CÓDIGO MAIS ORGANIZADO
 */
public class Servidor {

	public static void main(String[] args) {

		byte[] entrada=new byte[1000], saida=new byte[1000]; 
		String mensagem;
		int qtdParticipantes = 0;
		int qtdPalitos = 0;
		List<Integer> qtdPalitosParticipante = new ArrayList<Integer>();
		List<Integer> qtdPalitosnaMao = new ArrayList<Integer>();
		List<Integer> palpites = new ArrayList<Integer>();

		try {
			DatagramSocket socket = new DatagramSocket(12345);
			DatagramPacket dgEntrada = new DatagramPacket(entrada, entrada.length);  
			List<DatagramPacket> saidas= new ArrayList<DatagramPacket>();
			List<String> nomeCliente = new ArrayList<String>();

			while(true) {
				System.out.println("Servidor UDP pronto.");

				//RECEBE MENSAGEM DO CLIENTE
				socket.receive(dgEntrada);
				mensagem = (new String(entrada)).trim().split("#")[0]; 
				System.out.println("chegou MENSAGEM: "+ mensagem);

				if(mensagem.equals("FIM")) 
					break;
				if (mensagem.equals("CONECTA")) {

					//ENVIA MENSAGEM "OK" DE SAIDA PARA O CLIENTE QUE SE CONECTOU
					System.out.println("Conexão com cliente "+  dgEntrada.getAddress().getHostAddress() +":"+ 
							dgEntrada.getPort());
					saida = "OK#".getBytes();
					DatagramPacket x = new DatagramPacket(saida,  saida.length, dgEntrada.getAddress(), 
							dgEntrada.getPort());
					Servidor.enviarMensagemIndividual(saida, x, socket);

					//ADICIONA O PACOTE DE SAÍDA QUE FOI ENVIADO PARA O CLIENTE EM UM ARRAY
					saidas.add(x);

					//ADICIONA O QTD DE PALITOS INICIAIS NO VETOR
					qtdPalitosParticipante.add(3);

					//RECEBE NOME DO CLIENTE 
					socket.receive(dgEntrada);
					mensagem = (new String(entrada)).trim().split("#")[0];
					System.out.println("Nome do cliente recebido -> " + mensagem);
					nomeCliente.add(mensagem);
					System.out.println("Nome do cliente no vetor -> " + nomeCliente.get(qtdParticipantes));
					qtdParticipantes++;

				} else {
					boolean isStart = false;

					//CONFERE SE ALGUM CLIENTE MANDOU A MENSAGEM "START" PARA COMEÇAR O JOGO
					for(String name : nomeCliente) {
						if(mensagem.equals(new String(name + ":START"))) {

							//ENVIA A MENSAGEM DE START ENVIADA PELO CLIENTE PARA TODOS DO CHAT
							mensagem = new String (mensagem + "#");
							saida = mensagem.getBytes();
							Servidor.enviarMensagemTodos(saida, saidas, socket);

							mensagem = new String("Vamos começar#");
							saida = mensagem.getBytes();
							System.out.println("START GAME");
							isStart = true;
							break;
						}
					}

					//SE ALGUM CLIENTE TIVER ENVIADO O "START" ENVIA RESPOSTA DO SERVIDOR PARA TODOS
					if(isStart) {
						Servidor.enviarMensagemTodos(saida, saidas, socket);
						System.out.println("Mensagem -> " + mensagem + "... enviada");
						boolean verificaVencedor = false;

						/*CONTINUA DESENVOLVIMENTO DO GAME... COMEÇO DO JOGO AQUI...
						AQUI COMEÇA A RODADA
						..........................................................*/

						do {
							int i=0;

							//FOR PARA RECEBER OS PALITOS QUE CADA UM COLOCARÁ
							for(DatagramPacket sai : saidas) {

								//ENVIA MENSAGEM PARA TODOS
								mensagem = (nomeCliente.get(i) + ", Digite quantos palitos colocara na rodada:#");
								saida = mensagem.getBytes();
								Servidor.enviarMensagemTodos(saida, saidas, socket);

								//RECEBE QTD DE PALITOS QUE CLIENTE COLOCOU NA RODADA
								do {
									socket.receive(dgEntrada);
									mensagem = (new String(entrada)).trim().split("#")[0];
									mensagem = (mensagem.split(":")[1]);
									System.out.println("Mensagem Recebida -> " + mensagem );
									try {
										qtdPalitos = Integer.parseInt(mensagem);
									} catch (NumberFormatException e){
										mensagem = "-1";
									}
								}while (!mensagem.equals("0") && !mensagem.equals("1") && 
										!mensagem.equals("2") && !mensagem.equals("3") || dgEntrada.getPort() != sai.getPort());

								//DEVE ARMAZENAR QTD DE PALITOS QUE CADA CLIENTE COLOCOU
								qtdPalitos = Integer.parseInt(mensagem);
								qtdPalitosnaMao.add(qtdPalitos);

								//ENVIA UM OK PARA CONFIRMAR QUE RECEBEU
								mensagem = ("RECEBIDO#");
								saida = mensagem.getBytes();
								Servidor.enviarMensagemTodos(saida, saidas, socket);

								i++;
							}

							i=0;

							//ENVIA MENSAGEM PARA INFORMAR ETAPA DOS PALPITES
							mensagem = ("PALPITES...#");
							saida = mensagem.getBytes();
							Servidor.enviarMensagemTodos(saida, saidas, socket);

							//FOR PARA RECEBER OS PALPITES DE CADA JOGADOR
							for(DatagramPacket sai : saidas) {

								//ENVIA MENSAGEM PARA TODOS
								mensagem = (nomeCliente.get(i) + ", Digite o seu palpite para a rodada:#");
								saida = mensagem.getBytes();
								Servidor.enviarMensagemTodos(saida, saidas, socket);

								//RECEBE QTD DO PALPITE DO JOGADOR PARA A RODADA
								do {
									socket.receive(dgEntrada);
									mensagem = (new String(entrada)).trim().split("#")[0];
									mensagem = (mensagem.split(":")[1]);

									try {
										qtdPalitos = Integer.parseInt(mensagem);
									} catch (NumberFormatException e){
										mensagem = "-1";
									}

									System.out.println("Mensagem Recebida -> " + mensagem );
								}while (mensagem.equals("-1") || dgEntrada.getPort() != sai.getPort());

								//DEVE ARMAZENAR QTD DE PALITOS QUE CADA CLIENTE COLOCOU
								palpites.add(qtdPalitos);

								//ENVIA UM OK PARA CONFIRMAR QUE RECEBEU
								mensagem = ("RECEBIDO#");
								saida = mensagem.getBytes();
								Servidor.enviarMensagemTodos(saida, saidas, socket);

								i++;
							}

							i=0;
							//TRATAR PALPITES E TOTAL PARA SE E QUEM ACERTOU.
							int soma=0;

							//CALCULA SOMA DOS PALITOS NA RODADA						
							for (int x=0; x<qtdParticipantes; x++) {
								soma = soma + qtdPalitosnaMao.get(x);							
							}
							System.out.println("Soma: -> " + soma );

							//VERIFICA SE ALGUM PALPITE CORRESPONDE A SOMA
							boolean verificaAcerto = false; 

							for (int x=0; x<qtdParticipantes; x++) {
								if(palpites.get(x) == soma) {
									verificaAcerto = true;
									qtdPalitosParticipante.set(x, qtdPalitosParticipante.get(x)-1);

									if(qtdPalitosParticipante.get(x) == 0) {
										verificaVencedor = true;
										mensagem = (nomeCliente.get(x) + " VENCEU A PARTIDA!!!#");
										saida = mensagem.getBytes();
										Servidor.enviarMensagemTodos(saida, saidas, socket);
									}else {
										mensagem = (nomeCliente.get(x) + " Acertou o palpite... Agora tem "
												+ qtdPalitosParticipante.get(x) + " palitos#");
										saida = mensagem.getBytes();
										Servidor.enviarMensagemTodos(saida, saidas, socket);
									}

								}
							}
							//SE NÃO, ENVIA MENSAGEM DE QUE NINGUEM ACERTOU E COMEÇA UMA NOVA RODADA
							if(!verificaAcerto) {
								mensagem = ("Nenhum jogador acertou, iniciando nova rodada...#");
								saida = mensagem.getBytes();
								Servidor.enviarMensagemTodos(saida, saidas, socket);
							}

							qtdPalitosnaMao.removeAll(qtdPalitosnaMao);
							palpites.removeAll(palpites);

						}while(!verificaVencedor);


						//SE NÃO TIVER RECEBIDO O START, APENAS ENVIA A MENSAGEM RECEBIDA PARA TODOS
					}else {
						mensagem = new String (mensagem + "#");
						saida = mensagem.getBytes();
						Servidor.enviarMensagemTodos(saida, saidas, socket);
					}
				}
			}
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void enviarMensagemTodos(byte[] msg, List<DatagramPacket> saidas, DatagramSocket socket) {

		for(DatagramPacket sai : saidas) {
			sai.setData(msg);
			sai.setLength(msg.length);
			try {
				socket.send(sai);
				System.out.println("Enviou mensagem para port: " + sai.getPort());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void enviarMensagemIndividual(byte[] msg, DatagramPacket dgSaida, DatagramSocket socket) {

		dgSaida.setData(msg);
		dgSaida.setLength(msg.length);
		try {
			socket.send(dgSaida);
			System.out.println("Enviou mensagem para port: " + dgSaida.getPort());
		} catch (IOException e) { 
			e.printStackTrace(); 
		}

	}
}
