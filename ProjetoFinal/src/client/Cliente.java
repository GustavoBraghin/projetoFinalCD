package client;

import java.net.*;
import java.io.*;

/*
OBJETIVOS DO CLIENTE
ENVIAR QTD PALITOS PARA SERVIDOR 
ENVIAR PALPITE PARA O SERVIDOR (OK)
RETIRAR UM PALITO 
ESCUTAR APENAS MENSAGENS DO SERVIDOR (OK)

REFINAMENTOS:
DEIXAR APENAS OS SYSTEM OUTS NECESSÁRIOS

 */

public class Cliente extends Thread {
	Tela tela;
	DatagramSocket socket;
	DatagramPacket dgSaida;
	DatagramPacket dgEntrada;
	String mensagem;
	String nomeCliente;
	String host;
	
	byte[] saida=new byte[1000], entrada=new byte[1000];  
	
	public Cliente(Tela interf) {
		tela = interf;
	}
	
	public boolean conectar(String ihost, String nome) {
		
		nomeCliente = nome;
		host = ihost;
		saida="CONECTA#".getBytes();
		
		try {
			socket = new DatagramSocket();
			dgSaida = new DatagramPacket(saida, saida.length,  
				InetAddress.getByName(host), 12345);
			dgEntrada = new DatagramPacket(entrada, entrada.length); 
			
			// conecta com o servidor
			try {
				//ENVIA MENSAGEM DE "CONECTA#"
				socket.send(dgSaida);
				
				//RECEBE "OK" DO SERVER
				socket.receive(dgEntrada);
				mensagem = (new String(entrada)).trim().split("#")[0];
				System.out.println("Recebeu -> " + mensagem);
				
				//ENVIA NOME DO CLIENTE PARA O SERVER
				String clientName = nomeCliente + "#";
				dgSaida.setData(clientName.getBytes());
				dgSaida.setLength(clientName.getBytes().length);
				socket.send(dgSaida);
				System.out.println("Enviou nome do cliente: " + clientName);
				
				//ADICIONADO PARA AVISAR QUANDO ENTRA NO BATE PAPO
				//TAMBÉM ENVIA UM PACKET PARA O CLIENTE
				this.enviarMensagem("Entrei"); 
			
			} catch (IOException e) { 
				e.printStackTrace(); 
			}
            
			// ouve msgs de entrada
	        new Thread(){
		        public void run(){
		            System.out.println("Ouvindo o servidor");
			  		try {
						
			  			do {
							socket.receive(dgEntrada);
							mensagem = (new String(entrada)).trim().split("#")[0];
							System.out.println("Recebeu -> " + mensagem);
							tela.adicionarMensagem(mensagem);
						} while (!mensagem.equals("CMD|DESCONECTAR"));  
					
			  		} catch (IOException e) { 
						e.printStackTrace(); 
					}
		        }
		      }.start();

			return true;
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
		return false;
	}
	
	public void enviarMensagem(String msg) {
		String texto = (nomeCliente+":"+msg+"#");
		saida = texto.getBytes();
		
		try {
			dgSaida.setData(saida);
			dgSaida.setLength(texto.length());
			socket.send(dgSaida);
			System.out.println("Enviou mensagem: " + msg);
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
	}
	public void desconectar() {
		this.enviarMensagem("Saiu do batepapo"); // ADICIONADO PARA AVISAR QUANDO SAI DO BATEPAPO
		socket.close();
	}
}


