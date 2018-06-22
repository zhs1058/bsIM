package feiqq.ui.recent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import feiqq.bean.Group;
import feiqq.bean.Message;
import feiqq.bean.User;
import feiqq.socket.client.Client;
import feiqq.ui.common.CategoryNode;
import feiqq.ui.common.Emoticon;
import feiqq.ui.common.MyOptionPane;
import feiqq.ui.common.MyScrollBarUI;
import feiqq.ui.common.MyTabComponent;
import feiqq.ui.common.MyTreeUI;
import feiqq.ui.frame.AddGroupWindow;
import feiqq.ui.frame.ChatRoom;
import feiqq.ui.frame.ChatRoomPanel;
import feiqq.ui.frame.CreateGroupWindow;
import feiqq.ui.group.GroupNode;
import feiqq.util.Constants;
import feiqq.util.PictureUtil;
import feiqq.util.StringUtil;

public class RecentPanel extends JPanel {

	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;
	private JTree jTree;
	private JScrollPane jScrollPane;
	private static Color inColor = new Color(173, 216, 230); 
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
		
//		RecentNode node0 = new RecentNode(PictureUtil.getPicture("avatar2.png"), new User("好友-101", "啊啊啊"));
//		root.add(node0);
		
		
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
		
		//监听鼠标位置  以改变鼠标悬浮块颜色
		jTree.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				TreePath path = jTree.getPathForLocation(e.getX(), e.getY());
				if (null != path) {
					Object object = path.getLastPathComponent();
					if (object instanceof RecentNode) {
						for (int i = 0; i < root.getChildCount(); i++) {
							Object recent = root.getChildAt(i);
							if (((RecentNode)recent).userContent.getBackground() != selectColor) {
								if (recent == ((RecentNode)object)) {
									((RecentNode)recent).userContent.setBackground(inColor);
								} else {
									((RecentNode)recent).userContent.setBackground(Color.WHITE);
								}
								model.reload(((RecentNode)recent));
							}
						}
					}
				}
			}
		});
		
		jTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				for (int i = 0; i < root.getChildCount(); i++) {
					Object recent = root.getChildAt(i);
					((RecentNode)recent).userContent.setBackground(Color.WHITE);
					model.reload(((RecentNode)recent));
					
				}
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// 监听鼠标左键
				if (e.getButton() == MouseEvent.BUTTON1) {
					TreePath path = jTree.getSelectionPath();
					if (null == path) {
						return;
					}
					// path中的node节点（path不为空，这里基本不会空）
					Object object = path.getLastPathComponent();
//					if (e.getClickCount() == 2) {
//						// 好友node节点（区分群组）
//						if (object instanceof RecentNode) {
//							for (int i = 0; i < root.getChildCount(); i++) {
//								Object recent = root.getChildAt(i);
//								// 不是鼠标选中的那个
//								if (recent != ((RecentNode)object)) {
//									((RecentNode)recent).userContent.setBackground(Color.WHITE);
//								} else {
//									((RecentNode)object).userContent.setBackground(selectColor);
//									// 开启聊天窗口
//									Message message = null;
//									String name = null;
//									User user = null;
//									Group group = null;
//									if(((RecentNode)object).friend != null) {
//										user = ((RecentNode)object).friend;
//										name = ((RecentNode)object).friend.getNickName();
//									}else {
//										group = ((RecentNode)object).group;
//										name = ((RecentNode)object).group.getName();
//									}
//									// TODO 代码冗余，记得来改
//									ChatRoom room = selfClient.getRoom() == null ? 
//											ChatRoom.getInstance(selfClient) : selfClient.getRoom();
//									// 相应好友的panel没打开
//									if (!selfClient.tabMap.containsKey(name)) {
//										room.setTitle(name);
//										room.titleLabel.setText(name);
//										ChatRoomPanel pane = null;
//										if(user != null) {
//											pane = new ChatRoomPanel(selfClient, selfClient.getUser(),user);
//										}else {
//											pane = new ChatRoomPanel(selfClient, selfClient.getUser(),group);
//										}
//										
//										room.tabbedPane.addTab(name, null, pane, name);
//										// 重绘过的tab页签
//										room.tabbedPane.setTabComponentAt(room.tabbedPane.indexOfTab(name), 
//												new MyTabComponent(selfClient.getUser().getNickName(), name, room, selfClient));
//										int index = room.tabbedPane.indexOfTab(name);
//										room.tabbedPane.setSelectedIndex(index);
//										// 将队列里面的消息显示在面板上
//										//TODO 消息队列应该吧群组和好友分开, 但是统一不是更好吗
//										if (selfClient.msgQueMap.size() > 0) {
//											try {
//												while ((message = selfClient.msgQueMap.get(name).poll()) != null) {
//													System.out.println("接收到的群聊消息为："+ message.toString());
//													StyledDocument doc = pane.historyTextPane.getStyledDocument();
//													// 名称、日期
//													SimpleAttributeSet nameSet = getAttributeSet(true, null);
//													doc.insertString(doc.getLength(), StringUtil.createSenderInfo(message.getSenderName()), nameSet);
//													SimpleAttributeSet contentSet = getAttributeSet(false, message);
//													// 缩进
//													StyleConstants.setLeftIndent(contentSet, 10);
//													// 此处开始缩进
//													doc.setParagraphAttributes(doc.getLength(), doc.getLength(), contentSet, true);
//													// 正文
//													// 文字或者图文混合
//													if (!StringUtil.isEmpty(message.getContent())) {
//														// 记录下这行消息插入的光标在哪里
//														// 将光标放置到消息的最后
//														pane.position = doc.getLength();
//														doc.insertString(doc.getLength(), message.getContent(), contentSet);
//														if (!StringUtil.isEmpty(message.getImageMark()) && message.getImageMark().split("/").length > 0) {
//															for (String str : message.getImageMark().split("/")) {
//																int imgIndex = Integer.valueOf(str.substring(str.indexOf("|")+1));// 图片的位置（下标）
//																pane.historyTextPane.setCaretPosition(pane.position+imgIndex);// 光标
//																String mark = str.substring(str.indexOf(")")+1, str.indexOf("|"));
//																String fileName = "/feiqq/resource/image/face/" + mark + ".gif";
//																pane.historyTextPane.insertIcon(new ImageIcon(Emoticon.class.getResource(fileName)));
//															}
//														}
//													} else {// 文字为空，说明发送的全部是图片
//														for (String str : message.getImageMark().split("/")) {
//															// 此处要插入图片
//															pane.historyTextPane.setCaretPosition(doc.getLength());// 光标
//															String mark = str.substring(str.indexOf(")")+1, str.indexOf("|"));
//															String fileName = "/feiqq/resource/image/face/" + mark + ".gif";
//															pane.historyTextPane.insertIcon(new ImageIcon(Emoticon.class.getResource(fileName)));
//														}
//													}
//													// 换行
//													doc.insertString(doc.getLength(), "\n", contentSet);
//													// 将缩进还原回来
//													StyleConstants.setLeftIndent(contentSet, 0f);
//													doc.setParagraphAttributes(doc.getLength(), doc.getLength(), contentSet, true);
//												}
//											} catch (BadLocationException e1) {
//												e1.printStackTrace();
//											}
//										}
//										// 将room信息返回
//										selfClient.setRoom(room);
//										selfClient.tabMap.put(name, pane);
//										// 告知client，我已接受到相应好友消息
//										selfClient.msgStatusMap.put(name, false);
////											// 告知client，下次来消息了继续闪烁
////											selfClient.threadMap.put(user.getName(), false);
//									} 
//								}
//								model.reload(((RecentNode)object));
//							}
//						}
//					}
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					TreePath path = jTree.getPathForLocation(e.getX(), e.getY());
					if (null != path) {
						// path中的node节点（path不为空，这里基本不会空）
						Object object = path.getLastPathComponent();
						if (object instanceof RecentNode) {
							JPopupMenu pm = new JPopupMenu();
							pm.setBackground(Color.WHITE);
							pm.setBorder(Constants.LIGHT_GRAY_BORDER);
							JMenuItem mit0 = new JMenuItem("移除当前");
							mit0.setOpaque(false);
							mit0.setFont(Constants.BASIC_FONT);
							JMenuItem mit1 = new JMenuItem("移除全部");
							mit1.setOpaque(false);
							mit1.setFont(Constants.BASIC_FONT);
							
							//移除当前
							mit0.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent arg0) {
									RecentNode recentNode = (RecentNode) object;
									root.remove(recentNode);
									model.reload();
									if(recentNode.getFriend() != null) {
										selfClient.recentNodeMap.remove(recentNode.getFriend().getNickName());
									}else {
										selfClient.recentNodeMap.remove(recentNode.getGroup().getName());
									}
								}
								
							});
							//移除全部
							mit1.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent arg0) {
									root.removeAllChildren();
									model.reload();
									selfClient.recentNodeMap.clear();
								}
								
							});
							pm.add(mit0);
							pm.add(mit1);
							pm.show(jTree, e.getX(), e.getY());
						}
					}
				}
			}
			
		});
		
		
		this.add(jScrollPane, BorderLayout.CENTER);
	}
	public void loadTree(User user) {
		if(!selfClient.recentNodeMap.containsKey(user.getNickName())) {
			RecentNode recentNode = new RecentNode(PictureUtil.getPicture("avatar2.png"), user);
			root.add(recentNode);
			model.reload();	
			selfClient.recentNodeMap.put(user.getNickName(), recentNode);
		}
	}
	public void loadTree(Group group) {
		if(!selfClient.recentNodeMap.containsKey(group.getName())) {
			RecentNode recentNode = new RecentNode(PictureUtil.getPicture("group1.png"), group);
			root.add(recentNode);
			model.reload();	
			selfClient.recentNodeMap.put(group.getName(), recentNode);
		}
	}
	
	private SimpleAttributeSet getAttributeSet(boolean isDefault, Message message) {
		SimpleAttributeSet set = new SimpleAttributeSet();
		if (isDefault) {
			StyleConstants.setBold(set, false);
			StyleConstants.setItalic(set, false);
			StyleConstants.setFontSize(set, 15);
			StyleConstants.setFontFamily(set, "宋体");
			StyleConstants.setForeground(set, Color.RED);
		} else {
			// 字体名称
			StyleConstants.setFontFamily(set, message.getFamily());
			// 字号
			StyleConstants.setFontSize(set, message.getSize());
			// 样式
			int styleIndex = message.getStyle();
			if (styleIndex == 0) {// 常规
				StyleConstants.setBold(set, false);
				StyleConstants.setItalic(set, false);
			}
			if (styleIndex == 1) {// 斜体
				StyleConstants.setBold(set, false);
				StyleConstants.setItalic(set, true);
			}
			if (styleIndex == 2) {// 粗体
				StyleConstants.setBold(set, true);
				StyleConstants.setItalic(set, false);
			}
			if (styleIndex == 3) {// 粗斜体
				StyleConstants.setBold(set, true);
				StyleConstants.setItalic(set, true);
			}
			// 字体颜色
			int foreIndex = message.getFore();
			if (foreIndex == 0) {// 黑色
				StyleConstants.setForeground(set, Color.BLACK);
			}
			if (foreIndex == 1) {// 橙色
				StyleConstants.setForeground(set, Color.ORANGE);
			}
			if (foreIndex == 2) {// 黄色
				StyleConstants.setForeground(set, Color.YELLOW);
			}
			if (foreIndex == 3) {// 绿色
				StyleConstants.setForeground(set, Color.GREEN);
			}
			// 背景颜色
			int backIndex = message.getBack();
			if (backIndex == 0) {// 白色
				StyleConstants.setBackground(set, Color.WHITE);
			}
			if (backIndex == 1) {// 灰色
				StyleConstants.setBackground(set, new Color(200, 200, 200));
			}
			if (backIndex == 2) {// 淡红
				StyleConstants.setBackground(set, new Color(255, 200, 200));
			}
			if (backIndex == 3) {// 淡蓝
				StyleConstants.setBackground(set, new Color(200, 200, 255));
			}
			if (backIndex == 4) {// 淡黄
				StyleConstants.setBackground(set, new Color(255, 255, 200));
			}
			if (backIndex == 5) {// 淡绿
				StyleConstants.setBackground(set, new Color(200, 255, 200));
			}
		}
		return set;
	}
	
}
