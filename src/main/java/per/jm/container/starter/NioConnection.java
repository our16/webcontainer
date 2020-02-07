package per.jm.container.starter;

import per.jm.container.filter.FilterChain;
import per.jm.container.http.MyRequest;
import per.jm.container.http.MyResponse;
import per.jm.container.servlet.Servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class NioConnection implements Runnable {
    //多路复用调度器
    private Selector selector;


    public NioConnection(ServerSocketChannel serverSocketChannel) throws IOException {
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("this :" + this);
    }

    public void run() {
        try {
            while (true) {
                int readyChannels = 0;
                readyChannels = selector.select();
                if (readyChannels == 0) continue;
                Set selectedKeys = selector.selectedKeys();
                Iterator keyIterator = selectedKeys.iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey selectionKey = (SelectionKey) keyIterator.next();
                    keyIterator.remove();
                    if (selectionKey.isAcceptable()) {
                        //接收客户端的连接请求，完成TCP三次握手
                        this.handleAccept(selectionKey);
                    }
                    if (selectionKey.isReadable()) {
                        System.out.println("读事件");
                        // a channel is ready for reading
                        this.handleRead(selectionKey);
                    }
                    if (selectionKey.isWritable()) {
                        System.out.println("写事件");
                        this.handleOver(selectionKey);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册一个读事件
     *
     * */
    public void handleAccept(SelectionKey selectionKey) throws IOException {
        //获取channel,新接入的客户端链接需要进行注册
        SocketChannel socketChannel = ((ServerSocketChannel) selectionKey.channel()).accept();
        //非阻塞
        socketChannel.configureBlocking(false);
        //注册selector
        socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(1024));
    }

    /***
     *
     * 读数据，并进一步处理
     * */
    public void handleRead(SelectionKey selectionKey) throws Exception {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
        MyRequest myRequest = new MyRequest(socketChannel,buffer);
        MyResponse myResponse = new MyResponse(selectionKey);
        myRequest.setResponse(myResponse);
        Servlet servlet = Starter.getServletInnstance(myRequest.getUrl());
        try{
            if (null == servlet) {
                myResponse.setStatus(404);
                myResponse.write("");
            } else {
                new FilterChain(servlet,myRequest,myResponse);
            }
        }catch (Exception e){
            myResponse.setStatus(500);
            myResponse.write("");
            e.printStackTrace();
        }
        //调用response的时候注册写事件
        //读完了注册写事件
        socketChannel.register(selectionKey.selector(), SelectionKey.OP_WRITE);
    }

    private void handleWrite(SelectionKey selectionKey,String content) throws IOException {
        if(null == content){
            return;
        }
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put(content.getBytes());
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
        byteBuffer.clear();
    }

    public  void handleOver(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        socketChannel.shutdownInput();
        socketChannel.shutdownInput();
        socketChannel.close();
        selectionKey.cancel();
    }
}
