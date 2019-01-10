package com.gbdata.loadbalancing;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * 客户端均衡地调用服务端，基于zookeeper实现
 */
public class MyZkClient {
    /**
     * 客户端保存所有可用服务端信息
     */
    public static List<String> serverList = new ArrayList<String>();
    /**
     * 请求计数器
     */
    private static int reqestCount = 0;
    /**
     * 客户端扫描所有的server
     */
    public static void initServer() {
        String path = "/hellozk";
        final ZkClient zkClient = new ZkClient("127.0.0.1:2181", 12000, 12000);
        List<String> children = zkClient.getChildren(path);
        System.out.println(children);
        for (String serverIp : children) {
            System.out.println(serverIp+"==============serverIp==============");
            serverList.add((String) zkClient.readData(path + "/" + serverIp));
        }
        System.out.println("####从zookeeper注册中心获取的服务器server信息==>serverList:" + serverList.toString());
        // 监听事件
        zkClient.subscribeChildChanges(path, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                serverList.clear();
                for (String serverIp : currentChilds) {
                    System.out.println(serverIp+"==============serverIp==============");
                    serverList.add((String)zkClient.readData(parentPath + "/" + serverIp));
                }
                System.out.println("####客户端监听到节点发生变化###服务器server信息==>serverList:" + serverList.toString());

            }
        });
    }
    /**
     * 简单的负载均衡实现(按照请求计数来分配server)
     */
    public static String getServer() {
        int serverCount = serverList.size();
        String serverHost = serverList.get(reqestCount % serverCount);
        reqestCount++;
        return serverHost;
    }
    public void send(String name) {
        String server = MyZkClient.getServer();
        System.out.println("reqestCount:" + reqestCount);
        System.out.println("获取到的server:" + server);
        String[] config = server.split(":");
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            socket = new Socket(config[0], Integer.parseInt(config[1]));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(name);
            String resp = in.readLine();
            System.out.println("client receive : " + resp);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void main(String[] args) {
        initServer();
        MyZkClient client = new MyZkClient();
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String name;
            try {
                System.out.println("=========请输入=========");
                name = console.readLine();
                if ("exit".equals(name)) {
                    System.exit(0);
                }
                client.send(name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}