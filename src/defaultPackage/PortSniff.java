package defaultPackage;

import defaultPackage.gui.portGUI;
import javafx.application.Platform;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PortSniff {
    private final static int[] commonPort = {21,23,25,80,110,139,443,1433,1521,3389,8080};

    private String address;
    private int[] ports = new int[]{};
    private int numOfThread = 1;
    private boolean runAll = false;
    private static ArrayList<Integer> openPorts = new ArrayList<>();
    private ArrayList<Thread> WORKERS = new ArrayList<>();
    private ArrayList<Observer> observers = new ArrayList<>();
    private int timeout = 1000;

    private class SniffThread extends Thread{
        private Socket s;
        private int startPort_SUB_THREAD;
        private int endPort_SUB_THREAD;
        private boolean runAll = false;
        SniffThread(boolean runAll){
            this.runAll = runAll;
        }

        SniffThread(int startPort_SUB_THREAD, int endPort_SUB_THREAD){
            if(endPort_SUB_THREAD>65535) endPort_SUB_THREAD = 65535;
            this.startPort_SUB_THREAD = startPort_SUB_THREAD;
            this.endPort_SUB_THREAD = endPort_SUB_THREAD;
        }

        void sniff(int port){
            long startTime = System.currentTimeMillis();
            try{
                URL target_path = new URL(address);
                SocketAddress socketAddress = new InetSocketAddress(target_path.getHost(),port);
                System.out.println("@port: "+port);
                //portGUI.setContext("@port");
                s = new Socket();
                s.connect(socketAddress,timeout);
                s.setSoTimeout(timeout);
                openPorts.add(port);
                s.close();

            }catch (MalformedURLException m){
                System.err.println("is not a valid URL");
            }catch (IOException ex){
                //System.err.println("Unknown error occur!");
            }
            Platform.runLater(()->{
                upd("@port: "+port+", communicate time: "+(System.currentTimeMillis()-startTime));
            });
        }

        public void run(){
            if(!this.runAll){//todo:check logic.
                for(int port : commonPort){
                    sniff(port);
                }
            }else{
                for (int i = startPort_SUB_THREAD; i < endPort_SUB_THREAD; i++) {
                    sniff(i);
                }
            }
            String returnResult = "Ports: ";
            for(int portsInfo : openPorts){
                returnResult+=portsInfo+" ";
            }
            returnResult += "are opened";
            final String final_returnResult = returnResult;
            Platform.runLater(()->{
                upd(final_returnResult);
            });
        }
    }

    public PortSniff(){}

    public PortSniff(String address, boolean runAll){
        address = formatString(address);
        this.runAll = runAll;
        this.address = address;
    }

    public PortSniff(String address, int port){
        address = formatString(address);
        if(port>65535)port = 65535;
        if(port<1)port = 1;
        this.address = address;
        this.ports = new int[]{port};
    }

    public PortSniff(String address, int startPort, int endPort){
        address = formatString(address);
        if(startPort>65535)startPort = 65535;
        if(startPort<1)startPort = 1;
        if(endPort>65535)endPort = 65535;
        if(endPort<1)endPort = 1;
        if(startPort > endPort){
            int temp = endPort;
            endPort = startPort;
            startPort = temp;
        }
        this.address = address;
        this.ports = new int[]{startPort,endPort};
    }

    private String formatString (String address){
        if(!address.contains("http://")&&!address.contains("https://"))
            return "http://"+address;
        else
            return address;
    }

    public void setNoOfThread(int numOfThread){
        this.numOfThread = numOfThread;
    }

    public void setRunAll(boolean runAll){
        this.runAll = runAll;
    }

    public void setURL(String address){
        address = formatString(address);
        this.address = address;
    }

    public void setPort(int port){
        this.ports = new int[]{port};
    }

    public void setPort(int startPort, int endPort){
        this.ports = new int[]{startPort,endPort};
    }

    public void setTimeout(int timeout){
        if(timeout < 500) this.timeout = 500;
        else this.timeout = timeout;
        //unit ms, lower bound 500ms. default 1s.
    }

    public void distributeWorker(){
        if(this.ports.length < 1 && !this.runAll){
            SniffThread st = new SniffThread(this.runAll);
            st.start();
            //use common port
        }else if(this.ports.length < 1 && this.runAll){
            int maxWorkLoadOnThread = (int)(65535/this.numOfThread);
            for (int i = 0; i < 65535; i+=maxWorkLoadOnThread+1) {
                //System.out.println((i+1)+" "+(i+maxWorkLoadOnThread+1));
                Thread sniffWorker = new SniffThread((i+1),(i+maxWorkLoadOnThread+1));
                sniffWorker.start();
                try{
                    ExecutorService office = Executors.newFixedThreadPool(this.numOfThread);
                    sniffWorker.join();
                }catch (InterruptedException e){}
            }
            //use all port.
        }else{
            int maxWorkLoadOnThread = (int)((this.ports[1]-this.ports[0]+1)/this.numOfThread);
            int mod = (this.ports[1]-this.ports[0]+1)%this.numOfThread;
            //Thread Testworker = new SniffThread();
        }
    }

    public void reg(Observer o){
        observers.add(o);
    }

    public void upd(String showText){
        for(Observer o : observers) o.update(showText);
    }

//    public static void main(String[] args) {
//        PortSniff ps = new PortSniff("https://www.baidu.com", true);
//        //ps.setURL("www.baidu.com");
//        ps.setTimeout(0);
//        ps.setNoOfThread(30);
//        System.out.println(ps.address);
//        ps.distributeWorker();
//        for(int info : openPorts){
//            System.out.println("Port "+info+" open");
//        }
//    }

}
//todo: take care on all ADts restore. Especially for observer ADT and ports.