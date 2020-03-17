package defaultPackage;

import java.io.*;
import java.net.URL;

public class IPLocation {
    private static final String LOOKUP_126 = "ip.ws.126.net";
    private static final String LOOKUP_126_2LV_PATH = "/ipquery?ip=";
    private static final String LOOKUP_ALI = "http://ip.taobao.com/service/getIpInfo.php?ip=";
    private static final String LOOKUP_API = "http://ip-api.com/json/";
    private String nation = "--";
    private String nationCode = "--";
    private String province = "--";
    private String provinceCode = "--";
    private String city = "--";
    private String zip = "--";
    private String lat = "--";
    private String lon = "--";
    private String timezone = "--";
    private String isp = "--";
    private String netType = "--";
    private String ip = "";

    IPLocation(){
        this.ip = "127.0.0.1";
    }

    IPLocation(String ip) {
        this.ip = ip;
    }

    private String  connect(String type) throws IOException{
        String content = "";
        URL target = new URL((type.equals("ip-api")?LOOKUP_API:LOOKUP_ALI)+this.ip);
        InputStream is = target.openStream();
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
        return content;
    }

    public void lookup(){
        boolean connect_fail = false;
        if(this.ip.equals("127.0.0.1")){
            System.out.println("localhost");
            this.netType = "localhost";
        }
        else if(this.ip.substring(0,7).equals("192.168")||this.ip.substring(0,2).equals("10")){
            System.out.println("LAN");
            this.netType = "LAN";
        }
        else{
            String[] info;
            try {
                info = connect("ip-api").replace("{","").replace("}","").replace("\"\"","\"--\"").split(",");
                for(String i : info){
                    i=i.replace("\"","");
                    //System.out.println(i);
                    String[] item = i.split(":");
                    switch (item[0]){
                        case "country" : this.nation = item[1];
                        case "countryCode" : this.nationCode = item[1];
                        case "regionName" : this.province = item[1];
                        case "region" : this.provinceCode = item[1];
                        case "city" : this.city = item[1];
                        case "zip" : this.zip = item[1];
                        case "lat" : this.lat = item[1];
                        case "lon" : this.lon = item[1];
                        case "timezone" : this.timezone = item[1];
                        case "isp" : this.isp = item[1];
                    }
                }
            } catch (IOException e) {
                connect_fail = true;
            }

            if(connect_fail){
                try{
                    info = connect("ali").replace("{","").replace("}","").replace("\"\"","\"-\"").split(",");
                    for(String i : info){
                        i=i.replace("\"","");
                        //System.out.println(i);
                        String[] item = i.split(":");
                        switch (item[0]){
                            case "country" : this.nation = item[1];
                            case "region" : this.province = item[1];
                            case "city" : this.city = item[1];
                            case "isp" : this.isp = item[1];
                            case "country_id" : this.nationCode = item[1];
                        }
                    }
                }catch (IOException e){
                    System.out.println("unable to connect to server.");
                }
            }
            this.netType = "WAN";
        }
    }

    public void setIP(String ip){
        this.ip = ip;
    }

    public String getNation(){
        return this.nation;
    }

    public String getNationCode(){
        return this.nationCode;
    }

    public String getProvince(){
        return this.province;
    }

    public String getProvinceCode(){
        return this.provinceCode;
    }

    public String getCity(){
        return this.city;
    }

    public String getZip(){
        return this.zip;
    }

    public String getLat(){
        return this.lat;
    }

    public String getLon(){
        return this.lon;
    }

    public String getTimezone(){
        return this.timezone;
    }

    public String getIsp(){
        return this.isp;
    }

    public String getNetType(){
        return this.netType;
    }

    public String getIP(){
        return this.ip;
    }

    public String getReport(){
        return "country: "+this.nation+", countryCode: "+this.nationCode+
                ", region: "+this.province+", regionCode: "+this.provinceCode+
                ", city: "+this.city+", zip: "+this.zip+
                ", lat: "+this.lat+", lon: "+this.lon+
                ", timezone: "+this.timezone+", isp: "+this.isp+
                ", net type: "+this.netType+", ip: "+this.ip;
    }

    public static void main(String[] args) {
        IPLocation ipLocation = new IPLocation("56.23.52.41");
        ipLocation.setIP("148.153.64.18");
        ipLocation.lookup();
        System.out.println(ipLocation.getReport());
    }
}
