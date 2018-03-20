package feiqq.ui.common;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import feiqq.socket.client.Client;
import feiqq.ui.frame.ChatRoom;
import feiqq.ui.frame.ChatRoomPanel;
import feiqq.util.Constants;
import feiqq.util.PictureUtil;

public class MyTabComponent extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JLabel text;
	private JLabel icon;
	private String selfName;
	private String friendName;
	private ChatRoom room;
	private Client client;
	
	public MyTabComponent(String selfName, String friendName, ChatRoom room, Client client) {
		this.room = room;
		this.client = client;
		this.selfName = selfName;
		this.friendName = friendName;
		initGUI();
		initListener();
	}
	
	private void initGUI() {
		setOpaque(false);
		setLayout(new FlowLayout());
		text = new JLabel(friendName);
		text.setFont(Constants.BASIC_FONT);
		icon = new JLabel(PictureUtil.getPicture("empty.png"));
		add(text);
		add(icon);
	}

	private void initListener() {
		// TODO 哎呀，这个地方，我自己的组件竟然将原始组件的切换方法给冲掉了
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				icon.setIcon(PictureUtil.getPicture("empty.png"));
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				icon.setIcon(PictureUtil.getPicture("close_tab.png"));
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				int index = room.tabbedPane.indexOfTab(friendName);
				room.tabbedPane.setSelectedIndex(index);
				
				room.setTitle(selfName + " - " + friendName);
				room.titleLabel.setText(selfName + " - " + friendName);
			}
		});
		icon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				icon.setIcon(PictureUtil.getPicture("empty.png"));
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				icon.setIcon(PictureUtil.getPicture("close_tab_active.png"));
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				int index = room.tabbedPane.indexOfTab(friendName);
				room.tabbedPane.remove(room.tabbedPane.getComponentAt(index));
				// TODO 将map记录里面的移除
				client.tabMap.remove(friendName);
				// 被关闭完了
				if (client.tabMap.size() < 1) {
					room.dispose();
					// TODO 清空记录窗体数据
					client.setRoom(null);
					client.tabMap.clear();
				} else {
					ChatRoomPanel pane = (ChatRoomPanel) room.tabbedPane.getSelectedComponent();
					room.setTitle(pane.self.getNickName() + " - " + pane.friend.getNickName());
					room.titleLabel.setText(pane.self.getNickName() + " - " + pane.friend.getNickName());
				}
			}
		});
	}
	
}
