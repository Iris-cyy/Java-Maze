package server;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import client.*;

public class Server extends Frame {

	public static int ID = 0;//id号的初始序列
    public static final int TCP_PORT = 55555;//TCP端口号
    public static final int UDP_PORT = 55556;//转发客户端数据的UDP端口号
    private List<Client> clients = new ArrayList<>();//客户端集合
    private Image offScreenImage = null;//服务器画布
    private static final int SERVER_HEIGHT = 500;
    private static final int SERVER_WIDTH = 500;

    public static void main(String[] args) {
        Server server = new Server();
        server.launchFrame();
        server.start();
    }

    public void start(){
        new Thread(new UDPThread()).start();
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(TCP_PORT);//在TCP欢迎套接字上监听客户端连接
            System.out.println("Server has started...");
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true){
            Socket s = null;
            try {
                s = ss.accept();//给客户但分配专属TCP套接字
                DataInputStream dis = new DataInputStream(s.getInputStream());
                int UDP_PORT = dis.readInt();//记录客户端UDP端口
                Client client = new Client(s.getInetAddress().getHostAddress(), UDP_PORT, ID);//创建Client对象
                clients.add(client);//添加进客户端容器
                ID++;
                System.out.println("Client " + ID + " has connected...");
                
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                dos.writeInt(ID);//向客户端分配id号
                dos.writeInt(Server.UDP_PORT);//告诉客户端自己的UDP端口号
            }catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if(s != null) s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class UDPThread implements Runnable{

        byte[] buf = new byte[1024];

        @Override
        public void run() {
            DatagramSocket ds = null;
            try{
                ds = new DatagramSocket(UDP_PORT);
            }catch (SocketException e) {
                e.printStackTrace();
            }

            while (null != ds){
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                try {
                    ds.receive(dp);
                    for (Client c : clients){
                        dp.setSocketAddress(new InetSocketAddress(c.IP, c.UDP_PORT));
                        ds.send(dp);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class Client{
        String IP;
        int UDP_PORT;
        int id;

        public Client(String ipAddr, int UDP_PORT, int id) {
            this.IP = ipAddr;
            this.UDP_PORT = UDP_PORT;
            this.id = id;
        }
    }

    @Override
    public void paint(Graphics g) {
    	g.setFont(new Font("Calibri", Font.PLAIN, 20));
        g.drawString("Clients :", 30, 70);
        int y = 120;
        for(int i = 0; i < clients.size(); i++){//显示出每个客户端的信息
            Client c = clients.get(i);
            g.drawString("id : " + c.id + " - IP : " + c.IP, 30, y);
            y += 30;
        }
    }

    @Override
    public void update(Graphics g) {
        if(offScreenImage == null) {
            offScreenImage = this.createImage(SERVER_WIDTH, SERVER_HEIGHT);
        }
        Graphics gOffScreen = offScreenImage.getGraphics();
        Color c = gOffScreen.getColor();
        gOffScreen.setColor(Color.lightGray);
        gOffScreen.fillRect(0, 0, SERVER_WIDTH, SERVER_HEIGHT);
        gOffScreen.setColor(c);
        paint(gOffScreen);
        g.drawImage(offScreenImage, 0, 0, null);
    }

    public void launchFrame() {
        this.setLocation(200, 100);
        this.setSize(SERVER_WIDTH, SERVER_HEIGHT);
        this.setTitle("Server");
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        this.setResizable(false);
        this.setBackground(Color.lightGray);
        this.setVisible(true);
        new Thread(new PaintThread()).start();

    }

    /**
     * 重画线程
     */
    class PaintThread implements Runnable {
        public void run() {
            while(true) {
                repaint();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
