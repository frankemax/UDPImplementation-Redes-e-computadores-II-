import java.io.*; // classes para input e output streams e
import java.net.*;// DatagramaSocket,InetAddress,DatagramaPacket
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
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

        File fileIn = new File("file.txt");
        File fileOut = new File("fileUDPOut.txt");

        MessageDigest md5 = MessageDigest.getInstance("MD5");

        String in = getFileChecksum(md5, fileIn);
        String out = getFileChecksum(md5, fileIn);

        if(in.equals(out)){
            System.out.println("Os arquivos tem o mesmo tamanho");
        }else System.out.println("Os arquivos tem tamanho diferente");
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

    public static String getFileChecksum(MessageDigest digest, File file) throws IOException {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        ;

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }

}
