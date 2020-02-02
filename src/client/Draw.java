package client;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Msg.MoveMsg;
import Msg.WinMsg;
import server.Server;

public class Draw extends JFrame{
	public static final int GAME_WIDTH = 1715;
    public static final int GAME_HEIGHT = 845;
    
    static Client client;
    static int id;
    static boolean win = false;
    static Player ply = new Player();
    
	static JFrame frame = new JFrame("test");
	static JPanel mainScreen=new JPanel();
	
	static JLabel player, map_label, bound, map2, player2;
	
	static ImageIcon player_img = new ImageIcon("img\\player.gif");
	static ImageIcon map_img = new ImageIcon("img\\map.png");
	static ImageIcon bound_img = new ImageIcon("img\\bound.png");
    
	//0:grass
	//1:tree
	//end:i=1, j=18
	//begin:i=18, j=1
	private static final int[][] map = new int[][] { //20*20
		{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
		{1,0,0,0,0,1,0,0,0,0,0,0,0,0,1,0,1,0,0,1},
		{1,1,0,1,0,1,0,0,1,1,1,1,1,1,1,0,1,0,0,1},
		{1,0,0,1,0,1,0,0,0,0,0,0,0,0,1,0,1,0,0,1},
		{1,0,1,1,1,1,1,1,1,1,1,0,1,0,1,0,1,0,0,1},
		{1,0,0,0,0,1,0,0,0,0,0,0,1,0,1,0,1,0,0,1},
		{1,1,1,0,0,1,0,1,1,1,1,1,1,0,1,0,1,1,0,1},
		{1,0,0,0,0,1,0,0,0,0,0,0,0,0,1,0,0,1,0,1},
		{1,1,1,1,0,1,1,1,1,1,1,1,1,0,1,1,0,0,0,1},
		{1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,1,1,1},
		{1,0,1,0,0,1,0,1,0,0,1,0,0,1,0,1,0,0,0,1},
		{1,1,1,0,1,1,0,1,1,1,1,1,0,1,0,1,1,1,0,1},
		{1,0,0,0,0,0,0,0,0,0,0,1,0,1,0,1,0,0,0,1},
		{1,0,0,1,1,1,1,1,1,0,0,1,0,1,0,1,0,1,0,1},
		{1,0,0,1,0,0,0,0,0,0,0,1,0,1,0,1,0,1,0,1},
		{1,0,0,1,0,1,1,1,1,1,1,1,0,1,0,1,1,1,0,1},
		{1,0,0,1,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,1},
		{1,0,0,1,0,1,0,0,1,1,1,1,1,1,0,0,1,0,0,1},
		{1,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,1},
		{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
	};
	
	public Draw(Client client){
		this.client = client;
		generateMap(18, 1);
	}
	
	public static void generateMap(int player_posi, int player_posj) {
    	frame.setBounds(100,100,GAME_WIDTH, GAME_HEIGHT);
        mainScreen.setLayout(null);
        player = new JLabel();
		player.setBounds(player_posi*40, player_posj*40, 40, 40);
		setIcon(player_img, player);
		mainScreen.add(player);
        map_label = new JLabel();
        map_label.setBounds(0, 0, 800, 800);
        setIcon(map_img, map_label);
        mainScreen.add(map_label);
        bound = new JLabel();
        bound.setBounds(800, 0, 100, 800);
        setIcon(bound_img, bound);
        mainScreen.add(bound);
        
		frame.add(ply);
		frame.addKeyListener(ply);
    	frame.add(mainScreen);
    	frame.setVisible(true);
    	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	//拉伸图像至指定大小
	public static void setIcon(ImageIcon ico, JLabel com)
	{
		  ico.getImage();
		  Image temp=ico.getImage().getScaledInstance(com.getWidth(),com.getHeight(),Image.SCALE_DEFAULT);  
		  ico=new ImageIcon(temp);  
		  com.setIcon(ico);  
	}
	
	public static void reGenerate(int posi, int posj) {
		if(posi==1 && posj==18) {
			win = true;
			WinMsg msg = new WinMsg(id);
			client.getNc().send(msg);
			JOptionPane.showMessageDialog(frame, "YOU WIN!!", "Message", JOptionPane.INFORMATION_MESSAGE);
		}
		
		player.setLocation(posi*40, posj*40);
		setIcon(player_img, player);
		mainScreen.remove(map_label);
		mainScreen.add(map_label);
		
		MoveMsg msg = new MoveMsg(id, posi, posj);
		client.getNc().send(msg);
	}
	
	public static int getBlock(int i, int j) {
		return map[i][j];
	}
	
	public static void generatePlayer2(int posi, int posj) {
		player2 = new JLabel();
		player2.setBounds(900+posi*40, posj*40, 40, 40);
		setIcon(player_img, player2);
		mainScreen.add(player2);
        map2 = new JLabel();
        map2.setBounds(900, 0, 800, 800);
        setIcon(map_img, map2);
        mainScreen.add(map2);
	}
	
	public static void reGeneratePlayer2(int posi, int posj) {
		player2.setLocation(900+posi*40, posj*40);
		setIcon(player_img, player2);
		mainScreen.remove(map2);
		mainScreen.add(map2);
	}
	
	public static void lose() {
		JOptionPane.showMessageDialog(frame, "YOU LOSE!!", "Message", JOptionPane.INFORMATION_MESSAGE);
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}
