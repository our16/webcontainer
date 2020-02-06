package per.jm.container.servlet;

import per.jm.container.http.MyRequest;
import per.jm.container.http.MyResponse;

public abstract class Servlet {
    public Object service(MyRequest request,MyResponse response)throws Exception {
        //由service方法决定，来调用doGet或者是doPost

        if("GET".equalsIgnoreCase(request.getMethod())) {
            doGet(request, response);
        }else {
            doPost(request, response);
        }
        return null;
    }
    public abstract void doGet(MyRequest request, MyResponse response) throws Exception;
    public abstract void doPost(MyRequest request,MyResponse response) throws Exception;
}
