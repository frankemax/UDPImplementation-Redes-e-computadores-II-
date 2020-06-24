import java.io.*; // classes para input e output streams e
import java.net.*;// DatagramaSocket,InetAddress,DatagramaPacket
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class UDPClient {
    private static DatagramSocket clientSocket;
    private static int[] ACKArray;


    public static void main(String args[]) throws Exception {
        clientSocket = new DatagramSocket();
        readFile("file.txt");

        ACKArray = new int[DataPackage.getInstance().getTotalPackages()];
        for (int i = 0; i < ACKArray.length; i++) {
            ACKArray[i] = 0;
        }

        int dobra = 1;
        int inicia = getLastACK();


        while (true) {
            if (isFull()) {
                System.out.println("Array de ACKS cheio, encerrando programa");
                break;
            }
            if (has3ACKS() != -1) {
                dobra = 1;
                inicia = has3ACKS();
            }

            normalSendDataInit(inicia, dobra);

            clientSocket.setSoTimeout(1000);
            try {

                //System.out.println("console log");
                while (true) {
                    //.out.println("dentro do true");
                    handleACK();
                }
            } catch (Exception e) {
                System.out.println("Excecao no ack");
            }

            inicia = getLastACK();
            dobra *= 2;
        }
    }

    public static boolean isFull() {
        for (int i = 0; i < ACKArray.length; i++) {
            if (ACKArray[i] == 0) {
                return false;
            }
        }
        return true;
    }

    public static void readFile(String namefile) throws IOException {
        Path fileLocation = Paths.get(namefile);
        DataPackage.getInstance().setData(Files.readAllBytes(fileLocation));
        DataPackage.getInstance().splitData();

    }

    public static void sendData(byte[] data) throws Exception {


        // obtem endere�o IP do servidor com o DNS
        InetAddress IPAddress = InetAddress.getByName("localhost");

        // cria pacote com o dado, o endere�o do server e porta do servidor
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, 9876);
        System.out.println("Ta enviando o pacote: " + new String(data));

        //envia o pacote
        clientSocket.send(sendPacket);


    }

    public static void normalSendDataInit(int comeca, int tamanho) throws Exception {
        for (int i = comeca; i < tamanho; i++) {
            if (DataPackage.getInstance().getSplittedData().size() > i) {
                sendData(DataPackage.getInstance().getSplittedData().get(i).getData());

            }
        }
    }

    public static void handleACK() throws Exception {
        byte[] receiveData = new byte[512];

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        byte[] array = receivePacket.getData();

        for (int i = 0; i < array.length; i++){
            if(array[i] == 0){
                array = Arrays.copyOfRange(array, 0, i);
            }
        }


        int pos = Integer.parseInt(new String(array));
        System.out.println("recebendo ack: " + pos);
        ACKArray[pos - 2] = ACKArray[pos - 2] + 1;
    }

    public static int has3ACKS() {
        for (int i = 0; i < ACKArray.length; i++) {
            if (ACKArray[i] >= 3) {
                return i;
            }
        }
        return -1;
    }

    public static int getLastACK() {
        for (int i = 0; i < ACKArray.length; i++) {
            if (ACKArray[i] == 0) {
                return i;
            }
        }
        return -1;
    }

}
