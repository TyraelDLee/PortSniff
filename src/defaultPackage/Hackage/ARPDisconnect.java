package defaultPackage.Hackage;

public class ARPDisconnect {
    private byte[] string2mac (String in){
        byte[] MAC = new byte[]{(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00};
        String[] inputs = in.split("-");
        for (int i = 0; i < inputs.length; i++) {
            MAC[i] = (byte) ((Integer.parseInt(inputs[i],16))&0xff);
        }
        return MAC;
    }

    private void hack(){

    }
}
