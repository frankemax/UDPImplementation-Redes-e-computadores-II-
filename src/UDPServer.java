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
        byte[] sendData = new byte[1024];
        FileOutputStream f1 = new FileOutputStream(new File("fileUDPOut.txt"), true /* append = true */);


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

            PrintWriter printWriter = new PrintWriter(f1);
            String s;
            s = sentence;


            // nao sei como fazer aqui
            s.replaceAll("\\00", "");
            printWriter.print(s);
            printWriter.close();


            System.out.println("Mensagem recebida: " + sentence);



            sendData = "ok".getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            serverSocket.send(sendPacket);
            //serverSocket.close();
        }
    }
}
