package per.jm.container.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import per.jm.container.domain.Data;
import per.jm.container.domain.SessionVO;
import per.jm.container.starter.Starter;
import per.jm.container.util.MyAssert;

import java.io.*;
import java.util.*;

/**
 * 封装 input stream
 */
public class MyRequest {
    private String url;
    private String method;
    private String originIp;
    private String urlParam;
    private String body;
    private int bodyIndex;
    private int lineIndex;
    private String httpV;
    private String boundary;
    private int len;
    private boolean isParse = false;
    private HashMap<String, Object> params = new HashMap<String, Object>();
    private HashMap<String, String> header = new HashMap<String, String>();
    private BufferedInputStream bufferedInputStream;
    private MyResponse response;
    private SessionVO session;
    public void setResponse(MyResponse response){
        this.response = response;
    }
    public MyRequest(InputStream requestStream) {

        try {
            bufferedInputStream = new BufferedInputStream(requestStream);
            //HTTP协议就是一串字符串
            String content = "";
            byte[] buff = new byte[1024];
            boolean first = false;
            while ((len = bufferedInputStream.read(buff, 0, buff.length)) != -1) {
                content += new String(buff, 0, len);
                if (!first) {
                    this.bufGet(buff);
                    //解析第一行
                    this.doLine(buff);
                    //解析请求头
                    this.doHeader(buff);
                    first = true;
                }
                if (len < 1024) {
                    break;
                }
            }
            //解析请求体
            this.body = content.substring(bodyIndex);
            //this.doBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 把 line,header,body 分开
     */
    private void bufGet(byte[] str) {
        int count = 0, index = 0;
        boolean lineGet = false;
        while (index < len) {
            //回车符号和换行符分别为 13,10  ---> \r\n
            if (str[index] == 13 && str[index + 1] == 10) {
                count++;
                //避开 str--> \n
                index++;
                if (!lineGet) {
                    //请求行的数据尾偏移量(包含一个回车和换行符)
                    lineIndex = index;
                    lineGet = true;
                }
                //到这里header部分解析完了，一下则为body部分
                if (count > 1) {
                    this.bodyIndex = index;
                    return;
                } else if (index >= 1024) {
                    return;
                }
            } else {
                count = 0;
            }
            index++;
        }
    }

    /**
     * 解析第一行
     */
    private void doLine(byte[] str) {
        int index = 0, num = 0;
        String pStr = "";
        while (index < lineIndex) {
            if (str[index] != ' ' && str[index] != 13) {
                pStr += (char) str[index];
            } else {
                if (num == 0) {
                    //获取请求类型
                    this.method = pStr;
                } else if (num == 1) {
                    String[] uStr = pStr.split("\\?");
                    //获取路由和参数
                    if (pStr.contains("?")) {
                        this.urlParam = uStr[1];
                    }
                    this.url = uStr[0];
                } else if (num == 2) {
                    //获取http版本
                    this.httpV = pStr;
                }
                pStr = "";
                num++;
            }
            index++;
        }
    }

    /**
     * 解析请求头
     */
    private void doHeader(byte[] str) {
        int index = lineIndex + 1, num = 0;
        String pStr = "";
        String key = "";
        MyAssert.isTrue(index < bodyIndex, "下标解析错误");
        while (index < bodyIndex) {
            if (str[index] != ':' && str[index] != ' ' && str[index] != 13 && str[index] != 10) {
                pStr += (char) str[index];
            } else {
                //key值根据": " 确定
                if (str[index] == ':' && num == 0) {
                    key = pStr;
                    pStr = "";
                    num++;
                } else if (str[index] == 13 && !key.equals("") && !pStr.equals("")) {
                    this.header.put(key, pStr);
                    pStr = "";
                    index++;
                    num--;
                }
            }
            index++;
        }
        String contenType = this.header.get("Content-Type");
        if (null != contenType && contenType.contains(";")) {
            String[] con = contenType.split(";");
            //众多类型中只取第一个类型
            contenType = con[0];
            this.header.put("Content-Type", contenType);
            if (contenType.equalsIgnoreCase("multipart/form-data")) {
                this.boundary = con[1].split("=")[1];
            }
        }

        String sessionKey = header.get("Cookie");
        if(null != sessionKey){
            this.session = Starter.getSession().getSession(sessionKey);
        }
    }

    /**
     * 解析data body
     * 分情况解析
     * 1.x-www-form-urlencoded
     * 2.multipart/form-data
     * 3.json
     * 4...
     */
    private void doBody() throws Exception {
        String conentType = header.get("Content-Type");
        if ("application/x-www-form-urlencoded".equalsIgnoreCase(conentType)) {
            doBodyXWwwFormUrl();
        } else if ("application/json".equalsIgnoreCase(conentType)) {
            doBodyJson();
        } else if ("multipart/form-data".equalsIgnoreCase(conentType)) {
            doBodyMultipartFormData();
        } else {
            System.out.println("类型不支持");
        }
    }

    /**
     * 解析 application/x-www-form-urlencoded 类型的数据
     */
    private void doBodyXWwwFormUrl() {
        byte[] b = this.body.getBytes();
        String[] kv = new String(b, 1, b.length - 1).split("&");
        for (int i = 0; i < kv.length; i++) {
            String[] par = kv[i].split("=");
            if (par.length > 1) {
                this.params.put(par[0], par[1]);
//                System.out.println(par[0]+":"+par[1]);
            }
        }
    }

    /**
     * 解析json数据
     */
    private void doBodyJson() {
        try {
            JSONObject jsonObject = JSON.parseObject(this.body);
            Set<Map.Entry<String, Object>> entrySet = jsonObject.entrySet();
            Iterator iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                String param = iterator.next().toString();
                String[] kv = param.split("=");
                if (kv.length > 1) {
                    this.params.put(kv[0], kv[1]);
                }
            }
        } catch (Exception e) {
            //不是json数据
            e.printStackTrace();
        }
    }

    /**
     * 解析文件数据
     */
    private void doBodyMultipartFormData() throws Exception {

        if (!this.body.contains("--" + this.boundary + "--")) {
            throw new RuntimeException("数据错误");
        }
        //body才分为单个keyvalue
        String[] strs = this.body.split("--" + boundary);
        for (int i = 1; i < strs.length - 1; i++) {
            Data data = new Data();
            //对单个kv 进行循环解析
            byte[] bodys = strs[i].getBytes();
            String pStr = "";
            int count = 0, num = 0;
            String key = "";
            //从2 为了跳过 \r\n
            for (int j = 2; j < bodys.length; j++) {
                if (bodys[j] != ':' && bodys[j] != 13 && bodys[j] != 10) {
                    pStr += (char) bodys[j];
                    count++;
                } else {
                    if (count > 0) {
                        //kv段
                        if (num == 0) {
                            key = pStr;
                            num++;
                        } else if (num == 1) {
                            String[] kv = pStr.split("; ");
                            data.setKv(key, kv[0]);
                            for (int k = 1; k < kv.length; k++) {
                                String[] kv2 = kv[k].split("=");
                                data.setKv(kv2[0], kv2[1].replaceAll("\"", ""));
                            }
                            num = 0;
                            key = "";
                        }
                        j++;
                        count = 0;
                    } else {
                        //数据段
                        data.setData(strs[i].substring(j + 2));
                        String keyName = data.getKv("name");
                        this.params.put(keyName, data);
                        break;
                    }
                    pStr = "";
                }
            }
        }
        this.body = "";
    }

    private void doUrlParam(String params) {
        if (null != params) {
            String[] str = params.split("&");
            for (int i = 0; i < str.length; i++) {
                String[] kv = str[i].split("=");
                String key = kv[0];
                String value = kv[1];
                this.params.put(key, value);
            }
        }
    }

    private void doPostParams() throws Exception {

        if (!isParse) {
            this.doBody();
            isParse = true;
        }

    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public String getOriginIp() {
        return originIp;
    }

    /**
     * 需要数据时再取加载数据
     */
    public Object getParam(String key) throws Exception {
        //解析url参数
        doUrlParam(this.urlParam);
        if ("post".equalsIgnoreCase(this.method)) {
            this.doPostParams();
            Object data = params.get(key);
            //仅仅是multipart参数
            if (data instanceof Data) {
                return ((Data) data).getKv(key) == null ? ((Data) data).getData() : ((Data) data).getKv(key);
            }
        }
        return params.get(key);
    }

    public String getHeader(String key) {
        return this.header.get(key);
    }

    public SessionVO getSessionVO() {

        if(null == session){
            String JSESSIONID= UUID.randomUUID().toString().replaceAll("-","");
            response.setHeader("Set-Cookie",JSESSIONID);
            session = new SessionVO();
            Starter.getSession().setSession(JSESSIONID,"");
        }
        return session;
    }
}
