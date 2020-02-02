package Msg;

import java.io.DataInputStream;
import java.net.DatagramSocket;

public interface Msg {
	public static final int CONNECT_MSG = 1;
	public static final int MOVE_MSG = 2;
	public static final int WIN_MSG = 3;
	public static final int CONNECT_TO_ORI_MSG = 4;

    public void send(DatagramSocket ds, String IP, int UDP_Port);
    public void parse(DataInputStream dis);
}
