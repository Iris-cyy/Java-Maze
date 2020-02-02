package Msg;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import client.Client;

public class MoveMsg implements Msg {
    private int msgType = Msg.MOVE_MSG;
    private int id;
    private int posi, posj;
    private Client client;

    public MoveMsg(int id, int posi, int posj){
    	this.id = id;
        this.posi = posi;
        this.posj = posj;
    }

    public MoveMsg(Client client){
        this.client = client;
        
    }

    @Override
    public void send(DatagramSocket ds, String IP, int UDP_Port) {
    	System.out.println("["+id+"]"+"move_msg_sent: "+posi+ " " + posj);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(30);
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(msgType);
            dos.writeInt(id);
            dos.writeInt(posi);
            dos.writeInt(posj);
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

    @Override
    public void parse(DataInputStream dis) {
    	
        try{
            int id = dis.readInt();
            if(id == this.client.getMe().getId()){
                return;
            }
            
            int posi = dis.readInt();
            int posj = dis.readInt();
            System.out.println("["+id+"]"+"move_msg_received: "+posi+" "+posj);
            client.getMe().reGeneratePlayer2(posi, posj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
