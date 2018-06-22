package feiqq.socket.client;

import feiqq.bean.Category;
import feiqq.bean.Group;
import feiqq.bean.Message;
import feiqq.bean.Setup;
import feiqq.bean.User;
import feiqq.thread.FlashThread;
import feiqq.thread.ShakeThread;
import feiqq.ui.common.CategoryNode;
import feiqq.ui.common.Emoticon;
import feiqq.ui.common.MyOptionPane;
import feiqq.ui.common.MyTabComponent;
import feiqq.ui.frame.ChatRoom;
import feiqq.ui.frame.ChatRoomPanel;
import feiqq.ui.frame.LoginWindow;
import feiqq.ui.frame.MainWindow;
import feiqq.ui.friend.FriendNode;
import feiqq.ui.group.GroupNode;
import feiqq.util.Constants;
import feiqq.util.JsonUtil;
import feiqq.util.MacUtil;
import feiqq.util.PictureUtil;
import feiqq.util.StringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;

import java.awt.Color;
import java.awt.SystemTray;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ClientHandler implements ChannelInboundHandler {

	private Client client;

	public ClientHandler(Client client) {
		this.client = client;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// // 跟服务器打招呼
		// ctx.channel().writeAndFlush(
		// ByteBufAllocator.DEFAULT.buffer().writeBytes(
		// ("Hi, Server, I am Client!").getBytes()));
		client.setChannel(ctx.channel());
		System.out.println(ctx.channel().localAddress() + "成功连接上了"
				+ ctx.channel().remoteAddress());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println(ctx.channel().remoteAddress() + "服务器挂了！");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		String msgStr = ((ByteBuf) msg).toString(Charset.defaultCharset());
		System.err.println("接收的消息：" + msgStr.getBytes().length + msgStr);
		Message message = JsonUtil.transToBean(msgStr);
		// 不同类型消息不同处理
		// 回文
		if (null != message && Constants.PALIND_MSG.equals(message.getType())) {
			if (Constants.LOGIN_MSG.equals(message.getPalindType())) {
				if (null != message.getUser()) {
					// 销毁窗体
					client.getLogin().dispose();
					//client.getLoginingWindow().dispose();
					//client.setFalg(false);
					// 移除托盘图标
					SystemTray.getSystemTray().remove(client.getIcon());
					// 共有信息到client
					client.setUser(message.getUser());
					client.setCategoryList(message.getCategoryList());
					client.setMemberList(message.getMemberList());
				    client.setGroupList(message.getGroupList());
					// 初始化主界面
					MainWindow inst = MainWindow.getInstance(client);
					client.setMain(inst);
				} else {
					MyOptionPane.showMessageDialog(client.getLogin(), message.getContent(), "友情提示", Constants.FAILURE);
				}
			}
			if(Constants.ECHO_SEARCH_PRESET_INFO.equals(message.getPalindType())) {
				String content = message.getContent();
				String msgStr1[] = content.split(Constants.LEFT_SLASH);
				Setup setup = new Setup();
				setup.setUserName(msgStr1[0]);
				setup.setUserPassword(msgStr1[1]);
				if(msgStr1[2].equals("true")) {
					setup.setAutoLogin(true);
				}else {
					setup.setAutoLogin(false);
				}
				if(msgStr1[3].equals("true")) {
					setup.setSavePassword(true);
				}else {
					setup.setSavePassword(false);
				}
				client.setSetup(setup);
				
				
			}
			
			if (Constants.REGISTER_MSG.equals(message.getPalindType())) {
				if (Constants.SUCCESS.equals(message.getContent())) {
					MyOptionPane.showMessageDialog(client.getRegister(), "注册成功！", "友情提示", Constants.SUCCESS);
					client.getRegister().dispose();
					client.setRegister(null);
				} else {
					MyOptionPane.showMessageDialog(client.getRegister(), message.getContent(), "友情提示", Constants.FAILURE);
				}
			}
			if (Constants.REQUEST_ADD_MSG.equals(message.getPalindType())) {
				if (Constants.SUCCESS.equals(message.getStatus())) {
					if (null != client.getAddRriend()) {
						MyOptionPane.showMessageDialog(client.getAddRriend(), message.getSenderName()+"同意了您的好友请求！","友情提示", Constants.SUCCESS);
						client.getAddRriend().dispose();
						client.setAddRriend(null);
					} else {
						MyOptionPane.showMessageDialog(client.getMain(), message.getSenderName()+"同意了您的好友请求！", "友情提示", Constants.SUCCESS);
					}
					// 将数据更新到最新
					client.setUser(message.getUser());
					client.setCategoryList(message.getCategoryList());
					client.setMemberList(message.getMemberList());
					// 刷新tree
					CategoryNode cateNode = client.cateNodeMap.get(message.getContent());
					FriendNode friendNode = new FriendNode(PictureUtil.getPicture("avatar2.png"), message.getFriend());
					cateNode.add(friendNode);
//					client.getBuddyModel().reload();
//					如果是刚增加的群组，好像不能用下面这一句，原理还没太弄清楚
//					client.getBuddyModel().reload(cateNode);
					client.getBuddyModel().reload(client.getBuddyRoot());
					client.buddyNodeMap.put(message.getFriend().getNickName(), friendNode);
				} else {
					MyOptionPane.showMessageDialog(client.getAddRriend(), message.getContent(), "友情提示", Constants.FAILURE);
					if (!Constants.FAILURE.equals(message.getStatus())) {
						if (null != client.getAddRriend()) {
							client.getAddRriend().dispose();
							client.setAddRriend(null);
						}
					}
				}
			}
			if (Constants.ECHO_ADD_MSG.equals(message.getPalindType())) {
				if (Constants.SUCCESS.equals(message.getStatus())) {
					// 刷新tree
					CategoryNode cateNode = client.cateNodeMap.get(message.getContent());
					FriendNode friendNode = new FriendNode(PictureUtil.getPicture("avatar2.png"), message.getFriend());
					cateNode.add(friendNode);
					client.getBuddyModel().reload(cateNode);
					client.buddyNodeMap.put(message.getFriend().getNickName(), friendNode);
				}
				if (Constants.FAILURE.equals(message.getStatus())) {
					MyOptionPane.showMessageDialog(client.getMain(), message.getContent(), "友情提示", Constants.FAILURE);
				}
			}
			//回应添加群聊
			if(Constants.ECHO_ADD_GROUP.equals(message.getPalindType())) {
				if(Constants.SUCCESS.equals(message.getStatus())) {
					CategoryNode rootNode = client.getDefaultGroupRoot();
					Group group = new Group(message.getSenderId(), message.getSenderName());
					GroupNode groupNode = new GroupNode(PictureUtil.getPicture("group1.png"), group);
					rootNode.add(groupNode);
					client.getGroupModel().reload(rootNode);
					client.groupNodeMap.put(message.getSenderName(), groupNode);
					MyOptionPane.showMessageDialog(client.getMain(), "添加群聊成功", "友情提示", Constants.SUCCESS);
				}
				if(Constants.FAILURE.equals(message.getStatus())){
					MyOptionPane.showMessageDialog(client.getMain(), "添加群聊失败", "友情提示", Constants.FAILURE);
				}
			}
			//回应查询聊天记录
			if(Constants.ECHO_SEARCH_CHAR_RECORD.equals(message.getPalindType())) {
				//System.out.print("没执行到这里？？？");
				client.setCharRecord(message.getContent());
				//Constants.historyInfo = message.getContent();
				//System.out.print("查询到的客户端历史聊天记录：：:"+Constants.historyInfo);
			}
			//回应创建群聊
			if(Constants.ECHO_CREATE_GROUP.equals(message.getPalindType())) {
				if(Constants.SUCCESS.equals(message.getStatus())) {
					CategoryNode rootNode = client.getDefaultGroupRoot();
					Group group = new Group(message.getSenderId(), message.getSenderName());
					GroupNode groupNode = new GroupNode(PictureUtil.getPicture("group1.png"), group);
					rootNode.add(groupNode);
					client.getGroupModel().reload(rootNode);
					client.groupNodeMap.put(message.getSenderName(), groupNode);
					MyOptionPane.showMessageDialog(client.getMain(), "创建群聊成功", "友情提示", Constants.SUCCESS);
				}
				if(Constants.FAILURE.equals(message.getStatus())){
					MyOptionPane.showMessageDialog(client.getMain(), "创建群聊失败", "友情提示", Constants.FAILURE);
				}
			}
			//回应查找密码
			if(Constants.ECHO_FIND_PASSWORD.equals(message.getPalindType())) {
				if(Constants.SUCCESS.equals(message.getStatus())) {
					MyOptionPane.showMessageDialog(client.getMain(), "查找密码成功", "友情提示", Constants.SUCCESS);
					client.getFindPasswordWindow().dispose();
					client.setFindPasswordWindow(null);
				}else {
					MyOptionPane.showMessageDialog(client.getMain(), "查找密码失败", "友情提示", Constants.FAILURE);
				}
			}
			//回应修改密码
			if(Constants.ECHO_CHANGE_PASSWORD.equals(message.getPalindType())) {
				if(Constants.SUCCESS.equals(message.getStatus())) {
					MyOptionPane.showMessageDialog(client.getChangeInfoWindow(), "修改密码成功,请重新登陆!", "友情提示", Constants.SUCCESS);
					client.getChangeInfoWindow().dispose();
					client.setChangeInfoWindow(null);
					client.getMain().dispose();
					client.getMain().delIcon();
					client.setMain(null);
					client.sendMsg(new Message(Constants.EXIT_MSG));
					client = null;
					Client newClient = new Client();
					//查询预置信息
//					String mac = MacUtil.getMac();
//					newClient.sendMsg(new Message(Constants.SEARCH_PRESET_INFO, mac));
					newClient.setLogin(LoginWindow.getInstance(newClient));
				}else {
					MyOptionPane.showMessageDialog(client.getChangeInfoWindow(), "修改密码失败!", "友情提示", Constants.FAILURE);
				}
			}
			//回应修改个人信息未修改密码
			if(Constants.ECHO_CHANGE_INFO_WITHOUT_PASSWORD.equals(message.getPalindType())) {
				if(message.getStatus().equals(Constants.SUCCESS)) {
					client.getChangeInfoWindow().infoStaitc = Constants.SUCCESS;
					Constants.changeInformationStatic = Constants.SUCCESS;
					MyOptionPane.showMessageDialog(client.getChangeInfoWindow(), "修改个人信息成功!", "友情提示", Constants.SUCCESS);
					
				}else {
					MyOptionPane.showMessageDialog(client.getChangeInfoWindow(), "修改个人信息失败!", "友情提示", Constants.FAILURE);
					//client.setChangeInfoStatic(Constants.FAILURE);
				}
			}
			//回应修改个人信息已修改密码
			if(Constants.ECHO_CHANGE_INFO_WITH_PASSWORD.equals(message.getPalindType())) {
				if(message.getStatus().equals(Constants.SUCCESS)) {
					MyOptionPane.showMessageDialog(client.getChangeInfoWindow(), "修改个人信息成功,下次登陆请使用新密码！", "友情提示", Constants.SUCCESS);
					//client.setChangeInfoStatic(Constants.SUCCESS);
				}else {
					MyOptionPane.showMessageDialog(client.getChangeInfoWindow(), "修改个人信息失败!", "友情提示", Constants.FAILURE);
					//client.setChangeInfoStatic(Constants.FAILURE);
				}
			}
			//回应修改个人信息失败
			if(Constants.ECHO_CHANGE_INFO_FAILURE.equals(message.getPalindType())) {
				MyOptionPane.showMessageDialog(client.getChangeInfoWindow(), "该昵称被占用", "友情提示", Constants.NOTICE);
				//client.setChangeInfoStatic(Constants.FAILURE);
			}
//			if (Constants.GENRAL_MSG.equals(message.getPalindType()) 
//					|| Constants.SHAKE_MSG.equals(message.getPalindType())) {
//				MyOptionPane.showMessageDialog(client.getRoom(), message.getContent(), "友情提示");
//			}
			if (Constants.DELETE_USER_CATE_MSG.equals(message.getPalindType())) {
				// 刷新tree
				CategoryNode cateNode = client.cateNodeMap.get(message.getContent());
//				client.getBuddyModel().removeNodeFromParent(cateNode);
				client.getBuddyRoot().remove(cateNode);
				client.getBuddyModel().reload();// 需要从根节点刷新
				client.cateNodeMap.remove(message.getContent());// 清空记录
			}
			if (Constants.DELETE_USER_MEMBER_MSG.equals(message.getPalindType())) {
				// 刷新tree
				FriendNode friendNode = client.buddyNodeMap.get(message.getContent());
				CategoryNode cateNode = (CategoryNode) friendNode.getParent();
				cateNode.remove(friendNode);
				client.getBuddyModel().reload(cateNode);
				client.buddyNodeMap.remove(message.getContent());// 清空记录
			}
			if(Constants.ECHO_DELETE_GROUP.equals(message.getPalindType())) {
				System.out.println("回应删除群组执行到这里");
				GroupNode groupNode = client.groupNodeMap.get(message.getContent());
				CategoryNode cateNode = (CategoryNode) groupNode.getParent();
				cateNode.remove(groupNode);
				client.getGroupModel().reload(cateNode);
				client.groupNodeMap.remove(message.getContent());// 清空记录
			}
			if (Constants.ADD_USER_CATE_MSG.equals(message.getPalindType())) {
				Category category = message.getCategory();
				CategoryNode cateNode = new CategoryNode(PictureUtil.getPicture("arrow_left.png"), category);
				client.getBuddyRoot().add(cateNode);
				client.getBuddyModel().reload();
				client.cateNodeMap.put(category.getId(), cateNode);// 记录
			}
			if (Constants.EDIT_USER_CATE_MSG.equals(message.getPalindType())) {
				
				Category category = message.getCategory();
				CategoryNode cateNode = client.cateNodeMap.get(category.getId());
				cateNode.category = category;
				cateNode.nickName.setText(category.getName());
			}
		}
		
		// 普通 群聊
		if(null != message && Constants.GENRAL_MSG.equals(message.getType()) && Constants.GROUPCHAT.equals(message.getSenderType())) {
			// 相应好友的panel没打开
			if (!client.tabMap.containsKey(message.getReceiverName())) {
				System.out.println("这是群聊，执行岛这里！！！！"+message.getSenderName()+message.getReceiverName());
				// 将消息存放到相应的队列中
				Queue<Message> queue = client.msgQueMap.get(message.getReceiverName()) == null ? 
						new LinkedList<Message>() : client.msgQueMap.get(message.getReceiverName()); 
				queue.offer(message);
				client.msgQueMap.put(message.getReceiverName(), queue);
				// 是否有对应好友发消息
				client.msgStatusMap.put(message.getReceiverName(), true);
				// TODO 这个地方，关掉了相应窗口再打开的呢，应该如何处理，难道在开启一个线程？
				// 哎呀呀，java竟然已经帮我考虑到了，run方法执行完毕，次线程会被自动回收，好犀利
				//TODO 声音不好使
				//MusicUtil.playMsgMusic();
				// TODO 线程叠加了，怎么办呢
				// 线程叠加，越闪越快，导致最后也恢复不了原状
				FlashThread flash = new FlashThread(client, message.getReceiverName());
				flash.start();
			} else {
				ChatRoom room = client.getRoom() == null ? 
						ChatRoom.getInstance(client) : client.getRoom();
				room.setTitle(message.getReceiverName());
				room.titleLabel.setText(message.getReceiverName());
				int index = room.tabbedPane.indexOfTab(message.getReceiverName());
				room.tabbedPane.setSelectedIndex(index);
				try {
					ChatRoomPanel pane = (ChatRoomPanel) room.tabbedPane.getComponentAt(index);
					StyledDocument doc = pane.historyTextPane.getStyledDocument();
					// 名称、日期
					SimpleAttributeSet nameSet = getAttributeSet(true, null);
					doc.insertString(doc.getLength(), StringUtil.createSenderInfo(message.getSenderName()), nameSet);
					SimpleAttributeSet contentSet = getAttributeSet(false, message);
					// 缩进
					StyleConstants.setLeftIndent(contentSet, 10); //设置左缩进
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
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
				// 当前窗体获得焦点
				room.requestFocus();
				// 音乐
				//MusicUtil.playMsgMusic();
			}
		}
		
		// 普通 好友单发
		if (null != message && Constants.GENRAL_MSG.equals(message.getType()) && Constants.FRIEND.equals(message.getSenderType())) {
			// 相应好友的panel没打开
			if (!client.tabMap.containsKey(message.getSenderName())) {
				// 将消息存放到相应的队列中
				Queue<Message> queue = client.msgQueMap.get(message.getSenderName()) == null ? 
						new LinkedList<Message>() : client.msgQueMap.get(message.getSenderName()); 
				queue.offer(message);
				client.msgQueMap.put(message.getSenderName(), queue);
				// 是否有对应好友发消息
				client.msgStatusMap.put(message.getSenderName(), true);
				System.out.println("消息执行到这里");
				// TODO 这个地方，关掉了相应窗口再打开的呢，应该如何处理，难道在开启一个线程？
				// 哎呀呀，java竟然已经帮我考虑到了，run方法执行完毕，次线程会被自动回收，好犀利
				//TODO 声音不好使
				//MusicUtil.playMsgMusic();
				// TODO 线程叠加了，怎么办呢
				// 线程叠加，越闪越快，导致最后也恢复不了原状
				FlashThread flash = new FlashThread(client, message.getSenderName());
				flash.start();
			} else {
				ChatRoom room = client.getRoom() == null ? 
						ChatRoom.getInstance(client) : client.getRoom();
				room.setTitle(message.getReceiverName() + " - " + message.getSenderName());
				room.titleLabel.setText(message.getReceiverName() + " - " + message.getSenderName());
				int index = room.tabbedPane.indexOfTab(message.getSenderName());
				room.tabbedPane.setSelectedIndex(index);
				try {
					ChatRoomPanel pane = (ChatRoomPanel) room.tabbedPane.getComponentAt(index);
					StyledDocument doc = pane.historyTextPane.getStyledDocument();
					// 名称、日期
					SimpleAttributeSet nameSet = getAttributeSet(true, null);
					doc.insertString(doc.getLength(), StringUtil.createSenderInfo(message.getSenderName()), nameSet);
					SimpleAttributeSet contentSet = getAttributeSet(false, message);
					// 缩进
					StyleConstants.setLeftIndent(contentSet, 10); //设置左缩进
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
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
				// 当前窗体获得焦点
				room.requestFocus();
				// 音乐
				//MusicUtil.playMsgMusic();
			}
		}
		// 抖动
		if (null != message && Constants.SHAKE_MSG.equals(message.getType())) {
			int count = 0;
			while(client.getCharRecord() == null) {
				System.out.println("等待回执。。。");
				count++;
				if(count > 1000) {
					break;
				}
			}
			User self = client.getUser();
			User user = client.buddyNodeMap.get(message.getSenderName()).getFriend();
			ChatRoom room = client.getRoom() == null ? 
					ChatRoom.getInstance(client) : client.getRoom();
			ChatRoomPanel pane = null;
			// 相应好友的panel没打开
			if (!client.tabMap.containsKey(user.getNickName())) {
				System.out.println("没打开？");
				room.setTitle(message.getReceiverName() + " - " + message.getSenderName());
				room.titleLabel.setText(message.getReceiverName() + " - " + message.getSenderName());
				pane = new ChatRoomPanel(client, self, user);
				room.tabbedPane.addTab(user.getNickName(), null, pane, user.getNickName());
				// 重绘过的tab页签
				room.tabbedPane.setTabComponentAt(room.tabbedPane.indexOfTab(user.getNickName()), 
						new MyTabComponent(self.getNickName(), user.getNickName(), room, client));
				int index = room.tabbedPane.indexOfTab(user.getNickName());
				room.tabbedPane.setSelectedIndex(index);
				// 音乐
				//MusicUtil.playMsgMusic();
				// 抖动
				new ShakeThread(room).start();
				// 将room信息返回
				client.setRoom(room);
				client.tabMap.put(user.getNickName(), pane);
				// 告知client，我已接受到相应好友消息
				client.msgStatusMap.put(user.getNickName(), false);
			} else {
				room.setTitle(message.getReceiverName() + " - " + message.getSenderName());
				room.titleLabel.setText(message.getReceiverName() + " - " + message.getSenderName());
				int index = room.tabbedPane.indexOfTab(message.getSenderName());
				room.tabbedPane.setSelectedIndex(index);
				pane = (ChatRoomPanel) room.tabbedPane.getComponentAt(index);
				// 当前窗体获得焦点
				room.requestFocus();
				// 音乐
				//MusicUtil.playMsgMusic();
				// 抖动
				new ShakeThread(room).start();
			}
			
			if(client.getCharRecord() != null) {
				String news[] = client.getCharRecord().split(Constants.STAR);
				System.out.println("接收到的消息长度为：" + news.length);
				System.out.println("接收到的消息为：" + client.getCharRecord());
				for(int newIndex = 0; newIndex < news.length; newIndex++) {
					String messages[] = news[newIndex].split(Constants.LINE);
					System.out.println(messages[0]+messages[1]+messages[2]+messages[3]);
					try {
						StyledDocument doc = pane.historyTextPane.getStyledDocument();
						// 名称、日期
						SimpleAttributeSet nameSet = getDefaultAttributeSet();
						doc.insertString(doc.getLength(), StringUtil.createHistoryInfo(messages[0], messages[1], messages[2]), nameSet);
						SimpleAttributeSet contentSet = getDefaultAttributeSet();
						
						// 缩进
						StyleConstants.setLeftIndent(contentSet, 10);
						// 此处开始缩进
						doc.setParagraphAttributes(doc.getLength(), doc.getLength(), contentSet, true);
						// 正文
						// 文字或者图文混合
						if (!messages[3].equals("null")) {
							// 记录下这行消息插入的光标在哪里
							// 将光标放置到消息的最后
							pane.position = doc.getLength();
							doc.insertString(doc.getLength(), messages[3], contentSet);
							if (!messages[4].equals("null") && messages[4].split("/").length > 0) {
								for (String str : messages[4].split("/")) {
									int imgIndex = Integer.valueOf(str.substring(str.indexOf("|")+1));// 图片的位置（下标）
									pane.historyTextPane.setCaretPosition(pane.position+imgIndex);// 光标
									String mark = str.substring(str.indexOf(")")+1, str.indexOf("|"));
									String fileName = "/feiqq/resource/image/face/" + mark + ".gif";
									pane.historyTextPane.insertIcon(new ImageIcon(Emoticon.class.getResource(fileName)));
								}
							}
						} else {// 文字为空，说明发送的全部是图片
							for (String str : messages[4].split("/")) {
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
			
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
					
				}
				client.setCharRecord(null);
			}
			// 将队列里面的消息显示在面板上
			if (client.msgQueMap.size() > 0) {
				try {
					while ((message = client.msgQueMap.get(user.getNickName()).poll()) != null) {
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
		}
		// 请求添加好友
		if (null != message && Constants.REQUEST_ADD_MSG.equals(message.getType())) {
			int res = MyOptionPane.showConfirmDialog(client.getMain(), "同意or拒绝？", message.getSenderName() + "想添加您为好友", "同意", "拒绝");
			Message backMsg = new Message();
			backMsg.setType(Constants.ECHO_ADD_MSG);
			backMsg.setSenderId(client.getUser().getId());
			backMsg.setSenderName(client.getUser().getNickName());
			backMsg.setReceiverId(message.getSenderId());
			backMsg.setReceiverName(message.getSenderName());
			if (res == Constants.YES_OPTION) {
				backMsg.setContent(message.getContent() + Constants.LEFT_SLASH + Constants.YES);
			}
			if (res == Constants.NO_OPTION) {
				backMsg.setContent(message.getContent() + Constants.LEFT_SLASH + Constants.NO);
			}
			client.sendMsg(backMsg);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().flush();
	}

	@Override
	public void channelWritabilityChanged(ChannelHandlerContext ctx)
			throws Exception {
		System.out.println(ctx.channel().isWritable());
		System.out.println("我已掉线，如何继续说话？");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		System.out.println("异常了哦！");
		cause.printStackTrace();
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Client---handlerAdded");
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Client---handlerRemoved");
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Client---channelRegistered");
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Client---channelUnregistered");
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		System.out.println("Client---userEventTriggered");
	}
	
	private SimpleAttributeSet getDefaultAttributeSet() {
		SimpleAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setBold(set, false);
		StyleConstants.setItalic(set, false);
		StyleConstants.setFontSize(set, 15);
		StyleConstants.setFontFamily(set, "宋体");
		StyleConstants.setForeground(set, Color.GRAY);
		return set;
	}

	/**
	 * 生成对方的文字样式
	 * @param isDefault 是否默认样式
	 * @param message 消息体
	 * @return
	 */
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
