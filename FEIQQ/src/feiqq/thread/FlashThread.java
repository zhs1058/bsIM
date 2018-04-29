package feiqq.thread;

import java.awt.TrayIcon;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import feiqq.socket.client.Client;
import feiqq.ui.common.CategoryNode;
import feiqq.ui.friend.FriendNode;
import feiqq.ui.group.GroupNode;
import feiqq.util.PictureUtil;

public class FlashThread extends Thread {
	
	private Client client;
	private String senderName;
	
	public FlashThread(Client client, String senderName) {
		this.client = client;
		this.senderName = senderName;
	}
	
	public void run() {
		try {
			TreePath path = null;
			TrayIcon icon = null;
			CategoryNode parentNode = null;
			String parentName = null;
			DefaultTreeModel treeModel = null;
			GroupNode gNode = null;
			FriendNode node = client.buddyNodeMap.get(senderName);
			if(node == null) {
				treeModel = client.getBuddyModel();
				gNode = client.groupNodeMap.get(senderName);
				parentNode = (CategoryNode) gNode.getParent();
				path = new TreePath(parentNode.getPath());
				parentName = parentNode.nickName.getText();
				icon = client.getIcon();
			}else {
				treeModel = client.getGroupModel();
				parentNode = (CategoryNode) node.getParent();
				path = new TreePath(parentNode.getPath());
				parentName = parentNode.nickName.getText();
				icon = client.getIcon();
			}
			
			
			// TODO 这里有点儿别扭，以后记得改，好冗杂
			while (client.msgStatusMap.get(senderName)) {
				// 节点已展开就不再闪烁父节点
				if (client.getBuddyTree().isExpanded(path) || client.getGroupTree().isExpanded(path)) {
					if(node != null) {
						node.picture.setBounds(9, 5, 40, 43);
						icon.setImage(new ImageIcon("").getImage());
						Thread.sleep(600);
						treeModel.reload(node);
						
						node.picture.setBounds(8, 4, 39, 42);
						icon.setImage(PictureUtil.getPicture("qq_icon.png").getImage());
						Thread.sleep(600);
						treeModel.reload(node);
					}else {
						gNode.picture.setBounds(1, 1, 41, 41);
						icon.setImage(new ImageIcon("").getImage());
						Thread.sleep(600);
						treeModel.reload(gNode);
						
						gNode.picture.setBounds(0, 0, 40, 40);
						icon.setImage(PictureUtil.getPicture("qq_icon.png").getImage());
						Thread.sleep(600);
						treeModel.reload(gNode);
					}
					
				} else {
					parentNode.nickName.setText("");
					icon.setImage(new ImageIcon("").getImage());
					Thread.sleep(600);
					treeModel.reload(parentNode);
					
					parentNode.nickName.setText(parentName);
					icon.setImage(PictureUtil.getPicture("qq_icon.png").getImage());
					Thread.sleep(600);
					treeModel.reload(parentNode);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
