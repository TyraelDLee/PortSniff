package defaultPackage.ServiceSniff;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class read data from os file. Then abstract to objects.
 * prob abnd.
 * */
public class Fingerprint {
    Fingerprint(){

    }

    Fingerprint(String device, String fingerprint, String Class, String CPE, String SEQ, String OPS, String WIN, String ECN, String T1, String T2,
                String T3, String T4, String T5, String T6, String T7, String U1, String IE){

    }

    private String read(){
        String figenprint = null;
        try{
            InputStream in = this.getClass().getResourceAsStream("asset/os");
            byte[] filecontent = new byte[in.available()];
            figenprint = new String(filecontent,0, in.read(filecontent));
            in.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println(figenprint);
        return figenprint;
    }

    public Fingerprint[] getFingerprints(){

        String[] fingerPrint = this.read().split("\n\n");
        Fingerprint[] fingerprints = new Fingerprint[fingerPrint.length];
        System.out.println(fingerPrint.length);
        return fingerprints;
    }
    public static void main(String[] args) {
        Fingerprint fingerprint = new Fingerprint();
        fingerprint.getFingerprints();
    }
}
