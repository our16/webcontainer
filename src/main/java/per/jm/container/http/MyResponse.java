package per.jm.container.http;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MyResponse {

    private PrintWriter pw;

    private int status = 200;
    private String titile = "ok";
    private String httpv = "http/1.1";
    private String rn = "\r\n";
    private String blank = ": ";
    private Map<String,String> headers = new HashMap<String, String>();
    private String header = "http/1.1 " + status + " "+titile+"\r\n";
    private String error = "<html>\n" +
            "<head><title>502 Bad Gateway</title></head>\n" +
            "<body bgcolor=\"white\">\n" +
            "<center><h1>502 Bad Gateway</h1></center>\n" +
            "<hr><center>nginx/1.14.0</center>\n" +
            "</body>\n" +
            "</html>";

    private String notFound = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
            "    <meta content=\"width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no\" name=\"viewport\" id=\"viewport\">\n" +
            "    <title>404 Not Found</title>\n" +
            "    <style type=\"text/css\">\n" +
            "        html, body {\n" +
            "            margin: 0;\n" +
            "            padding: 0;\n" +
            "        }\n" +
            "\n" +
            "        body {\n" +
            "            font-family: PingFang SC, Helvetica Neue, Hiragino Sans GB, Segoe UI, Microsoft YaHei, sans-serif;\n" +
            "            font-size: 14px;\n" +
            "            line-height: 1.5;\n" +
            "            background: #f2f4f6;\n" +
            "            color: #323a45;\n" +
            "            -webkit-font-smoothing: antialiased;\n" +
            "        }\n" +
            "\n" +
            "        .page {\n" +
            "            height: 100vh;\n" +
            "            display: flex;\n" +
            "            justify-content: center;\n" +
            "            align-items: center;\n" +
            "            line-height: 1.4;\n" +
            "        }\n" +
            "        .wrapper {\n" +
            "            padding-right: 378px;\n" +
            "            background-size: 278px;\n" +
            "            background-position: right;\n" +
            "            background-repeat: no-repeat;\n" +
            "        }\n" +
            "\n" +
            "        .page-404 .wrapper {\n" +
            "            background-image: url(https://dn-coding-net-production-static.qbox.me/static/47b05c7cffd4d5e77ef99275abaf9017.png);\n" +
            "        }\n" +
            "\n" +
            "        .page-500 .wrapper {\n" +
            "            background-image: url(https://dn-coding-net-production-static.qbox.me/static/528d7710668c5fd6e6dfb4f61c2f9b65.png);\n" +
            "        }\n" +
            "        h1 {\n" +
            "            margin: 0;\n" +
            "            color: #4f565f;\n" +
            "            font-size: 120px;\n" +
            "            font-weight: bold;\n" +
            "        }\n" +
            "        a {\n" +
            "            text-decoration: none;\n" +
            "        }\n" +
            "        .summary {\n" +
            "            margin-top: 0;\n" +
            "            margin-bottom: 40px;\n" +
            "            color: #76808e;\n" +
            "            font-size: 24px;\n" +
            "        }\n" +
            "        .button {\n" +
            "            display: inline-block;\n" +
            "            border-radius: 4px;\n" +
            "            box-sizing: border-box;\n" +
            "            cursor: pointer;\n" +
            "            outline: none;\n" +
            "            -webkit-transition: all .1s ease;\n" +
            "            transition: all .1s ease;\n" +
            "            border: 1px solid #323a45;\n" +
            "            background-color: #323a45;\n" +
            "            color: #fff;\n" +
            "            font-size: 18px;\n" +
            "            padding: 0 30px;\n" +
            "            height: 48px;\n" +
            "            line-height: 46px;\n" +
            "        }\n" +
            "        .button:hover {\n" +
            "            background-color: #425063;\n" +
            "            border: 1px solid #425063;\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "\n" +
            "<body>\n" +
            "    <div class=\"page page-404\">\n" +
            "        <div class=\"wrapper\">\n" +
            "            <h1>404!</h1>\n" +
            "            <p class=\"summary\">页面，我找不到你，我找不到你啊～</p>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";

    public MyResponse(OutputStream os) {
        pw = new PrintWriter(os);
        headers.put("Content-Type:","text/html");
        headers.put("Server","MengGi");
        headers.put("Accept","text/html,application/json;q=0.9,image/webp,image/apng,*/*;");
        headers.put("Accept-Encoding","gzip, deflate, br");
        headers.put("Accept-Language","zh-CN,zh;q=0.9");
        headers.put("Cache-Control","max-age=0");
        headers.put("Access-Control-Allow-Origin","*");
        headers.put("Keep-Alive","timeout=5, max=1000");
        headers.put("Connection","Keep-Alive");
    }

    /**
     * 响应客户端
     */
    public void write(String outString) throws Exception {
        Set entry = headers.entrySet();
        if(status == 500){
            outString = error;
            titile = "Internal Server Error";

        }else if(status == 404){
            outString = notFound;
            titile = "Not Found";

        }
        header = httpv+" "+status+" "+titile+rn;
        String content = header;
        Iterator iterator =entry.iterator();
        while(iterator.hasNext()){
            String[] kv = iterator.next().toString().split("=");
            content += kv[0]+this.blank+kv[1]+this.rn;
        }
        content += rn;
        content += outString;
        pw.write(content);
        pw.close();
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public void setHeader(String key,String value){
        this.headers.put(key,value);
    }
}
