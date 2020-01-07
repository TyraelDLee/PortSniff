package defaultPackage;

import defaultPackage.gui.portGUI;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class PortSniff {
    private final static int[] commonPort = {21, 23, 25, 80, 110, 139, 443, 1433, 1521, 3389, 8080};

    private String address;
    private int[] ports = new int[]{};
    private int numOfThread = 1;
    private boolean runAll = false;
    private static ArrayList<Integer> openPorts = new ArrayList<>();
    private ArrayList<Observer> observers = new ArrayList<>();
    private int timeout = 1000;

//    private class SniffThread extends Thread{
//        private Socket s;
//        private int startPort_SUB_THREAD = -1;
//        private int endPort_SUB_THREAD = -1;
//        private boolean runAll = false;
//        SniffThread(boolean runAll){
//            this.runAll = runAll;
//        }
//
//        SniffThread(int port){
//            this.startPort_SUB_THREAD = port;
//            this.endPort_SUB_THREAD = port;
//        }
//
//        SniffThread(int startPort_SUB_THREAD, int endPort_SUB_THREAD){
//            if(endPort_SUB_THREAD>65535) endPort_SUB_THREAD = 65535;
//            if(startPort_SUB_THREAD < 1) startPort_SUB_THREAD = 1;
//            this.startPort_SUB_THREAD = startPort_SUB_THREAD;
//            this.endPort_SUB_THREAD = endPort_SUB_THREAD;
//        }
//
//        void sniff(int port){
//            long startTime = System.currentTimeMillis();
//            try{
//                URL target_path = new URL(address);
//                SocketAddress socketAddress = new InetSocketAddress(target_path.getHost(),port);
//                System.out.println("@port: "+port);
//                System.out.println(observers.size());
//                //portGUI.setContext("@port");
//                s = new Socket();
//                s.connect(socketAddress,timeout);
//                s.setSoTimeout(timeout);
//                openPorts.add(port);
//                s.close();
//
//            }catch (MalformedURLException m){
//                System.err.println("is not a valid URL");
//            }catch (IOException ex){
//                //System.err.println("Unknown error occur!");
//            }
//            Platform.runLater(()->{
//                upd("@port: "+port+", communicate time: "+(System.currentTimeMillis()-startTime));
//            });
//        }
//
//        public void run(){
//            if(startPort_SUB_THREAD == -1 && endPort_SUB_THREAD == -1){//todo:check logic.
//                for(int port : commonPort){
//                    sniff(port);
//                }
//            }else if(startPort_SUB_THREAD == endPort_SUB_THREAD){
//               sniff(startPort_SUB_THREAD);
//            }else{
//                for (int i = startPort_SUB_THREAD; i <= endPort_SUB_THREAD; i++) {
//                    sniff(i);
//                }
//            }
//            String returnResult = "Ports: ";
//            for(int portsInfo : openPorts){
//                returnResult+=portsInfo+" ";
//            }
//            returnResult += "are opened";
//            final String final_returnResult = returnResult;
//            Platform.runLater(()->{
//                upd(final_returnResult);
//            });
//        }
//    }

    private class SniffTask extends Task {
        private Socket s;
        private int startPort_SUB_THREAD = -1;
        private int endPort_SUB_THREAD = -1;

        SniffTask() {}

        SniffTask(int port) {
            this.startPort_SUB_THREAD = port;
            this.endPort_SUB_THREAD = port;
        }

        SniffTask(int startPort_SUB_THREAD, int endPort_SUB_THREAD) {
            if (endPort_SUB_THREAD > 65535) endPort_SUB_THREAD = 65535;
            if (startPort_SUB_THREAD < 1) startPort_SUB_THREAD = 1;
            this.startPort_SUB_THREAD = startPort_SUB_THREAD;
            this.endPort_SUB_THREAD = endPort_SUB_THREAD;
        }

        @Override
        protected Object call() throws Exception {
            if (startPort_SUB_THREAD == -1 && endPort_SUB_THREAD == -1) {
                for (int port : commonPort) {
                    sniff(port);
                }
            } else if (startPort_SUB_THREAD == endPort_SUB_THREAD) {
                sniff(startPort_SUB_THREAD);
            } else {
                for (int i = startPort_SUB_THREAD; i <= endPort_SUB_THREAD; i++) {
                    sniff(i);
                }
            }

            return null;
        }

        @Override
        protected void running() {
            updateMessage("running...");
        }

        @Override
        protected void succeeded() {
            updateMessage("Done!");
        }

        @Override
        protected void cancelled() {
            updateMessage("Cancelled!");
        }

        @Override
        protected void failed() {
            updateMessage("Failed!");
        }

        void sniff(int port) {
            long startTime = System.currentTimeMillis();
            try {
                URL target_path = new URL(address);
                SocketAddress socketAddress = new InetSocketAddress(target_path.getHost(), port);
                System.out.println("@port: " + port);
                System.out.println(observers.size());
                //portGUI.setContext("@port");
                s = new Socket();
                s.connect(socketAddress, timeout);
                s.setSoTimeout(timeout);
                openPorts.add(port);
                s.close();

            } catch (MalformedURLException m) {
                System.err.println("is not a valid URL");
            } catch (IOException ex) {
                //System.err.println("Unknown error occur!");
            }
            Platform.runLater(() -> {
                upd("@port: " + port + ", communicate time: " + (System.currentTimeMillis() - startTime));
            });
        }

    }

    public PortSniff() {
    }

    public PortSniff(String address, boolean runAll) {
        address = formatString(address);
        this.runAll = runAll;
        this.address = address;
    }

    public PortSniff(String address, int port) {
        address = formatString(address);
        if (port > 65535) port = 65535;
        if (port < 1) port = 1;
        this.address = address;
        this.ports = new int[]{port};
    }

    public PortSniff(String address, int startPort, int endPort) {
        address = formatString(address);
        if (startPort > 65535) startPort = 65535;
        if (startPort < 1) startPort = 1;
        if (endPort > 65535) endPort = 65535;
        if (endPort < 1) endPort = 1;
        if (startPort > endPort) {
            int temp = endPort;
            endPort = startPort;
            startPort = temp;
        }
        this.address = address;
        this.ports = new int[]{startPort, endPort};
    }

    private String formatString(String address) {
        if (!address.contains("http://") && !address.contains("https://"))
            return "http://" + address;
        else
            return address;
    }

    public void setNoOfThread(int numOfThread) {
        this.numOfThread = numOfThread;
    }

    public void setRunAll(boolean runAll) {
        this.runAll = runAll;
    }

    public void setURL(String address) {
        this.address = formatString(address);
    }

    public void setPort(int port) {
        this.ports = new int[]{port};
    }

    public void setPort(int startPort, int endPort) {
        if (startPort > endPort) {
            int temp = endPort;
            endPort = startPort;
            startPort = temp;
        }
        this.ports = new int[]{startPort, endPort};
    }

    public void setTimeout(int timeout) {
        if (timeout < 500) this.timeout = 500;
        else this.timeout = timeout;
        //unit ms, lower bound 500ms. default 1s.
    }

    public void distributeWorker() {
        if (this.ports.length < 1 && !this.runAll) {
            SniffTask sniffTask = new SniffTask();
            new Thread(sniffTask).start();
            //use common ports.
        } else if (this.ports.length < 1 && this.runAll) {
            distributeWorker(1, 65535);
            //use all ports.
        } else {
            if (this.ports.length == 1) {
                SniffTask sniffTask = new SniffTask(this.ports[0]);
                new Thread(sniffTask).start();
                //one defined port
            } else {
                distributeWorker(this.ports[0], this.ports[1]);
                //a defined range of ports.
            }
        }
    }

    private void distributeWorker(int startPort, int endPort) {
        int ports_per_thread = (int) endPort / this.numOfThread;
        int ports_mod_thread = endPort % this.numOfThread;
        int start = startPort, end = ports_per_thread + start;
        boolean run_once = true;
        for (int i = 1; i <= this.numOfThread; i++) {
            System.out.println(start + " " + end);
            SniffTask sniffTask = new SniffTask(start, end);
            new Thread(sniffTask).start();
            if (i <= ports_mod_thread) {
                start += ports_per_thread + 1;
                end += ports_per_thread + 1;
            } else {
                if (run_once)
                    start++;
                run_once = false;
                start += ports_per_thread;
                end += ports_per_thread;
                if (end == endPort + 1) end--;
            }
        }
    }


    public void reg(Observer o) {
        observers.add(o);
    }

    public void upd(String showText) {
        for (Observer o : observers) o.update(showText);
    }

//    public static void main(String[] args) {
//        PortSniff ps = new PortSniff("https://www.baidu.com", true);
//        //ps.setURL("www.baidu.com");
//        ps.setTimeout(0);
//        ps.setNoOfThread(30);
//        System.out.println(ps.address);
//        ps.distributeWorker();
//    }

}
//todo: return the result.