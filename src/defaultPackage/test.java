package defaultPackage;

public class test {
    public static void main(String[] args) {
        int ports = 65535;
        int tr = 10;
        System.out.println((int)ports/tr);
        System.out.println(ports%tr);
        for (int i = 0; i < ports; i+=ports/tr+1) {
            System.out.println((i+1)+" "+(i+ports/tr+1));
        }
    }
}
