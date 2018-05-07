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
import feiqq.util.MailUtil;
import feiqq.util.PictureUtil;
import feiqq.util.StringRandomUtil;
import feiqq.util.StringUtil;
import feiqq.util.ValidateUtil;

public class FindPasswordWindow extends JDialog{
	private static final long serialVersionUID = 1L;
	
	/** 主面板 */
	private JPanel content;
	/** 退出按钮 */
	private JLabel exitButton;
	
	private JLabel okButton;
	private JLabel quitButton;
	private JLabel CodeButton;
	private String TestCoding;
	private int sendCount = 0;
	private long curTime;
	private long preTime;
	private long differenceTime;

	/** 账号Txt */
	private JLabel userNameLabel;
	/** 验证码  */
	private JLabel testCodeLabel;

	/** 账号 */
	private JTextField userNameField;
	/** 验证码 */
	private JTextField testCodeField;
	private JScrollPane signatureScroll;
	
	private Point point = new Point();
	private Client client;
	
	public static FindPasswordWindow getInstance(Client client) {
		FindPasswordWindow inst = new FindPasswordWindow(client);
		inst.setLocationRelativeTo(null);
		inst.setVisible(true);
		return inst;
	}
	
	public FindPasswordWindow(Client client) {
		super();
		initGUI();
		initListener();
		this.client = client;
	}
	
	private void initGUI() {
		try {
			setSize(407, 250);
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
			exitButton.setBounds(368, 0, 40, 20);
			exitButton.setIcon(PictureUtil.getPicture("close.png"));

			userNameLabel = new JLabel("账     号");
			content.add(userNameLabel);
			userNameLabel.setFont(Constants.BASIC_FONT);
			userNameLabel.setBounds(30, 63, 70, 20);
			
			userNameField = new JTextField();
			content.add(userNameField);
			userNameField.setBounds(110, 60, 240, 28);
			userNameField.setBorder(Constants.LIGHT_GRAY_BORDER);
			userNameField.setText("电子邮件");
			
			testCodeLabel = new JLabel("验  证  码");
			content.add(testCodeLabel);
			testCodeLabel.setFont(Constants.BASIC_FONT);
			testCodeLabel.setBounds(30, 133, 70, 20);

			testCodeField = new JPasswordField();
			content.add(testCodeField);
			testCodeField.setBounds(110, 130, 240, 28);
			testCodeField.setBorder(Constants.LIGHT_GRAY_BORDER);
			
			CodeButton = new JLabel("获取", JLabel.CENTER);
			content.add(CodeButton);
			CodeButton.setFont(Constants.BASIC_FONT);
			CodeButton.setBorder(null);
			CodeButton.setBounds(350, 133, 40, 20);
			CodeButton.setOpaque(false);
			CodeButton.setBackground(new Color(240, 245, 240, 60));
		
			okButton = new JLabel("确定", JLabel.CENTER);
			content.add(okButton);
			okButton.setFont(Constants.BASIC_FONT);
			okButton.setBorder(null);
			okButton.setBounds(110, 190, 80, 30);
			okButton.setOpaque(false);
			okButton.setBackground(new Color(240, 245, 240, 60));
		
			quitButton = new JLabel("取消", JLabel.CENTER);
			content.add(quitButton);
			quitButton.setFont(Constants.BASIC_FONT);
			quitButton.setBorder(null);
			quitButton.setBounds(220, 190, 80, 30);
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
				findPass();
			}
		});
		//获取验证码事件
		CodeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				CodeButton.setBorder(null);
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				CodeButton.setBorder(Constants.GRAY_BORDER);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				String name = userNameField.getText();
				if (StringUtil.isEmpty(name)) {
					MyOptionPane.showMessageDialog(client.getRegister(), "没有输入账号！", "友情提示", Constants.NOTICE);
					return;
				}
				if (!ValidateUtil.isMail(name)) {
					MyOptionPane.showMessageDialog(client.getRegister(), "请检查邮件格式！", "友情提示", Constants.NOTICE);
					return;
				}
				if(sendCount > 2) {
					MyOptionPane.showMessageDialog(client.getRegister(), "验证码不能重复发送超过2次！", "友情提示", Constants.NOTICE);
					return;
				}
				TestCoding = StringRandomUtil.getStringRandom();
				try {
					MailUtil.sendMail(name, TestCoding);
					sendCount ++ ;
					MyOptionPane.showMessageDialog(client.getRegister(), "验证码发送成功！", "友情提示", Constants.NOTICE);
					return;
				} catch (MessagingException e1) {
					e1.printStackTrace();
				}
				
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
				client.setFindPasswordWindow(null);
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
				client.setFindPasswordWindow(null);
			}
		});
		
		userNameField.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				userNameField.setBorder(Constants.LIGHT_GRAY_BORDER);
				if(userNameField.getText() == null || userNameField.getText().equals("")) {
					userNameField.setText("电子邮件");
				}
			}
			@Override
			public void focusGained(FocusEvent e) {
				userNameField.setBorder(Constants.ORANGE_BORDER);
				if("电子邮件".equals(userNameField.getText())) {
					userNameField.setText("");
				}
			}
		});
		
	}
	
	// 找回密码
	private void findPass() {
		String name = userNameField.getText();
		String testCode = testCodeField.getText();
		
		if (StringUtil.isEmpty(testCode)) {
			MyOptionPane.showMessageDialog(client.getRegister(), "没有输入验证码！", "友情提示", Constants.NOTICE);
			return;
		}
		if (StringUtil.isEmpty(name)) {
			MyOptionPane.showMessageDialog(client.getRegister(), "没有输入账号！", "友情提示", Constants.NOTICE);
			return;
		}
		if (!ValidateUtil.isMail(name)) {
			MyOptionPane.showMessageDialog(client.getRegister(), "请检查邮件格式！", "友情提示", Constants.NOTICE);
			return;
		}
		if(!TestCoding.equals(testCode)) {
			MyOptionPane.showMessageDialog(client.getRegister(), "验证码错误，请注意大小写一致！", "友情提示", Constants.NOTICE);
			return;
		}
		// 登录服务器
		//String str =  name ;
		client.sendMsg(new Message(Constants.FIND_PASSWORD, name));
	}
	

}
