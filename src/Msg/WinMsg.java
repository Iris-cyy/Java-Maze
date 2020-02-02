package Msg;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import client.Client;
import client.Draw;

public class WinMsg implements Msg {
    private int msgType = Msg.WIN_MSG;
    private int id;
    private Client client;
	
    public WinMsg(Client client){
        this.client = client;
    }
    
    public WinMsg(int id) {
    	this.id = id;
    }

	@Override
	public void send(DatagramSocket ds, String IP, int UDP_Port) {
		System.out.println("move_win_sent");
		ByteArrayOutputStream baos = new ByteArrayOutputStream(88);
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(msgType);
            dos.writeInt(id);

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
            System.out.println("win_msg_received");
            client.getMe().lose();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
