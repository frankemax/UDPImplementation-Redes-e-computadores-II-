import java.util.ArrayList;

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

    public byte[] getData() {
        return data;
    }

    public ArrayList<miniDataPackage> getSplittedData() {
        return splittedData;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void splitData() {
        ArrayList<miniDataPackage> splittedData = new ArrayList<>();
        byte[] pedaco = new byte[512];
        int aux = 0;

        for (int i = 0; i < data.length; i++) {
            pedaco[aux] = this.data[i];
            aux++;
            if (i % 511 == 0 && i != 0) {
                splittedData.add(new miniDataPackage(pedaco));
                pedaco = new byte[512];
                aux = 0;
            }
        }

        this.splittedData = splittedData;
    }


}
