package client;

import server.Server;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;

import Msg.*;

/**
 * 网络方法接口
 */
public class netClient {
    private Client client;
    private int UDP_PORT;//客户端的UDP端口号
    private String serverIP;//服务器IP地址
    private int serverUDPPort;//服务器转发客户但UDP包的UDP端口
    private DatagramSocket ds = null;//客户端的UDP套接字

    public void setUDP_PORT(int UDP_PORT) {
        this.UDP_PORT = UDP_PORT;
    }

    public netClient(Client client){
        this.client = client;
        try {
            this.UDP_PORT = getRandomUDPPort();
        }catch (Exception e){
            client.getUdpPortWrongDialog().setVisible(true);//弹窗提示
            System.exit(0);//如果选择到了重复的UDP端口号就退出客户端重新选择.
        }
    }

    /**
     * 与服务器进行TCP连接
     * @param ip server IP
     */
    public void connect(String ip){
        serverIP = ip;
        Socket s = null;
        try {
            ds = new DatagramSocket(UDP_PORT);//创建UDP套接字
            try {
                s = new Socket(ip, Server.TCP_PORT);//创建TCP套接字
            }catch (Exception e1){
                client.getServerNotStartDialog().setVisible(true);
            }
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            dos.writeInt(UDP_PORT);//向服务器发送自己的UDP端口号
            DataInputStream dis = new DataInputStream(s.getInputStream());
            int id = dis.readInt();//获得自己的id号
            this.serverUDPPort = dis.readInt();//获得服务器转发客户端消息的UDP端口号
            client.getMe().setId(id);//设置坦克的id号
            System.out.println("connect to server successfully...");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                if(s != null) s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        new Thread(new UDPThread()).start();//开启客户端UDP线程, 向服务器发送或接收游戏数据

        connectMsg msg = new connectMsg(client.getMe());
        send(msg);
    }

    /**
     * 客户端随机获取UDP端口号
     * @return
     */
    private int getRandomUDPPort(){
        return 55558 + (int)(Math.random() * 9000);
    }

    public void send(Msg msg){
        msg.send(ds, serverIP, serverUDPPort);
    }

    public class UDPThread implements Runnable{

        byte[] buf = new byte[1024];

        @Override
        public void run() {
            while(null != ds){
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                try{
                    ds.receive(dp);
                    parse(dp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void parse(DatagramPacket dp) {
            ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0, dp.getLength());
            DataInputStream dis = new DataInputStream(bais);
            int msgType = 0;
            try {
                msgType = dis.readInt();//获得消息类型
            } catch (IOException e) {
                e.printStackTrace();
            }
            Msg msg = null;
            switch (msgType){//根据消息的类型调用对应消息的解析方法
            case Msg.CONNECT_MSG:
            	msg = new connectMsg(client);
            	msg.parse(dis);
            	break;
            case Msg.MOVE_MSG:
            	msg = new MoveMsg(client);
            	msg.parse(dis);
            	break;
            case Msg.WIN_MSG:
            	msg = new WinMsg(client);
            	msg.parse(dis);
            	break;
            case Msg.CONNECT_TO_ORI_MSG:
            	msg = new connectToOriMsg(client);
            	msg.parse(dis);
            	break;
            }
        }
    }

    public void sendClientDisconnectMsg(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream(88);
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(UDP_PORT);//发送客户端的UDP端口号, 从服务器Client集合中注销
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != dos){
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(null != baos){
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
