package client;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;



public class Client extends Frame {
	private static Draw me;
	private netClient nc = new netClient(this);
    private ConDialog dialog = new ConDialog();
    private UDPPortWrongDialog udpPortWrongDialog = new UDPPortWrongDialog();
    private ServerNotStartDialog serverNotStartDialog = new ServerNotStartDialog();

	public static void main(String[] args) {
		Client client = new Client();
		me = new Draw(client);
		client.launch();
	}
	
	public Draw getMe() {
		return me;
	}
	
	public void launch() {
		this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                nc.sendClientDisconnectMsg();//�رմ���ǰҪ�����������ע����Ϣ.
                System.exit(0);
            }
        });
		dialog.setVisible(true);
	}
	
	
	/**
     * ��Ϸ��ʼǰ���ӵ��������ĶԻ���
     */
    class ConDialog extends Dialog{
        Button b = new Button("connect to server");
        TextField serverIP = new TextField("127.0.0.1", 15);//��������IP��ַ
        TextField userName = new TextField("", 8);

        public ConDialog() {
            super(Client.this, true);
            this.setLayout(new FlowLayout());
            this.add(new Label("server IP:"));
            this.add(serverIP);
            this.add(new Label("user name:"));
            this.add(userName);
            this.add(b);
            this.setLocation(500, 400);
            this.pack();
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    setVisible(false);
                    System.exit(0);
                }
            });
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String IP = serverIP.getText().trim();
                    nc.connect(IP);
                    setVisible(false);
                }
            });
        }
    }


    /**
     * UDP�˿ڷ���ʧ�ܺ�ĶԻ���
     */
    class UDPPortWrongDialog extends Dialog{
        Button b = new Button("ok");
        public UDPPortWrongDialog() {
            super(Client.this, true);
            this.setLayout(new FlowLayout());
            this.add(new Label("something wrong, please connect again"));
            this.add(b);
            this.setLocation(500, 400);
            this.pack();
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
        }
    }

    /**
     * ���ӷ�����ʧ�ܺ�ĶԻ���
     */
    class ServerNotStartDialog extends Dialog{
        Button b = new Button("ok");
        public ServerNotStartDialog() {
            super(Client.this, true);
            this.setLayout(new FlowLayout());
            this.add(new Label("The server has not been opened yet..."));
            this.add(b);
            this.setLocation(500, 400);
            this.pack();
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
        }
    }
    public netClient getNc() {
        return nc;
    }

    public void setNc(netClient nc) {
        this.nc = nc;
    }

    public UDPPortWrongDialog getUdpPortWrongDialog() {
        return udpPortWrongDialog;
    }

    public ServerNotStartDialog getServerNotStartDialog() {
        return serverNotStartDialog;
    }

}
