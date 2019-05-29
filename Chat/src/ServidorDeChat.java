import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Vector;
public class ServidorDeChat extends Thread {

    public static void main (String args[]) {
        // instancia o vetor de clientes conectados
        clientes = new Vector();
        try {
			// criando um socket que fica escutando a porta 2222.
            ServerSocket s = new ServerSocket(2222);
            // Loop principal.
            while (true) {
                
                System.out.print("Esperando alguem se conectar...");
                Socket conexao = s.accept();
                System.out.println(" Conectou!");
                
                Thread t = new ServidorDeChat(conexao);
                t.start();
// voltando ao loop, esperando mais alguém se conectar.
            }
        } catch (IOException e) {
            
            System.out.println("IOException: " + e);
        }
    }
    // Parte que controla as conexões por meio de threads.
    // Note que a instanciação está no main.
    private static Vector clientes;
    // socket deste cliente
    private Socket conexao;
// nome deste cliente
    private String meuNome;
    // construtor que recebe o socket deste cliente

    public ServidorDeChat(Socket s) {
        conexao = s;
    }
    

    public void run() {
        try {
            
            BufferedReader entrada = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
            PrintStream saida = new PrintStream(conexao.getOutputStream());
            
            meuNome = entrada.readLine();
            
            if (meuNome == null) {
                return;
            }
            
            clientes.add(saida);
            
            String linha = entrada.readLine();
            
            while (linha != null && !(linha.trim().equals("sair"))) {
                
                sendToAll(saida, " disse: ", linha);
// espera por uma nova linha.
                linha = entrada.readLine();
            }
            
            sendToAll(saida, " saiu ", "do chat!");
            clientes.remove(saida);
            conexao.close();
        } catch (IOException e) {
            
            System.out.println("IOException: " + e);
        }
    }
    // enviar uma mensagem para todos, menos para o próprio

    public void sendToAll(PrintStream saida, String acao,String linha) throws IOException {
        Enumeration e = clientes.elements();
        while (e.hasMoreElements()) {
            
            PrintStream chat = (PrintStream) e.nextElement();
            
            if (chat != saida) {
                chat.println(meuNome + acao + linha);
            }
        }
    }
}