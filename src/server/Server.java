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

	public static int ID = 0;//id�ŵĳ�ʼ����
    public static final int TCP_PORT = 55555;//TCP�˿ں�
    public static final int UDP_PORT = 55556;//ת���ͻ������ݵ�UDP�˿ں�
    private List<Client> clients = new ArrayList<>();//�ͻ��˼���
    private Image offScreenImage = null;//����������
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
            ss = new ServerSocket(TCP_PORT);//��TCP��ӭ�׽����ϼ����ͻ�������
            System.out.println("Server has started...");
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true){
            Socket s = null;
            try {
                s = ss.accept();//���ͻ�������ר��TCP�׽���
                DataInputStream dis = new DataInputStream(s.getInputStream());
                int UDP_PORT = dis.readInt();//��¼�ͻ���UDP�˿�
                Client client = new Client(s.getInetAddress().getHostAddress(), UDP_PORT, ID);//����Client����
                clients.add(client);//��ӽ��ͻ�������
                ID++;
                System.out.println("Client " + ID + " has connected...");
                
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                dos.writeInt(ID);//��ͻ��˷���id��
                dos.writeInt(Server.UDP_PORT);//���߿ͻ����Լ���UDP�˿ں�
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
        for(int i = 0; i < clients.size(); i++){//��ʾ��ÿ���ͻ��˵���Ϣ
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
     * �ػ��߳�
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
