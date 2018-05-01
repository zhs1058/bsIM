package feiqq.ui.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.mail.MessagingException;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import feiqq.bean.Message;
import feiqq.socket.client.Client;
import feiqq.ui.common.MyOptionPane;
import feiqq.ui.common.MyScrollBarUI;
import feiqq.util.Constants;
import feiqq.util.PictureUtil;

public class ChangeInfoWindow extends JDialog {

	/** 主面板 */
	private JPanel content;
	/** 退出按钮 */
	private JLabel exitButton;
	
	private JLabel okButton;
	private JLabel quitButton;
	/** 原密码Txt */
	private JLabel prePassLabel;
	/** 新密码Txt */
	private JLabel curPassLabel;
	/** 确认密码Txt */
	private JLabel confirmPassLabel;


	/** 原密码 */
	private JPasswordField prePassField;
	/** 新密码 */
	private JPasswordField curPassField;
	/** 确认密码 */
	private JPasswordField confirmPassField;
	
	private JScrollPane signatureScroll;
	
	private Point point = new Point();
	private Client client;
	public String infoStaitc;
	
	public static ChangeInfoWindow getInstance(Client client) {
		ChangeInfoWindow inst = new ChangeInfoWindow(client);
		inst.setLocationRelativeTo(null);
		inst.setVisible(true);
		return inst;
	}
	
	public ChangeInfoWindow(Client client) {
		super();
		this.client = client;
		initGUI();
		initListener();
		
	}
	
	private void initGUI() {
		try {
			setSize(400, 250);
			setUndecorated(true);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			
			content = new JPanel() {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(PictureUtil.getPicture("back7.jpg").getImage(), 0, 0, null);
					this.setOpaque(false);
				}
			};
			content.setLayout(null);
			getContentPane().add(content, BorderLayout.CENTER);
			content.setBorder(Constants.GRAY_BORDER);
		
			exitButton = new JLabel();
			content.add(exitButton);
			exitButton.setBounds(360, 0, 40, 20);
			exitButton.setIcon(PictureUtil.getPicture("close.png"));

			prePassLabel = new JLabel("初始密码：");
			content.add(prePassLabel);
			prePassLabel.setFont(Constants.BASIC_FONT);
			prePassLabel.setBounds(30, 33, 70, 20);
			
			prePassField = new JPasswordField();
			content.add(prePassField);
			prePassField.setBounds(110, 30, 240, 28);
			prePassField.setBorder(Constants.LIGHT_GRAY_BORDER);
			//nickNameField.setText(client.getUser().getNickName());
			
			
			curPassLabel = new JLabel("当前密码");
			content.add(curPassLabel);
			curPassLabel.setFont(Constants.BASIC_FONT);
			curPassLabel.setBounds(30, 73, 70, 20);
			
			curPassField = new JPasswordField();
			content.add(curPassField);
			curPassField.setBounds(110, 70, 240, 28);
			curPassField.setBorder(Constants.LIGHT_GRAY_BORDER);
			//passWordField.setText("***********");
			
		
			confirmPassLabel = new JLabel("确认密码");
			content.add(confirmPassLabel);
			confirmPassLabel.setFont(Constants.BASIC_FONT);
			confirmPassLabel.setBounds(30, 113, 70, 20);
			
			confirmPassField = new JPasswordField();
			content.add(confirmPassField);
			confirmPassField.setBounds(110, 110, 240, 28);
			confirmPassField.setBorder(Constants.LIGHT_GRAY_BORDER);
		
		
			okButton = new JLabel("确定", JLabel.CENTER);
			content.add(okButton);
			okButton.setFont(Constants.BASIC_FONT);
			okButton.setBorder(null);
			okButton.setBounds(110, 170, 80, 30);
			okButton.setOpaque(false);
			okButton.setBackground(new Color(240, 245, 240, 60));
		
			quitButton = new JLabel("取消", JLabel.CENTER);
			content.add(quitButton);
			quitButton.setFont(Constants.BASIC_FONT);
			quitButton.setBorder(null);
			quitButton.setBounds(220, 170, 80, 30);
			quitButton.setOpaque(false);
			quitButton.setBackground(new Color(240, 245, 240, 60));
		} catch (Exception e) {
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
		// 确定按钮事件
		okButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				okButton.setBorder(null);
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				okButton.setBorder(Constants.GRAY_BORDER);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				register();
			}
		});
		
		// 取消按钮事件
		quitButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				quitButton.setBorder(null);
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				quitButton.setBorder(Constants.GRAY_BORDER);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				dispose();
				client.setRegister(null);
			}
		});
		// 退出按钮事件
		exitButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				exitButton.setIcon(PictureUtil.getPicture("close.png"));
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				exitButton.setIcon(PictureUtil.getPicture("close_active.png"));
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				dispose();
				client.setChangeInfoWindow(null);
			}
		});
		// 输入框焦点事件
		prePassField.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				prePassField.setBorder(Constants.LIGHT_GRAY_BORDER);
			}
			@Override
			public void focusGained(FocusEvent e) {
				prePassField.setBorder(Constants.ORANGE_BORDER);
			}
		});
		
		curPassField.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				curPassField.setBorder(Constants.LIGHT_GRAY_BORDER);
			}
			@Override
			public void focusGained(FocusEvent e) {
				curPassField.setBorder(Constants.ORANGE_BORDER);
			}
		});
		
		confirmPassField.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				confirmPassField.setBorder(Constants.LIGHT_GRAY_BORDER);
			}
			@Override
			public void focusGained(FocusEvent e) {
				confirmPassField.setBorder(Constants.ORANGE_BORDER);
			}
		});
	}
	
	// 注册
	private void register() {
		String pre = String.valueOf(prePassField.getPassword());
		String cur = String.valueOf(curPassField.getPassword());
		String con = String.valueOf(confirmPassField.getPassword());
		// 验证
		if (cur.length() > 10 || con.length() > 10) {
			MyOptionPane.showMessageDialog(client.getRegister(), "密码长度不可以超过10位！", "友情提示");
			return;
		}
		if (!cur.equals(con)) {
			MyOptionPane.showMessageDialog(client.getRegister(), "密码前后不一致！", "友情提示");
			return;
		}
		String str = pre + Constants.LEFT_SLASH + cur + Constants.LEFT_SLASH + client.getUser().getId();
		client.sendMsg(new Message(Constants.CHANGE_PASSWORD, str));
		
//		client.getMain().dispose();
//		client.setMain(null);
	}
	
}
