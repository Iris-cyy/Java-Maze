package client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class Player extends JPanel implements KeyListener {
	private static int posi;
	private static int posj;
	private static boolean win = false;
	
	Player(){
		super();
		posi = 18;
		posj = 1;
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		int key = e.getKeyCode();
		switch(key) {
		case KeyEvent.VK_UP:
			if(Draw.getBlock(posi, posj-1) != 1) {
				posj--;
			}
			break;
		case KeyEvent.VK_DOWN:
			if(Draw.getBlock(posi, posj+1) != 1) {
				posj++;
			}
			break;
		case KeyEvent.VK_LEFT:
			if(Draw.getBlock(posi-1, posj) != 1) {
				posi--;
			}
			break;
		case KeyEvent.VK_RIGHT:
			if(Draw.getBlock(posi+1, posj) != 1) {
				posi++;
			}
			break;
		default:
			break;
		}
		Draw.reGenerate(posi, posj);
	}		
	
	@Override
	public void keyReleased(KeyEvent e){}
	
	@Override
	public void keyTyped(KeyEvent e) {
			
	}
}
