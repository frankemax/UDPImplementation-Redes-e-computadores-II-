// Recebe um pacote de algum cliente
// Separa o dado, o endere�o IP e a porta deste cliente
// Imprime o dado na tela

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class UDPServer {
    private static DatagramPacket receivePacket;
    private static DatagramSocket serverSocket;
    private static ArrayList<miniDataPackage> splittedData;
    private static ArrayList<Integer> splittedDataInteger;

    private String lastACK;

    public static void main(String args[]) throws Exception {
        // cria socket do servidor com a porta 9876
        serverSocket = new DatagramSocket(9876);

        byte[] receiveData = new byte[512];
        byte[] sendData = new byte[512];
        miniDataPackage[] splittedData;
        splittedDataInteger = new ArrayList<>();


        while (true) {

            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String sentence = new String(receivePacket.getData());

            if (getIndice(sentence) == 1) {
                splittedData = new miniDataPackage[getTamanho(sentence)];
            }


            //popula(sentence);

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

        short numProx = Short.parseShort(a + "" + b);
        numProx++;

        System.out.println(numProx);
        byte[] aux2Byte;
        s = numProx + "";
        o = ("00" + numProx).substring(s.length());
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

    public static void popula(String sentence) {
        int posicao = getIndice(sentence);

        splittedData.add(posicao, new miniDataPackage(sentence.getBytes()));

        System.out.println("posicao" + posicao);


    }

    public static int getIndice(String sentence) {
        char a = sentence.charAt(0);
        char b = sentence.charAt(1);
        String index = "";
        index = index.concat(a + "").concat(b + "");

        int posicao = Integer.parseInt(index);
        return posicao;
    }

    public static int getTamanho(String sentence) {

        String totalString = "";
        for (int i = 10; i < 14; i++) {
            char ch = sentence.charAt(i);
            totalString = totalString.concat(ch+"");
        }
        int tamanho = Integer.parseInt(totalString);
        return tamanho;
    }

    public static String getData(String sentence){
        return null;
    }

    public int bytesToInt(byte[] int_bytes) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(int_bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        int my_int = ois.readInt();
        ois.close();
        return my_int;
    }
}
