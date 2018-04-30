package feiqq.ui.recent;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import feiqq.bean.Group;
import feiqq.bean.User;
import feiqq.socket.client.Client;
import feiqq.ui.common.MyScrollBarUI;
import feiqq.ui.common.MyTreeUI;
import feiqq.util.PictureUtil;

public class RecentPanel extends JPanel {

	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;
	private JTree jTree;
	private JScrollPane jScrollPane;
	private static Color inColor = new Color(254, 224, 109); 
	private static Color selectColor = new Color(249, 184, 87);
	
	private Client selfClient;
	
	public RecentPanel(Client client) {
		BorderLayout borderLayout = new BorderLayout();
		this.setLayout(borderLayout);
		this.selfClient = client;
		root = new DefaultMutableTreeNode();
		model = new DefaultTreeModel(root);
		selfClient.setRecentRoot(root);
		selfClient.setRecentModel(model);
		
		RecentNode node0 = new RecentNode(PictureUtil.getPicture("avatar2.png"), new User("好友-101", "啊啊啊"));
		root.add(node0);
		
		
		jTree = new JTree(model);
		jTree.setUI(new MyTreeUI()); // 自定义UI
		jTree.setCellRenderer(new RecentNodeRenderer());// 自定义节点渲染器
		jTree.setRootVisible(false);// 隐藏根节点
		selfClient.setRecentTree(jTree);
		
		jScrollPane = new JScrollPane();
		jScrollPane.setBorder(null);
		jScrollPane.setViewportView(jTree);
		jScrollPane.getVerticalScrollBar().setUI(new MyScrollBarUI());
		jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		this.add(jScrollPane, BorderLayout.CENTER);
	}
	public void loadTree(User user) {
		if(!selfClient.recentNodeMap.containsKey(user.getId())) {
			RecentNode recentNode = new RecentNode(PictureUtil.getPicture("avatar2.png"), user);
			root.add(recentNode);
			model.reload();	
			selfClient.recentNodeMap.put(user.getId(), recentNode);
		}
	}
	public void loadTree(Group group) {
		if(!selfClient.recentNodeMap.containsKey(group.getId())) {
			RecentNode recentNode = new RecentNode(PictureUtil.getPicture("avatar2.png"), group);
			root.add(recentNode);
			model.reload();	
			selfClient.recentNodeMap.put(group.getId(), recentNode);
		}
	}
	
}
