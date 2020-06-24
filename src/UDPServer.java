// Recebe um pacote de algum cliente
// Separa o dado, o endere�o IP e a porta deste cliente
// Imprime o dado na tela

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.CRC32;

public class UDPServer {
    private static DatagramPacket receivePacket;
    private static DatagramSocket serverSocket;
    private static miniDataPackage[] splittedData;
    private static ArrayList<Integer> splittedDataInteger;
    private static int lastACK;

    public static void main(String args[]) throws Exception {
        serverSocket = new DatagramSocket(9800);

        byte[] receiveData = new byte[512];
        lastACK = 1;

        while (true) {

            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String sentence = new String(receivePacket.getData());
            System.out.println("recebi : " + sentence);

            if (getIndice(sentence) == 1) {
                byte[] aux = Arrays.copyOfRange(receivePacket.getData(), 10, 14);
                splittedData = new miniDataPackage[Integer.parseInt(new String(aux))];

            }


            //System.out.println("chegou o pacote: " + getIndice(sentence));
            popula(sentence,receivePacket);

            if (lastACK == getIndice(sentence)){
                refreshLastACK();
            }




            sendACK();


            if (lastACK == splittedData.length+1){
                break;
            }

        }


        closeConnection();
        escreveArquivo();
    }

    public static void refreshLastACK(){
        boolean last = false;
        for (int i = 0; i < splittedData.length; i++) {
            if(splittedData[i]==null){
                lastACK=i+1;
                break;
            }
            if(i == splittedData.length-1){
                lastACK= splittedData.length+1;
            }
        }
    }

    public static void closeConnection() throws Exception {
        InetAddress IPAddress = receivePacket.getAddress();
        int port = receivePacket.getPort();
        String s = "00";
        byte[] ACKByte = s.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(ACKByte, ACKByte.length, IPAddress, port);
        System.out.println("mandei o ack: " + s);
        serverSocket.send(sendPacket);
        serverSocket.send(sendPacket);
        serverSocket.send(sendPacket);
        serverSocket.send(sendPacket);
        serverSocket.send(sendPacket);
        serverSocket.send(sendPacket);
        serverSocket.send(sendPacket);
        serverSocket.send(sendPacket);
        serverSocket.send(sendPacket);
        serverSocket.send(sendPacket);

    }

    public static void sendACK() throws Exception{
        InetAddress IPAddress = receivePacket.getAddress();
        int port = receivePacket.getPort();
        String s = lastACK+"";
        byte[] ACKByte = s.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(ACKByte, ACKByte.length, IPAddress, port);
        System.out.println("mandei o ack: " + s);
        serverSocket.send(sendPacket);

    }

    public static void escreveArquivo() throws Exception {
        FileOutputStream f1 = new FileOutputStream(new File("fileUDPOut.txt"), false /* append = true */);
        PrintWriter printWriter = new PrintWriter(f1);
        String s="";
        String a="";

        for (int i = 0; i < splittedData.length; i++) {
            a= new String(splittedData[i].getData());
            System.out.println("escrevi: " + a);
            s=s+ new String(splittedData[i].getData());

        }

        printWriter.print(s);
        printWriter.close();

    }

    public static void popula(String sentence,DatagramPacket dp) {
        int posicao = getIndice(sentence);

        if(checkCRC(dp)){
            splittedData[posicao-1]= new miniDataPackage(getData(dp.getData()));
            System.out.println("CheckCRC true, adicionando pacote no array");
            System.out.println("posicao " + posicao);

        }



    }

    public static boolean checkCRC(DatagramPacket receivePacket){
        byte[] packet = receivePacket.getData();
        byte[] packetOld = Arrays.copyOfRange(packet, 2, 10);

        for (int i = 2; i < 10; i++) {
            packet[i] =(byte)48;
        }


        CRC32 crc32 = new CRC32();
        crc32.update(packet,0,packet.length);
        long old =crc32.getValue();


        long old2 = bytesToLong(packetOld,0);

        if (old == old2) {
            return true;
        }
        return false;
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

    public static byte[] getData(byte[] sentence){
        byte[] array = Arrays.copyOfRange(sentence,14,sentence.length);

        return array;
    }

    public static int bytesToInt(byte[] int_bytes) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(int_bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        int my_int = ois.readInt();
        ois.close();
        return my_int;
    }

    public static long bytesToLong(final byte[] bytes, final int offset) {
        long result = 0;
        for (int i = offset; i < Long.BYTES + offset; i++) {
            result <<= Long.BYTES;
            result |= (bytes[i] & 0xFF);
        }
        return result;
    }

    public static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }
}
