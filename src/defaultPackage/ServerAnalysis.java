package defaultPackage;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ServerAnalysis {
    private static final String[] topDomain = {".com", ".gov", ".org", ".edu", ".net", ".info"};
    private Socket socket;
    private boolean isHttps = false;
    private String address = "";
    private String host = "";
    private int port = 80;

    ServerAnalysis() {
    }

    ServerAnalysis(String address) {
        this.host = address;
        this.address = formatString(address);
    }

    ServerAnalysis(String address, boolean isHttps) {
        this.host = address;
        this.address = formatString(address);
        this.isHttps = isHttps;
    }

    void setHttps(boolean isHttps) {
        this.isHttps = isHttps;
    }

    void setAddress(String address) {
        this.host = address;
        this.address = formatString(address);
    }

    void setPort(int port){
        this.port = port;
    }

    void analysis() {
        URL analyser;
        URLConnection connection = null;
        try {
            analyser = new URL(this.address);
            connection = analyser.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, List<String>> headers = connection.getHeaderFields();
        Set<String> keys = headers.keySet();
        for(String key : keys){
            List<String> val =  headers.get(key);
            System.out.println(key+"    "+val.toString());
        }
        getIP();
    }

    private void getIP(){
        String host = this.host;
        try {
            InetAddress[] ip = InetAddress.getAllByName(host);
            for(InetAddress i : ip){
                System.out.println(i.getHostAddress().toString());
                analysisIP(i.getHostAddress());
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    void analysisSocket() {
        try{
            if(isHttps){
                socket = (SSLSocketFactory.getDefault()).createSocket(this.host, 443);
            }else{
                SocketAddress dest = new InetSocketAddress(this.host, 80);
                socket = new Socket(this.host,this.port);
                //socket.connect(dest,1000);
            }

            OutputStreamWriter streamWriter = new OutputStreamWriter(socket.getOutputStream());
            BufferedWriter bufferedWriter = new BufferedWriter(streamWriter);
            bufferedWriter.write("GET / HTTP/1.1\r\n");
            bufferedWriter.write("Host: " + this.host + "\r\n");
            bufferedWriter.write("\r\n");
            bufferedWriter.flush();

            BufferedInputStream streamReader = new BufferedInputStream(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(streamReader, "utf-8"));
            String line = null;
            boolean record = true;
            while((line = bufferedReader.readLine())!= null){
                if(!line.contains("<!--") || !line.contains("-->")){
                    if(line.contains("<Style>") || line.contains("<style>") || line.contains("<Script>") || line.contains("<script>")) record = false;
                    if(line.contains("</Style>") || line.contains("</style>") || line.contains("</Script>") || line.contains("</script>")) record = true;
                    if(record)
                        System.out.println(line);
                }
                if(line.contains("</head>"))break;
            }
            bufferedReader.close();
            bufferedWriter.close();
            socket.close();
        }catch (IOException e){}
        getIP();
    }

    void analysisIP(String ip){
        ip = ip.replace('.','#');
        ip = ip.replace(':','#');
        String[] ipSegment = ip.split("#");
        boolean ipv4 = ipSegment.length==4;
        if(ipv4){
            System.out.println("IPv4");
            int[] segment = new int[4];
            for (int i = 0; i < ipSegment.length; i++) {
                segment[i] = Integer.parseInt(ipSegment[i]);
                System.out.println(segment[i]);
            }
        }else{
            System.out.println("IPv6");
            int[] segment = new int[8];
            for (int i = 0; i < ipSegment.length; i++) {
                segment[i] = Integer.parseUnsignedInt(ipSegment[i],16);
                System.out.println(segment[i]);
            }
        }
    }


    public static void main(String[] args) {
        ServerAnalysis analyser = new ServerAnalysis("192.168.1.1",false);
//        analyser.setHttps(false);
//        analyser.setAddress("www.baidu.com");
        System.out.println(analyser.formatString("www.taobao.com"));
        analyser.analysisSocket();
    }

    private String formatString(String address) {
        String[] proxy = {"http://", "https://"};
        if (!address.contains("http://") && !address.contains("https://") && !this.isHttps)
            return proxy[0] + address;
        else if (!address.contains("http://") && !address.contains("https://") && this.isHttps)
            return proxy[1] + address;
        else
            return address;
    }
}
