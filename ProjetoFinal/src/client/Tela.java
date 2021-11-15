package client;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Tela extends JFrame {
		private static final long serialVersionUID = 1L;
		private Cliente cliente;  
		private boolean conectado;  
		private JTextField edtHost;  
		private JTextField edtNome;
		private JButton btnConectarDesconectar;  
		private JTextArea taMensagens;
		private JTextField edtMensagem;  
		private JButton btnEnviar;

		public static void main(String[] args) {  @SuppressWarnings("unused")
			Tela exemplo = new Tela();
		}

		public Tela() {  
			super();
			this.cliente = new Cliente(this);  
			initialize();
			this.setVisible(true);
		}

		private void initialize() {  
			this.getContentPane().setLayout(null);  
			this.setSize(538, 355);  
			this.setTitle("Chat - Cliente");  
			this.setLocation(217, 172);  
			this.setResizable(false);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			JLabel lblHost = new JLabel("Servidor");  
			lblHost.setBounds(new Rectangle(10, 13, 57, 13));  
			this.getContentPane().add(lblHost);

			this.edtHost = new JTextField("localhost");  
			this.edtHost.setBounds(new Rectangle(62, 10, 140, 22));  
			this.getContentPane().add(edtHost, null);

			JLabel lblNome = new JLabel("Nome");  
			lblNome.setBounds(new Rectangle(213, 13, 57, 13));  
			this.getContentPane().add(lblNome, null);

			this.edtNome = new JTextField();  
			this.edtNome.setBounds(new Rectangle(250, 10, 140, 22)); 
			this.getContentPane().add(edtNome, null);

			this.btnConectarDesconectar = new JButton("Conectar");  
			this.btnConectarDesconectar.setBounds(new Rectangle(400, 10, 110, 22));  
			this.getContentPane().add(btnConectarDesconectar, null);

			this.taMensagens = new JTextArea();  
			this.taMensagens.setEditable(false);

			JScrollPane sbMensagens = new JScrollPane(this.taMensagens, 
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);  
			sbMensagens.setBounds(new Rectangle(10, 40, 500, 235));
			this.getContentPane().add(sbMensagens, null);

			JLabel lblMensagem = new JLabel("Mensagem");  
			lblMensagem.setBounds(new Rectangle(10, 287, 75, 14));  
			this.getContentPane().add(lblMensagem, null);

			this.edtMensagem = new JTextField();  
			this.edtMensagem.setBounds(new Rectangle(80, 285, 342, 22));  
			this.edtMensagem.setEnabled(false);  this.getContentPane().add(edtMensagem, null);

			this.btnEnviar = new JButton("Enviar");  
			this.btnEnviar.setBounds(new Rectangle(432, 285, 78, 22));  
			this.btnEnviar.setEnabled(false);  
			this.getContentPane().add(btnEnviar, null);

			this.btnConectarDesconectar.addMouseListener(
					new MouseAdapter() {  
						public void mouseClicked(MouseEvent e) {
							conectarDesconectar();
						}
					});

			this.btnEnviar.addMouseListener(new MouseAdapter() {  
				public void mouseClicked(MouseEvent e) {  
					enviarMensagem();
				}
				});

			this.edtMensagem.addActionListener(new ActionListener() {  
				public void actionPerformed(ActionEvent e) {  
					enviarMensagem();
				}
			});
			}

		public void adicionarMensagem(String mensagem) {  
			this.taMensagens.insert(mensagem+"\n\n", 0);
		}

		private void conectarDesconectar() {  
			if (this.conectado) { // desconectar
				this.cliente.desconectar();  
				this.conectado = false;
			}
			else { // conectar
				if (this.edtNome.getText().length() == 0) {  
					this.adicionarMensagem("Informe o nome");  
					this.edtNome.requestFocus();
				}
				else {
					this.conectado = this.cliente.conectar(this.edtHost.getText(), this.edtNome.getText());  
					if (!this.conectado)
					   this.adicionarMensagem("Erro de conexão");
				}
			}
			this.habilitar();
		}

		private void enviarMensagem() {
				this.cliente.enviarMensagem(this.edtMensagem.getText());  
				this.edtMensagem.setText("");  
				this.edtMensagem.requestFocus();
		}

		private void habilitar() {  
			if (this.conectado)
				this.btnConectarDesconectar.setText("Desconectar");  
			else  
				this.btnConectarDesconectar.setText("Conectar");  
			this.edtHost.setEnabled(!this.conectado);  
			this.edtNome.setEnabled(!this.conectado);  
			this.edtMensagem.setEnabled(this.conectado);  
			this.btnEnviar.setEnabled(this.conectado);
		}
	}



