
// L� uma linha do teclado
// Envia o pacote (linha digitada) ao servidor

import java.io.*; // classes para input e output streams e
import java.net.*;// DatagramaSocket,InetAddress,DatagramaPacket
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UDPClient {

    public static void main(String args[]) throws Exception {
        readFile("file.txt");


        for (int i=0;i< DataPackage.getInstance().getSplittedData().size();i++){
            sendData(DataPackage.getInstance().getSplittedData().get(i).getData());

        }

    }

    public static void readFile(String namefile) throws IOException {
        Path fileLocation = Paths.get(namefile);
        DataPackage.getInstance().setData(Files.readAllBytes(fileLocation));
        DataPackage.getInstance().splitData();

    }

    public static void sendData(byte[] data) throws Exception {
        // declara socket cliente
        DatagramSocket clientSocket = new DatagramSocket();

        // obtem endere�o IP do servidor com o DNS
        InetAddress IPAddress = InetAddress.getByName("localhost");

        // cria pacote com o dado, o endere�o do server e porta do servidor
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, 9876);

        //envia o pacote
        clientSocket.send(sendPacket);

        // fecha o cliente
        clientSocket.close();
    }

}
