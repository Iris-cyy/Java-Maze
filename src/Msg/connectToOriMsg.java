package Msg;

import client.Client;
import client.Draw;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class connectToOriMsg implements Msg{
    private int msgType = Msg.CONNECT_TO_ORI_MSG;
    private Draw me;
    private Client client;

    public connectToOriMsg(Client client){
        this.client = client;
    }
    
    public connectToOriMsg(Draw me) {
    	this.me = me;
    }

    public void send(DatagramSocket ds, String IP, int UDP_Port){
    	System.out.println("connect_ori_sent");
        ByteArrayOutputStream baos = new ByteArrayOutputStream(88);
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(msgType);
            dos.writeInt(me.getId());
            dos.writeUTF(me.getName());

        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] buf = baos.toByteArray();
        try{
            DatagramPacket dp = new DatagramPacket(buf, buf.length, new InetSocketAddress(IP, UDP_Port));
            ds.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parse(DataInputStream dis){
        try{
            int id = dis.readInt();
            if(id == this.client.getMe().getId()){
                return;
            }
            String Name = dis.readUTF();
            	System.out.println("connect_ori_received");
            	client.getMe().generatePlayer2(18, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
