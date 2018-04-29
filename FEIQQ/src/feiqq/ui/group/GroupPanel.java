package feiqq.ui.group;

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

import feiqq.bean.Category;
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
import feiqq.ui.frame.AddFriendWindow;
import feiqq.ui.frame.AddGroupWindow;
import feiqq.ui.frame.ChatRoom;
import feiqq.ui.frame.ChatRoomPanel;
import feiqq.ui.friend.FriendNode;
import feiqq.util.Constants;
import feiqq.util.PictureUtil;
import feiqq.util.StringUtil;

public class GroupPanel extends JPanel {

	private JTree jTree;
	private JScrollPane jScrollPane;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;
	private static Color inColor = new Color(254, 224, 109); 
	private static Color selectColor = new Color(249, 184, 87);
	
	private Client selfClient;
	
	public GroupPanel(Client client) {
		
		this.selfClient = client;
		BorderLayout borderLayout = new BorderLayout();
		this.setLayout(borderLayout);
		root = new DefaultMutableTreeNode();
		model = new DefaultTreeModel(root);
		selfClient.setGroupRoot(root);
		selfClient.setGroupModel(model);
		//定义默认群组
		CategoryNode root1 = new CategoryNode(PictureUtil.getPicture("arrow_left.png"), new Category("QQ群"));
		//加载群组数据
		loadTree(root1);
		root.add(root1);
		jTree = new JTree(model);
		jTree.setUI(new MyTreeUI());
		jTree.setCellRenderer(new GroupNodeRenderer());
		jTree.setRootVisible(false);
		jTree.setToggleClickCount(1);
		selfClient.setGroupTree(jTree);
		selfClient.setDefaultGroupRoot(root1);
		
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
					if (object instanceof GroupNode) {
						for (int i = 0; i < root.getChildCount(); i++) {
							Object category = root.getChildAt(i);
							((CategoryNode)category).categoryContent.setBackground(Color.WHITE);
							model.reload((CategoryNode)category);
							for (int j = 0; j < root.getChildAt(i).getChildCount(); j++) {
								Object group = root.getChildAt(i).getChildAt(j);
								if (((GroupNode)group).groupContent.getBackground() != selectColor) {
									if (group == ((GroupNode)object)) {
										((GroupNode)group).groupContent.setBackground(inColor);
									} else {
										((GroupNode)group).groupContent.setBackground(Color.WHITE);
									}
									model.reload(((GroupNode)group));
								}
							}
						}
					}
					if (object instanceof CategoryNode) {
						for (int i = 0; i < root.getChildCount(); i++) {
							Object category = root.getChildAt(i);
							if (category == ((CategoryNode)object)) {
								((CategoryNode)category).categoryContent.setBackground(inColor);
							} else {
								((CategoryNode)category).categoryContent.setBackground(Color.WHITE);
							}
							model.reload((CategoryNode)category);
							for (int j = 0; j < root.getChildAt(i).getChildCount(); j++) {
								Object group = root.getChildAt(i).getChildAt(j);
								if (((GroupNode)group).groupContent.getBackground() != selectColor) {
									((GroupNode)group).groupContent.setBackground(Color.WHITE);
									model.reload(((GroupNode)group));
								}
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
					Object category = root.getChildAt(i);
					((CategoryNode)category).categoryContent.setBackground(Color.WHITE);
					model.reload(((CategoryNode)category));
					for (int j = 0; j < root.getChildAt(i).getChildCount(); j++) {
						Object group = root.getChildAt(i).getChildAt(j);
						((GroupNode)group).groupContent.setBackground(Color.WHITE);
						model.reload(((GroupNode)group));
					}
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
					if (e.getClickCount() == 2) {
						// 好友node节点（区分群组）
						if (object instanceof GroupNode) {
							// 从根节点开始获取所有节点（第一层全部是分组）
							for (int i = 0; i < root.getChildCount(); i++) {
								// 鼠标滑入好友node节点时，需要将分组上面的颜色恢复
								Object category = root.getChildAt(i);
								((CategoryNode)category).categoryContent.setBackground(Color.WHITE);
								model.reload(((CategoryNode)category));// 刷新UI
								// 这一层全部是好友node节点
								for (int j = 0; j < root.getChildAt(i).getChildCount(); j++) {
									Object group = root.getChildAt(i).getChildAt(j);
									// 不是鼠标选中的那个
									if (group != ((GroupNode)object)) {
										((GroupNode)group).groupContent.setBackground(Color.WHITE);
									} else {
										((GroupNode)object).groupContent.setBackground(selectColor);
										// 开启聊天窗口
										Message message = null;
										Group groupMember = ((GroupNode)object).group;
										// TODO 代码冗余，记得来改
										ChatRoom room = selfClient.getRoom() == null ? 
												ChatRoom.getInstance(selfClient) : selfClient.getRoom();
										// 相应好友的panel没打开
										if (!selfClient.tabMap.containsKey(groupMember.getName())) {
											room.setTitle(groupMember.getName());
											room.titleLabel.setText(groupMember.getName());
											ChatRoomPanel pane = new ChatRoomPanel(selfClient, selfClient.getUser(),groupMember);
											room.tabbedPane.addTab(groupMember.getName(), null, pane, groupMember.getName());
											// 重绘过的tab页签
											room.tabbedPane.setTabComponentAt(room.tabbedPane.indexOfTab(groupMember.getName()), 
													new MyTabComponent(selfClient.getUser().getNickName(), groupMember.getName(), room, selfClient));
											int index = room.tabbedPane.indexOfTab(groupMember.getName());
											room.tabbedPane.setSelectedIndex(index);
											// 将队列里面的消息显示在面板上
											
											//TODO 消息队列应该吧群组和好友分开, 但是统一不是更好吗
											if (selfClient.msgQueMap.size() > 0) {
												try {
													while ((message = selfClient.msgQueMap.get(groupMember.getName()).poll()) != null) {
														System.out.println("接收到的群聊消息为："+ message.toString());
														StyledDocument doc = pane.historyTextPane.getStyledDocument();
														// 名称、日期
														SimpleAttributeSet nameSet = getAttributeSet(true, null);
														doc.insertString(doc.getLength(), StringUtil.createSenderInfo(message.getSenderName()), nameSet);
														SimpleAttributeSet contentSet = getAttributeSet(false, message);
														// 缩进
														StyleConstants.setLeftIndent(contentSet, 10);
														// 此处开始缩进
														doc.setParagraphAttributes(doc.getLength(), doc.getLength(), contentSet, true);
														// 正文
														// 文字或者图文混合
														if (!StringUtil.isEmpty(message.getContent())) {
															// 记录下这行消息插入的光标在哪里
															// 将光标放置到消息的最后
															pane.position = doc.getLength();
															doc.insertString(doc.getLength(), message.getContent(), contentSet);
															if (!StringUtil.isEmpty(message.getImageMark()) && message.getImageMark().split("/").length > 0) {
																for (String str : message.getImageMark().split("/")) {
																	int imgIndex = Integer.valueOf(str.substring(str.indexOf("|")+1));// 图片的位置（下标）
																	pane.historyTextPane.setCaretPosition(pane.position+imgIndex);// 光标
																	String mark = str.substring(str.indexOf(")")+1, str.indexOf("|"));
																	String fileName = "/feiqq/resource/image/face/" + mark + ".gif";
																	pane.historyTextPane.insertIcon(new ImageIcon(Emoticon.class.getResource(fileName)));
																}
															}
														} else {// 文字为空，说明发送的全部是图片
															for (String str : message.getImageMark().split("/")) {
																// 此处要插入图片
																pane.historyTextPane.setCaretPosition(doc.getLength());// 光标
																String mark = str.substring(str.indexOf(")")+1, str.indexOf("|"));
																String fileName = "/feiqq/resource/image/face/" + mark + ".gif";
																pane.historyTextPane.insertIcon(new ImageIcon(Emoticon.class.getResource(fileName)));
															}
														}
														// 换行
														doc.insertString(doc.getLength(), "\n", contentSet);
														// 将缩进还原回来
														StyleConstants.setLeftIndent(contentSet, 0f);
														doc.setParagraphAttributes(doc.getLength(), doc.getLength(), contentSet, true);
													}
												} catch (BadLocationException e1) {
													e1.printStackTrace();
												}
											}
											// 将room信息返回
											selfClient.setRoom(room);
											selfClient.tabMap.put(groupMember.getName(), pane);
											// 告知client，我已接受到相应好友消息
											selfClient.msgStatusMap.put(groupMember.getName(), false);
//											// 告知client，下次来消息了继续闪烁
//											selfClient.threadMap.put(user.getName(), false);
										} 
									}
									model.reload(((GroupNode)group));
								}
							}
						}
					}
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					TreePath path = jTree.getPathForLocation(e.getX(), e.getY());
					if (null != path) {
						// path中的node节点（path不为空，这里基本不会空）
						final Object object = path.getLastPathComponent();
						if (object instanceof CategoryNode) {
							JPopupMenu pm = new JPopupMenu();
							pm.setBackground(Color.WHITE);
							pm.setBorder(Constants.LIGHT_GRAY_BORDER);
							JMenuItem mit0 = new JMenuItem("添加群聊");
							mit0.setOpaque(false);
							mit0.setFont(Constants.BASIC_FONT);
							JMenuItem mit1 = new JMenuItem("创建群聊");
							mit1.setOpaque(false);
							mit1.setFont(Constants.BASIC_FONT);
							// 添加群聊
							mit0.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									if (null == selfClient.getAddRriend()) {
										AddGroupWindow inst = AddGroupWindow.getInstance(selfClient, selfClient.getUser());
										selfClient.setAddGroupWindow(inst);
									} else {
										MyOptionPane.showMessageDialog(selfClient.getAddRriend(), "窗口重复打开不太好！", "友情提示");
										selfClient.getAddRriend().requestFocus();
									}
								}
							});
							
							pm.add(mit0);
							pm.add(mit1);
							pm.show(jTree, e.getX(), e.getY());
						}
						if (object instanceof GroupNode) {
							JPopupMenu pm = new JPopupMenu();
							pm.setBackground(Color.WHITE);
							pm.setBorder(Constants.LIGHT_GRAY_BORDER);
							JMenuItem mit1 = new JMenuItem("退出群聊");
							mit1.setOpaque(false);
							mit1.setFont(Constants.BASIC_FONT);
							
							//删除群聊
							mit1.addActionListener(new ActionListener() {

								@Override
								public void actionPerformed(ActionEvent arg0) {
									GroupNode groupNode = (GroupNode) object;
									//CategoryNode cateNode = (CategoryNode) groupNode.getParent();
									Message msg = new Message();
									msg.setType(Constants.DELETE_GROUP);
									msg.setSenderId(selfClient.getUser().getId());
									msg.setSenderName(selfClient.getUser().getNickName());
									msg.setReceiverName(groupNode.group.getName());
									msg.setContent(groupNode.group.getId() 
											+ Constants.LEFT_SLASH + selfClient.getUser().getId());
									selfClient.sendMsg(msg);
									
								}
								
							});
							pm.add(mit1);
//							pm.add(mit2);
//							pm.add(mit3);
//							pm.add(mit4);
							pm.show(jTree, e.getX(), e.getY());
						}
					}
				}
			}
			
		});
		
		this.add(jScrollPane, BorderLayout.CENTER);
	}

	
	
	private void loadTree(CategoryNode root1) {
		for(Group group : selfClient.getGroupList()) {
			GroupNode node = new GroupNode(PictureUtil.getPicture("group1.png"), group);
			root1.add(node);
			selfClient.groupNodeMap.put(group.getName(), node);
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
