package per.jm.container.starter;

import per.jm.container.domain.ServletVO;
import per.jm.container.http.MyResponse;
import per.jm.container.servlet.Servlet;
import per.jm.container.scan.MyProperties;
import per.jm.container.session.Session;

import java.net.*;
import java.util.HashMap;
import java.util.List;

public class Starter {

    private String ipBind = "127.0.0.1";
    private int port = 8080;
    private ServerSocket serverSocket;
    public static boolean isStart = true;
    private static HashMap<String, Servlet> servlets = new HashMap<String, Servlet>();
    private static Session session;

    public Starter() throws Exception {
        init();
    }

    private void init() throws Exception {
        /**
         *
         * 加载配置文件，获取servlet路径
         *
         * */
       MyProperties myProperties = new MyProperties();
        List<ServletVO> list =myProperties.getServletVOList();
        for(ServletVO servletVO : list){
            Servlet servlet = (Servlet)servletVO.getClazz().getDeclaredConstructor().newInstance();
            servlets.put(servletVO.getRoute(),servlet);
        }
    }



    private void start() throws Exception {
        InetSocketAddress inetAddress = new InetSocketAddress(ipBind, port);
        serverSocket = new ServerSocket();
        serverSocket.bind(inetAddress);
        System.out.println("server is running!");
        while (isStart) {
            Socket client = serverSocket.accept();
            MuilConnection muilConnection = new MuilConnection(client);
            Thread thread = new Thread(muilConnection);
            thread.start();
        }
    }
    public static Session getSession(){
        if(null == session ) {
            synchronized (Starter.class) {
                if (null == session) {
                    session = new Session();
                }
            }
        }
        return session;
    }
    public static Servlet getServletInnstance(String route){
        return servlets.get(route);
    }

    public static void main(String[] args) {
        try {
            new Starter().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
