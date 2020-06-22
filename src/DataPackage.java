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
        short aux2= 1;
        byte[] aux2Byte;
        String s = "";
        String a = "";
        int ultimo = 0;
        int result = data.length +((int)Math.ceil(data.length/512.0)*10);
        for (int i = 0; i < result ; i++) {
            if(aux==0 || aux==1){
                a= aux2+"";
                s= ("00"+aux2).substring(a.length());
                aux2Byte = s.getBytes();
                pedaco[aux] = aux2Byte[aux];

                //aux2++;
                aux++;

            }
            else if(aux > 1 && aux <10){
                pedaco[aux] =(byte)48;
                aux++;
            }
            else {
                pedaco[aux] = this.data[ultimo];
                ultimo++;

                if (aux==511) {
                    splittedData.add(new miniDataPackage(pedaco));
                    pedaco = new byte[512];
                    aux = 0;
                    aux2 ++;
                }
                else{
                    aux++;
                }

            }

        }

        splittedData.add(new miniDataPackage(pedaco));




        this.splittedData = splittedData;
    }




}
