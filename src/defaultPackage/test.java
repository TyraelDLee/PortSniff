package defaultPackage;

public class test {
    public static void main(String[] args) {

        int tr = 8;
        int startPort = 10;
        int endPort = 100;

        int ports_per_thread = (int)(endPort-startPort)/tr;
        int start = startPort, end = ports_per_thread+start;
        boolean run_once = true;
        for (int i = 1; i <= tr; i++) {
            System.out.println(start+" "+end);
            if(i<=(endPort-startPort)%tr){
                start+=ports_per_thread+1;
                end+=ports_per_thread+1;
            }else{
                if(run_once)
                    start++;
                run_once = false;
                start+=ports_per_thread;
                end+=ports_per_thread;
                if(end==(endPort-startPort))end--;
            }
            //System.out.println((i+1)+" "+(i+ports/tr+1));
        }

    }
}
