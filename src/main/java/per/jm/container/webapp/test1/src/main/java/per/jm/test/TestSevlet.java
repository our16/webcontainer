package per.jm.container.webapp.test1.src.main.java.per.jm.test;

import per.jm.container.http.MyRequest;
import per.jm.container.http.MyResponse;
import per.jm.container.servlet.Servlet;

public class TestSevlet extends Servlet {
    @Override
    public void doGet(MyRequest request, MyResponse response) throws Exception {
       // System.out.println(request.getUrl());
       // System.out.println(request.getMethod());
        System.out.println("key:"+request.getParam("key1"));
        request.getSessionVO();
        response.write("successed");
    }

    @Override
    public void doPost(MyRequest request, MyResponse response) throws Exception {
        //System.out.println(request.getUrl());
        System.out.println(request.getParam("key1"));
        System.out.println(request.getParam("code2"));
        System.out.println(request.getParam("key"));
        response.write("successed param is :"+request.getParam("code2"));
    }
}
