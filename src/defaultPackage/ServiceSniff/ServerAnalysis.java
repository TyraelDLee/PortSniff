package defaultPackage.ServiceSniff;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * This analysis by using whois and target http header if applicable.
 *
 */
public class ServerAnalysis {
    private Socket socket;
    private boolean isHttps = false;
    private String address = "";
    private String host = "";
    private int port = 80;
    private String server = "unknown";
    private String title = "";
    private String primaryLang = "unknown";
    private String hostingOrg = "unrecognized";
    private String hostingLoc = "unrecognized";
    private String Connection = "";
    private String ContentType = "";
    private String topDomain = "";
    private String topDomLoc = "";
    private String docType = "";
    private HashSet<String> techEnable = new HashSet<>();
    private HashMap<String, ArrayList<String>> whoisINFO = new HashMap();


    public ServerAnalysis() {}

    public ServerAnalysis(String address) {
        this.host = address;
        this.address = formatString(address);
    }

    public ServerAnalysis(String address, boolean isHttps) {
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
        //getIP();
    }

    void analysisSocket() {
        String line = null;
        String content = "";
        try{
            if(isHttps){
                socket = (SSLSocketFactory.getDefault()).createSocket(this.host, 443);
            }else{
                socket = new Socket(this.host,this.port);
            }

            OutputStreamWriter streamWriter = new OutputStreamWriter(socket.getOutputStream());
            BufferedWriter bufferedWriter = new BufferedWriter(streamWriter);
            bufferedWriter.write("GET / HTTP/1.1\r\n");
            bufferedWriter.write("Host: " + this.host + "\r\n");
            bufferedWriter.write("\r\n");
            bufferedWriter.flush();

            BufferedInputStream streamReader = new BufferedInputStream(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(streamReader, StandardCharsets.UTF_8));
            boolean record = true;
            boolean header  = true;
            while((line = bufferedReader.readLine())!= null){
                if(!line.contains("<!--") || !line.contains("-->")){
                    if(line.contains("<Style>") || line.contains("<style>") || line.contains("<Script>") || line.contains("<script>")) record = false;
                    if(line.contains("</Style>") || line.contains("</style>") || line.contains("</Script>") || line.contains("</script>")) record = true;
                    if(record){
                        content+=line;
                        System.out.println(line);
                        if(line.equals("<html>") || line.equals("<HTML>")) header = false;
                        if(header){
                            //-- header --//
                            if(line.contains("Server")) this.server = line;
                            if(line.contains("Content-Type")) this.ContentType = line;
                            if(line.contains("Connection")) this.Connection = line;
                            if(line.contains("HTTP-EQUIV=\"Content-Language\"")) this.primaryLang = "LANG:"+line.substring(line.indexOf("Content=")+8).replace(">","").replace("\"","");
                            //-- header --//
                        }else{
                            //-- body --//
                            if(line.contains("LANG")) this.primaryLang = "LANG:"+line.substring(line.indexOf("LANG")+4).replace(">","").replace("\"","");
                            if(line.contains("<title>") && line.contains("</title>"))this.title = "title:"+line.replace("<title>","").replace("</title>","");
                            if(line.contains("<script>")) techEnable.add("JavaScript");
                            if(line.contains(".css") || line.contains("rel=\"stylesheet\"")) techEnable.add("CSS");
                            //-- body --//
                        }
                        //-- doc type --//
                        if(line.equals("<!DOCTYPE html>") || line.equals("<!DOCTYPE HTML>"))this.docType = "HTML 5";
                        if(line.contains("!DOCTYPE") && line.contains("html4") || line.contains("!DOCTYPE") && line.contains("HTML4")) this.docType = "HTML 4";
                        if(line.contains("!DOCTYPE") && line.contains("xhtml1") || line.contains("!DOCTYPE") && line.contains("XHTML1")) this.docType = "XHTML 1";
                        if(line.contains("!DOCTYPE") && line.contains("xhtml11") || line.contains("!DOCTYPE") && line.contains("XHTML11")) this.docType = "XHTML 1.1";
                        //-- doc type --//
                    }
                }
                if(line.contains("</head>"))break;
            }
            bufferedReader.close();
            bufferedWriter.close();
            socket.close();
        }catch (IOException e){
            if(this.isHttps){
                content = "https_not_support";
                System.out.println("https_not_support");
            }else
                System.out.println("unknown_err");
        }

        if(!isIP(this.host)){
            WhoisUtil whoisUtil = new WhoisUtil();
            this.whoisINFO = whoisUtil.get(this.host.replace("www.",""), false);
        }else
            this.whoisINFO.put("IP address", new ArrayList<String>());
        //System.out.println(this.whoisINFO);
        getIP();

    }

    private void getIP(){
        String host = this.host;
        ArrayList<String> IPStack = new ArrayList<>();
        try {
            InetAddress[] ip = InetAddress.getAllByName(host);
            for(InetAddress i : ip){
                System.out.println(i.getHostAddress().toString());
                IPStack.add(i.getHostAddress().toString());
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if(IPStack.size()>0){
            for(String ip : IPStack){
                //analysisIP(ip);
            }
        }
    }

    void analysisIP(String ip){
        ip = ip.replace('.','#');
        ip = ip.replace(':','#');
        String[] ipSegment = ip.split("#");
        boolean ipv4 = ipSegment.length==4;
        if(ipv4){
            IPLocation ipl = new IPLocation();
            String[][] ipTrace = new String[5][2];
            ipTrace[0][0] = "0.0.0.0";
            ipTrace[0][1] = "255.255.255.255";
            System.out.println("IPv4");
            int[] segment = new int[4];
            for (int i = 0; i < ipSegment.length; i++)
                segment[i] = Integer.parseInt(ipSegment[i]);
            for (int i = 0; i < 4; i++) {
                ipTrace[i+1][0] = segment[0]+"."+(i<1?0:segment[1])+"."+(i<2?0:segment[2])+"."+(i<3?0:segment[3]);
                ipTrace[i+1][1] = segment[0]+"."+(i<1?255:segment[1])+"."+(i<2?255:segment[2])+"."+(i<3?255:segment[3]);
            }
            for(String[] iprange : ipTrace){
                System.out.println(iprange[0]+" - "+iprange[1]);
                ipl.setIP(iprange[0]);
                ipl.lookup();
                System.out.println(ipl.getReport());
            }
        }else{
            //Currently not support for ipv6.
            System.out.println("IPv6");
//            int[] segment = new int[8];
//            for (int i = 0; i < ipSegment.length; i++) {
//                segment[i] = Integer.parseUnsignedInt(ipSegment[i],16);
//                System.out.println(segment[i]);
//            }
        }
    }

    void analysisDomain(){
        String domain = this.host.replace(".","@");
        String[] domainSegment = domain.split("@");
        String domains = "";
        String domainl = "";
        String location = "";
        String topDomain = "--";
        try{
            InputStream in = this.getClass().getResourceAsStream("asset/TopDomin.dlist");
            byte[] filecontent = new byte[in.available()];
            domains = new String(filecontent,0, in.read(filecontent));
            in.close();
            in = this.getClass().getResourceAsStream("asset/TopDominLoc.dlist");
            filecontent = new byte[in.available()];
            domainl = new String(filecontent,0, in.read(filecontent));
            in.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        String[] tds = domains.split("\n");
        String[] tdl = domainl.split("\n");
        for(String s : domainSegment){
            if(s.length()==2){
                location = "--";
                for(String loc : tdl){
                    if(s.equals(loc.split(" : ")[0])){
                        location = loc.split(" : ")[1];
                        break;
                    }
                }
            }else{
                for(String top : tds){
                    if(s.equals(top.split(" : ")[0])){
                        topDomain = top.split(" : ")[1];
                        break;
                    }
                }
            }
        }
        System.out.println(topDomain+" "+location);
    }

    boolean getHTTPS(){
        return this.isHttps;
    }

    public static void main(String[] args) {
        ServerAnalysis analyser = new ServerAnalysis("www.baidu.com",true);
//        analyser.setHttps(false);
//        analyser.setAddress("www.baidu.com");
//        System.out.println(analyser.formatString("www.taobao.com"));
        analyser.analysisSocket();
        //analyser.analysisDomain();
        System.out.println(analyser.isIP("192.168.1.1"));
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

    private boolean isIP(String context){
        if(context.contains(".")){
            return check(context,3);
        }else if(context.contains(":")){
            return check(context,7);
        }else if(context.equals("fe80::1")) return true;
        else return false;
    }

    private boolean check(String context, int dots){
        String cms = "1234567890abcdef";
        int dot = 0;
        boolean allNum = true;
        for (int i = 0; i < context.length(); i++) {
            if (context.charAt(i) == (dots==3?'.':':')) dot++;
            else if (!cms.contains(context.charAt(i)+"")){
                allNum = false;
                break;
            }
        }
        return allNum && dot==dots;
    }
}
