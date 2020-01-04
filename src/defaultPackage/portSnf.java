package defaultPackage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by leety on 2019/4/11.
 */
public class portSnf extends Thread{
    private static String target = "";
    private static String[] targets;
    private static ArrayList<Integer> openPorts = new ArrayList<>();
    private static ArrayList<Thread> MTs = new ArrayList<>();
    private int[] range;
    private final static int[] commonPort = {21,23,25,80,110,139,443,1433,1521,3389,8080};
    private final static String ints = "0123456789";
    Socket s;
    private static int prec = 0;
    

    public portSnf(int[] range){
        this.range = range;
    }

    public void run(){
        for (int i = range[0]; i < range[1]; i++) {
            int p = i/100 * 100;
            int d = i-p;

            if(d>prec) {
                prec = d;
                String bar = "process: [";
                for (int j = 0; j < 50; j++) {
                    if(j<prec/2) bar+="#";
                    else bar+=" ";
                }
                bar+="] "+prec+"%   ";
                System.out.print(bar+"\r");
            }else if(p==i){
                    prec = 100;
                    String bar = "process: [";
                    for (int j = 0; j < 50; j++)
                        bar+="#";
                    bar+="] "+prec+"%   ";
                    System.out.print(bar+"\r");
            }
            try{
                URL target_path = new URL(targets[0]);

                s = new Socket(target_path.getHost(),i);
                s.setSoTimeout(100);
                openPorts.add(i);
                s.close();
            }catch (MalformedURLException m){
                System.err.println("is not a valid URL");
            }catch (IOException ex){

            }
        }
        super.run();
    }

/**
 * Usage:
 * url [option]
 * url: target url, http or https. Default http, for https need specify
 * [option]:
 * c, common will sniff common used port include 80, 443 ect.
 * (x-y) will sniff post in range, between x and y
 * (port) will sniff specific port
 * */
    public static void main(String[] args) {
        Scanner cin = new Scanner(System.in);
        target = cin.nextLine();
        String command = "";
        if(target.contains(" ")){
            targets = target.split(" ");
            command = targets[1];
        }else {
            targets = new String[1];
            targets[0] = target;
        }

        if(!targets[0].contains("http://")&&!targets[0].contains("https://")){
            targets[0] = "http://"+targets[0];
        }else targets[0] =targets[0];

        System.out.println("Target: "+targets[0]);
        System.out.println("Starting scan...");

        if(targets.length<2){
            runAll();
        }else{
            if(command.equals("c") || command.equals("common")){
                run(commonPort);
            }else if(command.contains("-")){
                String[] cms = command.split("-");
                if(transableToInt(cms[0]) && transableToInt(cms[1])){
                    int start = Math.min(Integer.parseInt(cms[0]), Integer.parseInt(cms[1]));
                    int end = Math.max(Integer.parseInt(cms[0]), Integer.parseInt(cms[1]));
                    if(start<1) start = 1;
                    if(end > 65535) end = 65535;
                    runWithRange(start,end);
                }else runAll();
            }else if(transableToInt(command) && Integer.parseInt(command)>0 && Integer.parseInt(command)<65536) {
                runSpecificPort(Integer.parseInt(command));
            }else runAll();
        }

        System.out.println("\nPort scan on "+targets[0]);
        System.out.println(openPorts.size()==0?"No port opened":"Finished, total find: "+openPorts.size()+" ports opened");
        for(int i : openPorts){
            System.out.println("Port: "+i+" is opened");
        }
    }

    private static void runAll(){
        for (int i = 1; i < 65600; i+=100) {
            int end = i+100;
            if(end>65535) end = 65535;
            //System.out.println(end);
            Thread t = new portSnf(new int[] {i, end});
            MTs.add(t);
            t.start();
        }
        for(Thread t:MTs){
            try{
                t.join();
            }catch (InterruptedException e){

            }
        }
    }

    static Socket s1;

    /**
     * This method usually called for sniff the common used ports
     * for the ports:
     * refer to @code final static int[] commonPort
     * this method also may called for self-defined ports
     * @param ports the defined ports, @code commonPort in this package
     * */
    public static void run(int[] ports){
        int index = 1;
        for(int i : ports){
            int pre = (int)((double)(index/11.0) * 100);
            String bar = "process: [";
            if(index == ports.length){
                for (int j = 0; j < 50; j++){
                    bar+="#";
                    pre = 100;
                }
            }
            else{
                for (int j = 0; j < 50; j++) {
                    if(j<pre/2) bar+="#";
                    else bar+=" ";
                }
            }
            bar+="] "+pre+"%   ";
            System.out.print(bar+"\r");
            try{
                URL target_path = new URL(targets[0]);

                s1 = new Socket(target_path.getHost(),i);
                s1.setSoTimeout(100);
                openPorts.add(i);
                s1.close();
            }catch (MalformedURLException m){
                System.err.println("is not a valid URL");
            }catch (IOException ex){}
            index++;
        }
    }

    /**
     * This method will sniff the specific port
     *
     * @param port an int the port number need to be sniff
     * */
    private static void runSpecificPort(int port){

        try{
            URL target_path = new URL(targets[0]);
            //s1 = new Socket();
            //s1.connect(new InetSocketAddress(targets[0], port),0);
            s1 = new Socket(target_path.getHost(),port);
            //s1.setSoTimeout(10000);
            openPorts.add(port);
            s1.close();
        }catch (MalformedURLException m){
            System.err.println("is not a valid URL");
        }catch (IOException ex){}
    }

    /**
     * This method will sniff the specific ports between range
     *
     * WAR: used MULTI THREAD, handle with care
     * @param start an int that start port
     * @param end   an int the last port
     * */
    private static void runWithRange(int start, int end){
        for (int i = start; i <= end; i+=100) {
            int ending = i+100;
            if(ending>end) ending = end;
            //System.out.println(end);
            Thread t = new portSnf(new int[] {i, ending});
            MTs.add(t);
            t.start();
        }
        for(Thread t:MTs){
            try{
                t.join();
            }catch (InterruptedException e){

            }
        }
    }

    /**
     * This method is testing the input String contain the bits which
     * able to transfer to int or not.
     *
     * @param input input data, type String
     * @return a boolean value
     * */
    private static boolean transableToInt(String input){
        ArrayList<Boolean> arr = new ArrayList<>();
        for (int i = 0; i < input.length(); i++) {
            arr.add(ints.contains(input.charAt(i)+""));
        }
        if(arr.contains(false))return false;
        else return true;
    }
}
