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

public class connectMsg implements Msg{
    private int msgType = Msg.CONNECT_MSG;
    private Draw me;
    private Client client;

    public connectMsg(Client client){
        this.client = client;
    }
    
    public connectMsg(Draw me) {
    	this.me = me;
    }

    public void send(DatagramSocket ds, String IP, int UDP_Port){
    	System.out.println("connect_msg_sent");
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
            System.out.println("connect_msg_received");
            String Name = dis.readUTF();
            client.getMe().generatePlayer2(18, 1);
            connectToOriMsg msg = new connectToOriMsg(client.getMe());
            client.getNc().send(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
