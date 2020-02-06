package per.jm.container.webapp.test2.src.main.java.per.jm.test;

import com.alibaba.fastjson.JSONObject;
import per.jm.container.http.MyRequest;
import per.jm.container.http.MyResponse;
import per.jm.container.servlet.Servlet;

public class TestSevlet2 extends Servlet {
    @Override
    public void doGet(MyRequest request, MyResponse response) throws Exception {
       // System.out.println(request.getUrl());
       // System.out.println(request.getMethod());
        System.out.println(request.getParam("key1"));
        String str ="{'code':1}";
        response.write(JSONObject.toJSONString(str));
    }

    @Override
    public void doPost(MyRequest request, MyResponse response) throws Exception {
        //System.out.println(request.getUrl());
        System.out.println(request.getParam("code2"));
        response.write("successed param is :"+request.getParam("code2"));
    }
}
