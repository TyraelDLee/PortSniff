package defaultPackage;

import javafx.application.Platform;
import javafx.concurrent.Task;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**************************************************************************
 *                                                                        *
 *                         PortSniffer v 1.0                              *
 *                        Main class for Logic                            *
 *                                                                        *
 *                       Copyright (c) 2020 LYL                           *
 *                            @author LYL                                 *
 *                            @version 1.0                                *
 **************************************************************************/
public class PortSniff {
    private final static int[] commonPort = {21, 23, 25, 80, 110, 139, 443, 1433, 1521, 3389, 8080};

    private String address;
    private int[] ports = new int[]{};
    private int numOfThread = 1;
    private boolean runAll = false;
    private ArrayList<Observer> observers = new ArrayList<>();
    public ArrayList<SniffTask> WORKERS = new ArrayList<>();
    private int timeout = 1000;

    /**
     * The sniff worker class, extended from Task with concurrency operation
     * this class will be called by distribute worker by given the number of
     * Thread. Each Task will run the specific port range which allocate by
     * distribute worker
     * */
    public class SniffTask extends Task {
        private Socket s;
        private int startPort_SUB_THREAD = -1;
        private int endPort_SUB_THREAD = -1;
        private ArrayList<Integer> openPortOnThisThread = new ArrayList<>();
        private long threadID = 0;

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

        SniffTask(long threadID, int startPort_SUB_THREAD, int endPort_SUB_THREAD){
            if (endPort_SUB_THREAD > 65535) endPort_SUB_THREAD = 65535;
            if (startPort_SUB_THREAD < 1) startPort_SUB_THREAD = 1;
            this.startPort_SUB_THREAD = startPort_SUB_THREAD;
            this.endPort_SUB_THREAD = endPort_SUB_THREAD;
            this.threadID = threadID;
        }

        @Override
        protected Object call() throws Exception {
            int totalPort = endPort_SUB_THREAD - startPort_SUB_THREAD + 1;
            double progress = 0.0;
            if (startPort_SUB_THREAD == -1 && endPort_SUB_THREAD == -1) {
                int currentPort = 0;
                for (int port : commonPort) {
                    if(isCancelled())break;
                    sniff(port);
                    currentPort++;
                    progress = getCurrentProgress(currentPort,commonPort.length);
                    updateProgress(currentPort,commonPort.length);
                }
            } else if (startPort_SUB_THREAD == endPort_SUB_THREAD) {
                sniff(startPort_SUB_THREAD);
                progress = getCurrentProgress(1,1);
                updateProgress(1,1);
            } else {
                for (int i = startPort_SUB_THREAD; i <= endPort_SUB_THREAD; i++) {
                    if(isCancelled())break;
                    sniff(i);
                    progress = getCurrentProgress(i-startPort_SUB_THREAD+1,totalPort);
                    updateProgress(i-startPort_SUB_THREAD+1,totalPort);
                }
            }
            return null;
        }

        private double getCurrentProgress(int current, int total){
            return Math.round((double)current/total * 100)/100.0;
        }

        @Override
        protected void running() {
            updateMessage("running...");
        }

        @Override
        protected void succeeded() {
            updateMessage("ThreadFinished");
        }

        @Override
        protected void cancelled() {
            updateMessage("Cancelled!");
        }

        @Override
        protected void failed() {
            updateMessage("Failed!");
        }

        public ArrayList<Integer> getOpenPortOnThisThread(){
            return this.openPortOnThisThread;
        }

        /**
         * This method used Socket to send request to the destination.
         *
         * destination format: http/https URL or IP address and with ports
         * @param port connect with the specific port.
         * */
        void sniff(int port) {
            long startTime = System.currentTimeMillis();
            try {
                URL target_path = new URL(address);
                SocketAddress socketAddress = new InetSocketAddress(target_path.getHost(), port);
                System.out.println("@port: " + port);
                s = new Socket();
                s.connect(socketAddress, timeout);
                s.setSoTimeout(timeout);
                openPortOnThisThread.add(port);
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

    public PortSniff() {}

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
        if(numOfThread<1)numOfThread=1;
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

    public int getNumOfThread(){
        return this.numOfThread;
    }
    public void clearPorts(){
        this.ports = new int[]{};
    }

    /**
     * This method will allocate the workload to Tasks.
     * The sniff mode and number of Thread will defined
     * by users.
     * */
    public void distributeWorker() {
        WORKERS = new ArrayList<>();
        if (this.ports.length < 1 && !this.runAll) {
            SniffTask sniffTask = new SniffTask();
            WORKERS.add(sniffTask);
            new Thread(sniffTask).start();
            //use common ports.
        } else if (this.runAll) {
            distributeWorker(1, 65535);
            //use all ports.
        } else {
            if (this.ports.length == 1) {
                SniffTask sniffTask = new SniffTask(this.ports[0]);
                WORKERS.add(sniffTask);
                new Thread(sniffTask).start();
                //one defined port
            } else {
                distributeWorker(this.ports[0], this.ports[1]);
                //a defined range of ports.
            }
        }
    }

    private void distributeWorker(int startPort, int endPort) {
        int ports_per_thread = (int) (endPort - startPort) / this.numOfThread;
        int ports_mod_thread = (endPort - startPort) % this.numOfThread;
        int start = startPort, end = ports_per_thread+start;
        boolean run_once = true;
        for (int i = 1; i <= this.numOfThread; i++) {
            //System.out.println(start + " " + end);
            SniffTask sniffTask = new SniffTask(start, end);
            WORKERS.add(sniffTask);
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
                if (end == (endPort - startPort)) end--;
            }
        }
    }

    /**
     * The observer mode function
     * */
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