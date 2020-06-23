import java.io.*; // classes para input e output streams e
import java.net.*;// DatagramaSocket,InetAddress,DatagramaPacket
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;

public class UDPClient {
    private static DatagramSocket clientSocket;


    public static void main(String args[]) throws Exception {
        readFile("file.txt");
        normalSendDataInit();

        int [] ackI = new int [DataPackage.getInstance().getTotalPackages()];

        slowStart(ackI);



    }

    public static void readFile(String namefile) throws IOException {
        Path fileLocation = Paths.get(namefile);
        DataPackage.getInstance().setData(Files.readAllBytes(fileLocation));
        DataPackage.getInstance().splitData();

    }

    public static void sendData(byte[] data) throws Exception {

        // declara socket cliente
        clientSocket = new DatagramSocket();

        // obtem endere�o IP do servidor com o DNS
        InetAddress IPAddress = InetAddress.getByName("localhost");

        // cria pacote com o dado, o endere�o do server e porta do servidor
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, 9876);

        //envia o pacote
        clientSocket.send(sendPacket);

        handleReceive();


        // fecha o cliente
        clientSocket.close();
    }

    public static void normalSendDataInit() throws Exception {
        for (int i = 0; i < DataPackage.getInstance().getSplittedData().size(); i++) {
            sendData(DataPackage.getInstance().getSplittedData().get(i).getData());

        }
    }

    public static String handleReceive() throws Exception {
        byte[] receiveData = new byte[512];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String modifiedSentence = new String(receivePacket.getData());

        System.out.println("Recebi o ACK:" + modifiedSentence);
        return modifiedSentence;

    }

    public static void handleACK() throws Exception{


    }

    public static void fastRetransmit(byte[] pacote) throws Exception{ //manda imediatamente
        sendData(pacote);
    }

    public static void slowStart( int []ackI) throws Exception{


        int i=0;
        int x=0;
        String ack="";

        while (i<DataPackage.getInstance().getSplittedData().size()){

            for(int y=0; y<ackI.length; y++){
                if(ackI[i] == 3){
                    fastRetransmit(DataPackage.getInstance().getSplittedData().get(i).getData()); //manda o pacote que errou 3x
                    ackI[i]=0;
                }
            }



            for(; x<i; x++){
                sendData(DataPackage.getInstance().getSplittedData().get(i).getData());
                ack = handleReceive();
                int ackNum = Integer.parseInt(ack);
                ackI[ackNum] = +1;
            }

            if(i==0){i++;}
            i=i*2;

        }


    }


}
