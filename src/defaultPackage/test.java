package defaultPackage;

public class test {
    public static void main(String[] args) {
        int ports = 300;
        int tr = 10;
        System.out.println((int)ports/tr);
        System.out.println(ports%tr);
        int ports_per_thread = (int)ports/tr;
        int start = 1, end = ports_per_thread+start;
        boolean run_once = true;
        for (int i = 1; i <= tr; i++) {
            System.out.println(start+" "+end);
            if(i<=ports%tr){
                start+=ports_per_thread+1;
                end+=ports_per_thread+1;
            }else{
                if(run_once)
                    start++;
                run_once = false;
                start+=ports_per_thread;
                end+=ports_per_thread;
                if(end==ports+1)end--;
            }
            //System.out.println((i+1)+" "+(i+ports/tr+1));
        }

    }
}
