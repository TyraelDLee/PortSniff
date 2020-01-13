package defaultPackage;

public class test {
    public static void main(String[] args) {

        int tr = 8;
        int startPort = 10;
        int endPort = 100;

        int ports_per_thread = (int)(endPort-startPort)/tr;
        int start = startPort, end = ports_per_thread+start;
        boolean run_once = true;
//        for (int i = 1; i <= tr; i++) {
//            System.out.println(start+" "+end);
//            if(i<=(endPort-startPort)%tr){
//                start+=ports_per_thread+1;
//                end+=ports_per_thread+1;
//            }else{
//                if(run_once)
//                    start++;
//                run_once = false;
//                start+=ports_per_thread;
//                end+=ports_per_thread;
//                if(end==(endPort-startPort))end--;
//            }
//            //System.out.println((i+1)+" "+(i+ports/tr+1));
//        }

        start = 1; end = 30;


        System.out.println(getCurrentProgress(1,1));
        int h,m,s;
        double duration = 3662;
        h = (int)duration/3600;
        m = ((int)duration/60)%60;
        s = (int)duration%60;
        System.out.println(h+" "+m+" "+s+" ");
    }

    private static double getCurrentProgress(int current, int total){
        return Math.round(0.19999999999999999 * 1000)/10.0;
    }
}
