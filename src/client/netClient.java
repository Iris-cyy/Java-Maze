package client;

import server.Server;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;

import Msg.*;

/**
 * ���緽���ӿ�
 */
public class netClient {
    private Client client;
    private int UDP_PORT;//�ͻ��˵�UDP�˿ں�
    private String serverIP;//������IP��ַ
    private int serverUDPPort;//������ת���ͻ���UDP����UDP�˿�
    private DatagramSocket ds = null;//�ͻ��˵�UDP�׽���

    public void setUDP_PORT(int UDP_PORT) {
        this.UDP_PORT = UDP_PORT;
    }

    public netClient(Client client){
        this.client = client;
        try {
            this.UDP_PORT = getRandomUDPPort();
        }catch (Exception e){
            client.getUdpPortWrongDialog().setVisible(true);//������ʾ
            System.exit(0);//���ѡ�����ظ���UDP�˿ںž��˳��ͻ�������ѡ��.
        }
    }

    /**
     * �����������TCP����
     * @param ip server IP
     */
    public void connect(String ip){
        serverIP = ip;
        Socket s = null;
        try {
            ds = new DatagramSocket(UDP_PORT);//����UDP�׽���
            try {
                s = new Socket(ip, Server.TCP_PORT);//����TCP�׽���
            }catch (Exception e1){
                client.getServerNotStartDialog().setVisible(true);
            }
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            dos.writeInt(UDP_PORT);//������������Լ���UDP�˿ں�
            DataInputStream dis = new DataInputStream(s.getInputStream());
            int id = dis.readInt();//����Լ���id��
            this.serverUDPPort = dis.readInt();//��÷�����ת���ͻ�����Ϣ��UDP�˿ں�
            client.getMe().setId(id);//����̹�˵�id��
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

        new Thread(new UDPThread()).start();//�����ͻ���UDP�߳�, ����������ͻ������Ϸ����

        connectMsg msg = new connectMsg(client.getMe());
        send(msg);
    }

    /**
     * �ͻ��������ȡUDP�˿ں�
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
                msgType = dis.readInt();//�����Ϣ����
            } catch (IOException e) {
                e.printStackTrace();
            }
            Msg msg = null;
            switch (msgType){//������Ϣ�����͵��ö�Ӧ��Ϣ�Ľ�������
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
            dos.writeInt(UDP_PORT);//���Ϳͻ��˵�UDP�˿ں�, �ӷ�����Client������ע��
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
