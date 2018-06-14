package feiqq.ui.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import feiqq.bean.Message;
import feiqq.socket.client.Client;
import feiqq.util.Constants;
import feiqq.util.PictureUtil;

public class LoginingWindow extends JDialog {

	private static final long serialVersionUID = 1L;
	/** 主面板 */
	private JPanel content;
	/** 是否登陆 */
	private static boolean isLoginFlag = false;
	/** 取消登陆按钮 */
	private JLabel loginButton;
	/** Txt */
	private JLabel textLabel;
	/** 坐标（拖动记录） */
	private Point point = new Point();
	private Client client;
	private static LoginingWindow inst;
	
//	public static void main(String args[]) {
//		boolean falg = getInstance(true);
//	}
	
	public static void getInstance(Client client, String str) {
		
		inst = new LoginingWindow(client);
		inst.setLocationRelativeTo(null);
		inst.setVisible(true);
		client.setLoginingWindow(inst);
//		try {
//			Thread.sleep(3000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		client.sendMsg(new Message(Constants.LOGIN_MSG, str));
		
		
	}
	
	public LoginingWindow(Client client) {
		super();
		this.client = client;
		initGUI();
		initListener();
	}

	private void initGUI() {
		try {
			setSize(400, 300);
			setUndecorated(true);
//			TODO 这个API导致输入中文白屏
//			AWTUtilities.setWindowOpaque(this, false);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			
			// 主面板
			content = new JPanel() {
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(PictureUtil.getPicture("back32.jpg").getImage(), 0, 0, null);
					this.setOpaque(false);
				}
			};
			content.setLayout(null);
			content.setBorder(Constants.GRAY_BORDER);
			getContentPane().add(content, BorderLayout.CENTER);

//			textLabel = new JLabel("登陆中...");
//			content.add(textLabel);
//			textLabel.setFont(Constants.DIALOG_FONT2);
//			textLabel.setBounds(150, 100, 80, 50);
			
			// 取消登陆按钮
			loginButton = new JLabel("取消", JLabel.CENTER);
			content.add(loginButton);
			loginButton.setBounds(160, 200, 80, 40);
			loginButton.setFont(new Font("Dialog", 0, 21));
			loginButton.setForeground(Color.green);
			loginButton.setBorder(BorderFactory.createLineBorder(Color.white));
			
			//loginButton.setIcon(PictureUtil.getPicture("login_button.png"));
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	private void initListener() {
		// 主窗体事件
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				point.x = e.getX();
				point.y = e.getY();
			}
		});
		this.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Point p = getLocation();
				setLocation(p.x + e.getX() - point.x, p.y + e.getY() - point.y);
			}
		});
		
		
		// 登陆按钮事件
		loginButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				//loginButton.setIcon(PictureUtil.getPicture("login_button.png"));
				loginButton.setBorder(BorderFactory.createLineBorder(Color.white));
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				//loginButton.setIcon(PictureUtil.getPicture("login_active.png"));
				loginButton.setBorder(BorderFactory.createLineBorder(Color.green));
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				client.setLoginingWindow(null);
				client.setFalg(true);
				inst.dispose();
				
			}
		});
	}
	

}
