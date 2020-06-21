// Recebe um pacote de algum cliente
// Separa o dado, o endere�o IP e a porta deste cliente
// Imprime o dado na tela

import java.io.*;
import java.net.*;

class UDPServer {
    public static void main(String args[]) throws Exception {
        // cria socket do servidor com a porta 9876
        DatagramSocket serverSocket = new DatagramSocket(9876);

        byte[] receiveData = new byte[512];

        while (true) {
            // declara o pacote a ser recebido
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            // recebe o pacote do cliente
            serverSocket.receive(receivePacket);

            // pega os dados, o endere�o IP e a porta do cliente
            // para poder mandar a msg de volta
            String sentence = new String(receivePacket.getData());
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();

            File myFile = new File("fileUDPOut.txt");
            PrintWriter printWriter = new PrintWriter(myFile);
            String s = new String();
            s=sentence;




            // nao sei como fazer aqui
            s.replaceAll("\\00","");
            printWriter.print(s);
            printWriter.close();

            // ta colocando no arquivo um monte de \00
            // mas o socket ta funcionando

            System.out.println("Mensagem recebida: " + sentence);
            //serverSocket.close();
        }
    }
}
