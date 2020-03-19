package defaultPackage.ServiceSniff;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class WhoisUtil {
    private static final int DEFAULT_PORT = 43;


    /**
     * Get top domain
     * */
    public static String getTail(String domain) {
        int index;
        String tail = "";
        if (domain != null && !"".equals(domain) && (index = domain.lastIndexOf('.') + 1) != 0) tail = domain.substring(index);
        return tail;
    }

    public String queryDoamin(String domain,String server) {
        String context = "";
        Socket socket;
        try {
            socket = new Socket(server, DEFAULT_PORT);

            String lineSeparator = "\n";

            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println(domain);
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            int white = 0;
            while ((line = in.readLine()) != null) {
                if(line.equals("")) white++;
                if(white>=2) break;
                context+=line+lineSeparator;
            }
            socket.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return context;
    }

    private String queryDomainApi(String domain) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        String content = "";
        String line = null;
        URL target = new URL("https://api.devopsclub.cn/api/whoisquery?type=text&domain="+domain);
        HttpsURLConnection con = (HttpsURLConnection) target.openConnection();
        X509TrustManager xtm = new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                    throws CertificateException {
                // TODO Auto-generated method stub

            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                    throws CertificateException {
                // TODO Auto-generated method stub

            }
        };

        TrustManager[] tm = {xtm};

        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, tm, null);

        con.setSSLSocketFactory(ctx.getSocketFactory());
        con.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        });
        InputStream is = con.getInputStream();
        InputStreamReader isr = new InputStreamReader(is,"utf-8");
        BufferedReader br = new BufferedReader(isr);
        String data = br.readLine();
        while (data!=null){
            //System.out.println(data);
            content+=data;
            data = br.readLine();
        }
        br.close();
        isr.close();
        is.close();
        content = content.replace("\\r\\n","@return@");
        String[] info = content.split("@return@");
        for(String in : info){
            System.out.println(in);
        }
        return content;
    }

    private String getServer(String tail){
        String domains = "";
        String linesep = "\r";
        String server = "";
        try{
            InputStream in = this.getClass().getResourceAsStream("asset/whois.dlist");
            byte[] filecontent = new byte[in.available()];
            domains = new String(filecontent,0, in.read(filecontent));
            in.close();
            if(domains.contains("\n"))linesep = "\n";
            if(domains.contains("\r\n"))linesep = "\r\n";
        }catch (IOException e){}
        String[] domainSegment = domains.split(linesep);
        for(String domain : domainSegment){
            if(("."+tail).equals(domain.split(" : ")[0])){
                server = domain.split(" : ")[1];
                break;
            }
        }
        return server;
    }

    public HashMap get(String url, boolean api){
        String tail = getTail(url);
        String server = getServer(tail);
        String get = "";
        if(api){
            try{
                get = queryDomainApi(url);
            }catch (IOException | NoSuchAlgorithmException | KeyManagementException e){}
        }
        else
            get = queryDoamin(url,server);
        for(String key : parseList(get).keySet()){
            System.out.print(key+": ");
            for(String info : parseList(get).get(key))
                System.out.print(info+" ");
            System.out.println();
        }
       return parseList(get);
    }

    public HashMap<String, ArrayList<String>> parseList(String context){
        HashMap<String, ArrayList<String>> info = new HashMap<>();
        context = context.replace("   ","");
        String[] infoSegment = context.split("\n");

        for(String infoItem : infoSegment){
            if(infoItem.contains(": ")){
                String key = infoItem.split(": ")[0];
                String dat = infoItem.split(": ")[1];
                if(!info.containsKey(key)){
                    ArrayList<String> datas = new ArrayList<>();
                    datas.add(dat);
                    info.put(key,datas);
                }else{
                    info.get(key).add(dat);
                    //datas.add(dat);
                }
            }
        }
        return info;
    }
    public static void main(String[] args) {
        //System.out.println(getTail("cheng.xin"));
        WhoisUtil wu = new WhoisUtil();
        wu.get("bilibili.com", false);
    }

}
