package feiqq.ui.recent;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

import feiqq.bean.Group;
import feiqq.bean.User;

public class RecentNode extends DefaultMutableTreeNode {

	public Icon icon;
	public User friend;
	public Group group;
	public JLabel picture;
	public JLabel nickName;
	public JLabel descript;
	public JPanel userContent = new JPanel();

	public RecentNode(Icon icon, User friend) {
		super();
		this.icon = icon;
		this.friend = friend;
		initGUI();
	}
	
	public RecentNode(Icon icon, Group group) {
		super();
		this.icon = icon;
		this.group = group;
		initGUI();
	}
	public void initGUI() {
		userContent.setLayout(null);
		userContent.setBackground(Color.WHITE);
		userContent.setPreferredSize(new Dimension(300, 50));

		picture = new JLabel();
		userContent.add(picture);
		picture.setIcon(icon);
		picture.setBounds(8, 4, 39, 42);

		nickName = new JLabel();
		userContent.add(nickName);
		if(friend != null) {
			nickName.setText(friend.getNickName());
		}else {
			nickName.setText(group.getName());
		}
		
		nickName.setBounds(59, 5, 132, 19);

		descript = new JLabel();
		userContent.add(descript);
		if(friend != null) {
			descript.setText(friend.getSignature());
			descript.setBounds(59, 28, 132, 17);
		}
		
	}

	public Component getView() {
		return userContent;
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public User getFriend() {
		return friend;
	}

	public void setFriend(User friend) {
		this.friend = friend;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}
	

}
