package feiqq.ui.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import feiqq.util.Constants;
import feiqq.util.PictureUtil;

/**
 * 自定义弹出框，基本都是模态窗体 <br/>
 * 可以修正参数modal来设置 <br/>
 * 也可以扩展加入icon图标 <br/>
 * 后期还可以用来做新闻提示，类似于右下角QQ新闻 <br/>
 * @author Administrator
 *
 */
// 嘿嘿，虽然有些丑额，不想优化了。
public class MyOptionPane {

	private static int result = 0;

	/**
	 * 消息框
	 * @param owner 父窗体
	 * @param title 标题
	 * @param content 内容
	 */
	public static void showMessageDialog(Component owner, String content, String title) {
		JDialog dialog = createMessageDialog(owner, title, content);
		if (null == owner) {
			dialog.setLocationRelativeTo(null);
		} else {
			dialog.setLocationRelativeTo(owner);
		}
		dialog.setVisible(true);
	}

	/**
	 * 确认框
	 * @param owner 父窗体
	 * @param title 标题
	 * @param content 内容
	 * @return 0：确定 1：取消
	 */
	public static int showConfirmDialog(Component owner, String title, 
			String content, String okButtonStr, String quitButtonStr) {
		JDialog dialog = createConfirmDialog(owner, title, content, okButtonStr, quitButtonStr);
		if (null == owner) {
			dialog.setLocationRelativeTo(null);
		} else {
			dialog.setLocationRelativeTo(owner);
		}
		dialog.setVisible(true);
		return result;
	}

	private static JDialog createConfirmDialog(Component owner, String title, 
			String content, String okButtonStr, String quitButtonStr) {
		final Point point = new Point();

		// 这里可以修改modal是否模态窗体，但是想想提示框就算了吧
		// final JDialog dialog = new JDialog(owner, title, modal);
		final JDialog dialog = getDialog(owner, title);
		dialog.setUndecorated(true);
		dialog.setSize(320, 170);
		dialog.getContentPane().setLayout(null);

		JPanel topPane = new JPanel();
		topPane.setLayout(null);
		dialog.getContentPane().add(topPane);
		topPane.setBounds(0, 0, 320, 31);
		topPane.setBackground(Color.CYAN);
		topPane.setBorder(Constants.LIGHT_GRAY_BORDER);

		JLabel titleLabel = new JLabel(title);
		titleLabel.setBounds(10, 0, 281, 25);
		topPane.add(titleLabel);
		titleLabel.setFont(Constants.BASIC_FONT);

		final JLabel exitButton = new JLabel();
		exitButton.setBounds(281, 0, 40, 20);
		exitButton.setIcon(PictureUtil.getPicture("close.png"));
		topPane.add(exitButton);

		JPanel downPane = new JPanel();
		downPane.setLayout(null);
		downPane.setBackground(Color.WHITE);
		dialog.getContentPane().add(downPane);
		downPane.setBounds(0, 30, 320, 140);
		downPane.setBorder(Constants.LIGHT_GRAY_BORDER);

		// TODO 这个地方没太考虑好，不知道用啥来显示合适
		// jlabel第一选择，但是他似乎不能换行
		// 自己写算法扩展宽度什么的太麻烦了，偷下懒
		JTextArea text = new JTextArea(content);
		text.setFont(Constants.DIALOG_FONT);// 字体
		text.setEditable(false);// 不允许编辑
		text.setLineWrap(true);// 激活自动换行功能
		text.setWrapStyleWord(true);// 激活断行不断字功能
		text.setBounds(48, 25, 220, 70);
		text.setOpaque(false);
		downPane.add(text);

		JPanel interiorPane = new JPanel();
		interiorPane.setBounds(1, 100, 318, 37);
		downPane.add(interiorPane);
		interiorPane.setBackground(Color.WHITE);
		interiorPane.setLayout(null);

		final JLabel okButton = new JLabel(okButtonStr, JLabel.CENTER);
		okButton.setBounds(130, 10, 93, 25);
		okButton.setFont(Constants.BASIC_FONT);
		interiorPane.add(okButton);

		final JLabel quitButton = new JLabel(quitButtonStr, JLabel.CENTER);
		quitButton.setBounds(217, 10, 93, 25);
		quitButton.setFont(Constants.BASIC_FONT);
		interiorPane.add(quitButton);

		dialog.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				point.x = e.getX();
				point.y = e.getY();
			}
		});
		dialog.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Point p = dialog.getLocation();
				dialog.setLocation(p.x + e.getX() - point.x, p.y + e.getY() - point.y);
			}
		});
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
				dialog.dispose();
				result = 1;
			}
		});
		okButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				okButton.setBorder(Constants.LIGHT_GRAY_BORDER);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				okButton.setBorder(null);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				dialog.dispose();
				result = 0;
			}
		});
		quitButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				quitButton.setBorder(Constants.LIGHT_GRAY_BORDER);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				quitButton.setBorder(null);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				dialog.dispose();
				result = 1;
			}
		});

		return dialog;
	}

	private static JDialog createMessageDialog(Component owner, String title, String content) {
		final Point point = new Point();
		
		final JDialog dialog = getDialog(owner, title);
		dialog.setUndecorated(true);
		dialog.setSize(320, 170);
		dialog.setLayout(null);

		JPanel topPane = new JPanel();
		topPane.setLayout(null);
		dialog.add(topPane);
		topPane.setBounds(0, 0, 320, 31);
		topPane.setBackground(Color.CYAN);
		topPane.setBorder(Constants.LIGHT_GRAY_BORDER);

		final JLabel titleLabel = new JLabel(title);
		titleLabel.setBounds(10, 0, 281, 25);
		topPane.add(titleLabel);
		titleLabel.setFont(Constants.BASIC_FONT);

		final JLabel exitButton = new JLabel();
		exitButton.setBounds(281, 0, 40, 20);
		exitButton.setIcon(PictureUtil.getPicture("close.png"));
		topPane.add(exitButton);

		JPanel downPane = new JPanel();
		downPane.setLayout(null);
		downPane.setBackground(Color.WHITE);
		dialog.add(downPane);
		downPane.setBounds(0, 30, 320, 139);
		downPane.setBorder(Constants.LIGHT_GRAY_BORDER);

		JTextArea text = new JTextArea(content);
		text.setFont(Constants.DIALOG_FONT);// 字体
		text.setEditable(false);// 不允许编辑
		text.setLineWrap(true);// 激活自动换行功能
		text.setWrapStyleWord(true);// 激活断行不断字功能
		text.setBounds(51, 47, 215, 42);
		text.setOpaque(false);
		downPane.add(text);
		
		final JLabel okButton = new JLabel("确定", JLabel.CENTER);
		okButton.setBounds(115, 106, 93, 25);
		okButton.setFont(Constants.BASIC_FONT);
		downPane.add(okButton);

		dialog.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				point.x = e.getX();
				point.y = e.getY();
			}
		});
		dialog.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Point p = dialog.getLocation();
				dialog.setLocation(p.x + e.getX() - point.x, p.y + e.getY() - point.y);
			}
		});
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
				dialog.dispose();
			}
		});
		okButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				okButton.setBorder(Constants.LIGHT_GRAY_BORDER);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				okButton.setBorder(null);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				dialog.dispose();
			}
		});

		return dialog;
	}
	
	private static JDialog getDialog(Component owner, String title) {
		JDialog dialog = null;
		if (owner instanceof JFrame) {
			dialog = new JDialog((JFrame)owner, title, true);
		}
		if (owner instanceof JDialog) {
			dialog = new JDialog((JDialog)owner, title, true);
		} 
		if (null == owner) {
			dialog = new JDialog();
			dialog.setTitle(title);
			dialog.setLocationRelativeTo(null);
		}
		return dialog;
	}

}
