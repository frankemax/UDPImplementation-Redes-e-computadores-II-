// Recebe um pacote de algum cliente
// Separa o dado, o endere�o IP e a porta deste cliente
// Imprime o dado na tela

import java.io.*;
import java.net.*;

public class UDPServer {
    private static DatagramPacket receivePacket;
    private static DatagramSocket serverSocket;
    private String lastACK;

    public static void main(String args[]) throws Exception {
        // cria socket do servidor com a porta 9876
        serverSocket = new DatagramSocket(9876);

        byte[] receiveData = new byte[512];
        byte[] sendData = new byte[512];



        while (true) {
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String sentence = new String(receivePacket.getData());
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();



            escreveArquivo(sentence);
            sendACK(sentence);


        }
    }

    public static void sendACK(String sentence) throws Exception {
        char a = sentence.charAt(0);
        char b = sentence.charAt(1);
        String s;
        String o;
        InetAddress IPAddress = receivePacket.getAddress();
        int port = receivePacket.getPort();

        short numProx = Short.parseShort(a+""+b);
        numProx++;

        System.out.println(numProx);
        byte[] aux2Byte;
        s= numProx + "";
        o= ("00"+numProx).substring(s.length());
        aux2Byte = o.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(aux2Byte, aux2Byte.length, IPAddress, port);
        serverSocket.send(sendPacket);

    }

    public static void escreveArquivo(String sentence) throws Exception {
        FileOutputStream f1 = new FileOutputStream(new File("fileUDPOut.txt"), true /* append = true */);
        PrintWriter printWriter = new PrintWriter(f1);
        String s;
        s = sentence;
        s.replaceAll("\\00", "");
        printWriter.print(s);
        printWriter.close();

        System.out.println("Mensagem escrita no file: " + sentence);
    }
}
