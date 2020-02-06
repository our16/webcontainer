package per.jm.container.starter;

import per.jm.container.http.MyRequest;
import per.jm.container.http.MyResponse;
import per.jm.container.servlet.Servlet;

import java.io.OutputStream;
import java.net.Socket;

public class MuilConnection implements Runnable{
    private Socket client;
    public MuilConnection(Socket client){
        this.client = client;
    }
    public void run() {
        try {
            this.proccess(client);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void proccess(Socket client) throws Exception {
        MyRequest myRequest = new MyRequest(client.getInputStream());
        OutputStream response = client.getOutputStream();
        MyResponse myResponse = new MyResponse(response);
        myRequest.setResponse(myResponse);
        Servlet servlet = Starter.getServletInnstance(myRequest.getUrl());
       try{
           if (null == servlet) {
               myResponse.setStatus(404);
               myResponse.write("");
           } else {
               servlet.service(myRequest, myResponse);
           }
       }catch (Exception e){
           myResponse.setStatus(500);
           myResponse.write("");
           e.printStackTrace();
       }
        response.flush();
        response.close();
        client.close();
    }
}
