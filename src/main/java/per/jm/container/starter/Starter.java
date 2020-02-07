package per.jm.container.starter;

import per.jm.container.domain.ServletVO;
import per.jm.container.http.MyResponse;
import per.jm.container.servlet.Servlet;
import per.jm.container.scan.MyProperties;
import per.jm.container.session.Session;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class Starter {

    private String ipBind = "127.0.0.1";
    private int port = 8080;
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
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(inetAddress);
        System.out.println("server is running!");
        while (isStart) {
            Socket client = serverSocket.accept();
            MuilConnection muilConnection = new MuilConnection(client);
            Thread thread = new Thread(muilConnection);
            thread.start();
        }
    }
    private void nioStart() throws IOException {
        InetSocketAddress inetAddress = new InetSocketAddress(ipBind, port);
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(inetAddress);
        //飞阻塞模式
        serverSocketChannel.configureBlocking(false);
        NioConnection nioConnection = new NioConnection(serverSocketChannel);
        Thread thread = new Thread(nioConnection);
        thread.start();
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
            InputStream in = new FileInputStream(System.getProperty("user.dir")+"/src/main/java/per/jm/container/config/server.properties");
            //2、新建一个Properties对象
            Properties pro = new Properties();
            pro.load(in);    //properties对象封装配置文件的输入流，现在文件里面的信息都已被封装成String
            Starter starter = new Starter();
            Set set = pro.entrySet();
            Iterator iterator = set.iterator();
            while(iterator.hasNext()) {
                String[] kv = iterator.next().toString().split("=");
                if(kv[0].equals("server.model")){
                    if(kv[1].equals("io")){
                        starter.start();
                    }else{
                        starter.nioStart();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
