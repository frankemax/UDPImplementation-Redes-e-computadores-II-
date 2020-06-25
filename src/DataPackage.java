import java.util.ArrayList;
import java.util.zip.CRC32;

public class DataPackage {
    private static DataPackage instance;
    private byte[] data;
    private ArrayList<miniDataPackage> splittedData;


    private DataPackage() {

    }

    public static DataPackage getInstance() {
        if (instance == null) {
            instance = new DataPackage();
        }
        return instance;
    }

    public ArrayList<miniDataPackage> getSplittedData() {
        return splittedData;
    }

    public void setData(byte[] data) {
        this.data = data;
    }


    //retorna o numero total de pacotes que serão enviados
    public int getTotalPackages() {
        int totalPackages = (int) Math.ceil((data.length) / (498.0));

        return totalPackages;
    }


    /*divide o file em pacotes de 512, sendo:
    0-1 Bytes: indice do pacote
    2-9 Bytes: guarda o CRC do pacote, que é um long (é calculado depois do pacote estar pronto)
    10-14 Bytes: guarda o número total de packages.
    15-512 Bytes: data
     */
    public void splitData() {
        ArrayList<miniDataPackage> splittedData = new ArrayList<>();
        byte[] pedaco = new byte[512];
        int aux = 0;
        short aux2 = 1;
        byte[] aux2Byte;
        int ultimo = 0;

        byte[] totalPackages = (String.format("%04d", getTotalPackages())).getBytes();
        int result = data.length + 14 * getTotalPackages();

        for (int i = 0; i < result; i++) {

            //adiciona o indice do pacote
            if (aux == 0 || aux == 1) {
                aux2Byte = shortToBytes(aux2);
                pedaco[aux] = aux2Byte[aux];
                aux++;
            }

            //reserva o espaco para o crc
            else if (aux > 1 && aux < 10) {
                pedaco[aux] = (byte) 48;
                aux++;
            }

            // adiciona o numero de pacotes no package
            else if (aux > 9 && aux < 14) {
                pedaco[aux] = totalPackages[aux - 10];
                aux++;
            } else {
                pedaco[aux] = this.data[ultimo];
                ultimo++;

                if (aux == 511) {

                    // adiciona o crc no package
                    CRC32 crc32 = new CRC32();
                    crc32.update(pedaco, 0, pedaco.length);
                    long crc32Long = crc32.getValue();
                    byte[] longByte;
                    longByte = longToBytes(crc32Long);
                    for (int j = 2; j < 10; j++) {
                        pedaco[j] = longByte[j - 2];
                    }

                    System.out.println("Criei o pacote: " + new String(pedaco));
                    splittedData.add(new miniDataPackage(pedaco));
                    pedaco = new byte[512];
                    aux = 0;
                    aux2++;
                } else {
                    aux++;
                }
            }
        }

        // adiciona o crc no package
        CRC32 crc32 = new CRC32();
        crc32.update(pedaco, 0, pedaco.length);
        long crc32Long = crc32.getValue();
        byte[] longByte;
        longByte = longToBytes(crc32Long);
        for (int i = 2; i < 10; i++) {
            pedaco[i] = longByte[i - 2];
        }

        // adiciona o numero de pacotes no package
        for (int i = 10; i < 14; i++) {
            pedaco[i] = totalPackages[i - 10];
        }

        splittedData.add(new miniDataPackage(pedaco));
        System.out.println("pedaco: " + new String(pedaco));

        this.splittedData = splittedData;
    }

    public static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (l & 0xFF);
            l >>= 8;
        }
        return result;
    }

    public byte[] shortToBytes(short value) {
        byte[] returnByteArray = new byte[2];
        returnByteArray[0] = (byte) (value & 0xff);
        returnByteArray[1] = (byte) ((value >>> 8) & 0xff);
        return returnByteArray;
    }

}
