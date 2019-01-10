package com.gbdata.loadbalancing;

import org.I0Itec.zkclient.ZkClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MyZkServer implements Runnable {
    private int port;
    public MyZkServer(int port) {
        this.port = port;
    }
    /**
     * 向ZooKeeper注册当前服务端
     */
    public void registerToZk() {
        ZkClient client = new ZkClient("127.0.0.1:2181", 12000, 12000);
        String path = "/hellozk/server" + port;
        if (client.exists(path)) {
            client.delete(path);
        }
        client.createEphemeral(path, "127.0.0.1:" + port);
    }
    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            registerToZk();
            System.out.println("Server started,ip: " + serverSocket.getInetAddress() + " port:" + port);
            Socket socket;
            //保持服务器端程序一直运行
            while (true) {
                System.out.println("============"+serverSocket+"等待连接============");
                socket = serverSocket.accept();
                //单开一个线程处理每一个客户端连接
                Thread thread=new Thread(new MyServerHandler(socket));
                thread.start();
                System.out.println("============"+thread+"启动处理请求============");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (Exception e2) {
            }
        }
    }
    public static void main(String[] args) throws IOException {
        int port = 1003;
        MyZkServer server = new MyZkServer(port);
        Thread thread = new Thread(server);
        thread.start();
    }
}