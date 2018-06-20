package feiqq.socket.server;

import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import feiqq.bean.Category;
import feiqq.bean.Group;
import feiqq.bean.CateMember;
import feiqq.bean.Message;
import feiqq.bean.Setup;
import feiqq.bean.User;
import feiqq.method.CategoryDao;
import feiqq.method.CharRecordDao;
import feiqq.method.GroupDao;
import feiqq.method.GroupUserDao;
import feiqq.method.OffLineDao;
import feiqq.method.SetupDao;
import feiqq.method.CateMemberDao;
import feiqq.method.UserDao;
import feiqq.util.Constants;
import feiqq.util.JsonUtil;
import feiqq.util.MailUtil;
import feiqq.util.TimeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;

public class ServerHandler implements ChannelInboundHandler {

	private UserDao userDao;
	private CategoryDao cateDao;
	private CateMemberDao memberDao;
	private GroupUserDao groupUserDao;
	private GroupDao groupDao;
	private OffLineDao offLineDao;
	private SetupDao setupDao;
	private CharRecordDao charRecordDao;

	// 这里其实可以直接使用ip存储，为了防止一台电脑登录多个账号
	/** key：SocketAddress，value：用户Id */
	private Map<SocketAddress, String> map = new HashMap<SocketAddress, String>();
	/** key：用户Id，value：channel */
	private Map<String, Channel> clientMap = new HashMap<String, Channel>();

	public ServerHandler(Map<SocketAddress, String> map,
			Map<String, Channel> clientMap) {
		this.map = map;
		this.clientMap = clientMap;

		userDao = new UserDao();
		cateDao = new CategoryDao();
		memberDao = new CateMemberDao();
		groupUserDao = new GroupUserDao();
		groupDao = new GroupDao();
		offLineDao = new OffLineDao();
		setupDao = new SetupDao();
		charRecordDao = new CharRecordDao();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println(ctx.channel().remoteAddress() + "连接成功！");
		// // 跟客户端打招呼
		// ctx.channel().writeAndFlush(
		// ByteBufAllocator.DEFAULT.buffer().writeBytes(
		// ("Hi, Client, Welcome to here!").getBytes()));
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println(ctx.channel().remoteAddress() + "掉线了！");
		clientMap.remove(map.get(ctx.channel().remoteAddress()));
		map.remove(ctx.channel().remoteAddress());
		System.err.println("map大小:" + map.size());
		System.err.println("clientMap大小:" + clientMap.size());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		System.out.println("接收的消息：" + ((ByteBuf) msg).toString(Charset.defaultCharset()));
		dealBusiness(ctx.channel(), msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.err.println("channel读操作完成？");
		ctx.channel().flush();
	}

	@Override
	public void channelWritabilityChanged(ChannelHandlerContext ctx)
			throws Exception {
		System.out.println("掉线儿了，你还怎么说话？");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		System.out.println("异常了哦！");
		cause.printStackTrace();
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Server---handlerAdded 添加server handler");
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Server---handlerRemoved 处理程序删除");
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Server---channelRegistered 通道注册");
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Server---channelUnregistered 通道未注册");
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		System.out.println("Server---userEventTriggered 用户事件触发");
	}

	private void sendMsg(Channel channel, Message message) {
		if (null != channel) {
			String msg = JsonUtil.transToJson(message);
			channel.writeAndFlush(ByteBufAllocator.DEFAULT.buffer()
					.writeBytes(msg.getBytes()))
					.addListener(new ServerListener());
			System.out.println("发送的消息：" + msg.getBytes().length + msg);
		}
	}
	
	private void dealBusiness(Channel channel, Object msg) {
		// 接收到的消息
		String msgStr = ((ByteBuf) msg).toString(Charset.defaultCharset());
		Message message = JsonUtil.transToBean(msgStr);
		// 查询预置信息
		if(null != message && Constants.SEARCH_PRESET_INFO.equals(message.getType())) {
			String mac = message.getContent();
			Setup setup = setupDao.getMessageByMac(mac);
			if(setup != null) {
				String autoLogin = null;
				String savePassword = null;
				if(setup.isAutoLogin()) {
					autoLogin = "true";
				}else {
					autoLogin = "false";
				}
				if(setup.isSavePassword()) {
					savePassword = "true";
				}else {
					savePassword = "false";
				}
				String str = setup.getUserName() + Constants.LEFT_SLASH + setup.getUserPassword() + Constants.LEFT_SLASH + autoLogin + Constants.LEFT_SLASH + savePassword;
				Message backMsg = new Message();
				backMsg.setContent(str);
				backMsg.setType(Constants.PALIND_MSG);
				backMsg.setPalindType(Constants.ECHO_SEARCH_PRESET_INFO);
				sendMsg(channel, backMsg);
			}
		}
		// 登陆
		if (null != message && Constants.LOGIN_MSG.equals(message.getType())) {
			String content = message.getContent();
			String msgStr1[] = content.split(Constants.LEFT_SLASH);
			User user = userDao.getByUserName(msgStr1[0]);
			Message backMsg = login(message, channel);
			backMsg.setPalindType(Constants.LOGIN_MSG);
			sendMsg(channel, backMsg);
			
			//保存密码与自动登陆
			//if(!msgStr1[2].equals("false") && !msgStr1[3].equals("false")) {
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
					setup.setSavePassword(false);;
				}
				setup.setMacAddress(msgStr1[4]);
				if(setupDao.getMessageByMac(setup.getMacAddress()) != null) {
					setupDao.update(setup);
				}else {
					setupDao.save(setup);
				}
				
//			}else {
//				if(setupDao.getMessageByMac(msgStr1[4]) != null) {
//					setupDao.delSetup(msgStr1[4]);
//				}
//			}
			
			
			//检查离线消息
			System.out.println("正在检查离线消息");
			List<Message> listMessage = offLineDao.getOffLineMessage(user.getId());
			if(listMessage != null && listMessage.size() > 0) {
				for(Message messages : listMessage) {
					sendMsg(channel,messages);
					//System.out.println("发送消息");
				}
			}
			offLineDao.deleteMessage(user.getId());
			
			//检查群组离线消息
			System.out.println("正在检查群组离线消息");
			List<Message> listMessageChars = offLineDao.getOffLineCharsMessage(user.getId());
			if(listMessageChars != null && listMessageChars.size() > 0) {
				for(Message messages : listMessageChars) {
					sendMsg(channel,messages);
					//System.out.println("发送消息");
				}
			}
			offLineDao.deleteCharsMessage(user.getId());
			
			
		}
		//找回密码
		if(null != message && Constants.FIND_PASSWORD.equals(message.getType())) {
			Message backMsg = new Message();
			backMsg.setType(Constants.PALIND_MSG);
			backMsg.setPalindType(Constants.ECHO_FIND_PASSWORD);
			String passWord = userDao.findPassWordByUserName(message.getContent());
			try {
				MailUtil.sendPassMail(message.getContent(), passWord);
				backMsg.setStatus(Constants.SUCCESS);
			} catch (MessagingException e) {
				backMsg.setStatus(Constants.FAILURE);
				e.printStackTrace();
			} finally {
				sendMsg(channel, backMsg);
			}
		}
		//查询聊天记录
		if(null != message && Constants.SEARCH_CHAR_RECORD.equals(message.getType())) {
			String content = message.getContent();
			String msgStr1[] = content.split(Constants.LEFT_SLASH);
			Message backMsg = new Message();
			backMsg.setType(Constants.PALIND_MSG);
			backMsg.setPalindType(Constants.ECHO_SEARCH_CHAR_RECORD);
			String contents = null;
			List<Message> messageList = charRecordDao.getCharRecord(msgStr1[0], msgStr1[1]);
			if(messageList != null && messageList.size() > 0) {
				for(Message mes : messageList) {
					if(contents == null) {
						contents = mes.getSenderName() + Constants.LINE + mes.getReceiverName() + Constants.LINE + mes.getSendTime() + Constants.LINE;
						if(mes.getContent() != null) {
							contents += mes.getContent() + Constants.LEFT_SLASH;
						}else {
							contents += Constants.NULL + Constants.LEFT_SLASH;
						}
//						if(mes.getImageMark() != null) {
//							contents += mes.getImageMark() + Constants.LEFT_SLASH;
//						}else{
//							contents += Constants.NULL + Constants.LEFT_SLASH;
//						}
								 
					}else {
						contents += mes.getSenderName() + Constants.LINE + mes.getReceiverName() + Constants.LINE + mes.getSendTime() + Constants.LINE;
						if(mes.getContent() != null) {
							contents += mes.getContent() + Constants.LEFT_SLASH;
						}else {
							contents += Constants.NULL + Constants.LEFT_SLASH;
						}
//						if(mes.getImageMark() != null) {
//							contents += mes.getImageMark() + Constants.LEFT_SLASH;
//						}else{
//							contents += Constants.NULL + Constants.LEFT_SLASH;
//						}
					}
					
				}
				//System.out.print("这该死的样例是这样的：" + contents);
				backMsg.setContent(contents);
				sendMsg(channel, backMsg);
			}
			
			
		}
		// TODO 现在的场景还不需要
		if (null != message && Constants.EXIT_MSG.equals(message.getType())) {
			clientMap.remove(map.get(channel.remoteAddress()));
			map.remove(channel.remoteAddress());
		}
		// 普通、抖动
		if (null != message && Constants.GENRAL_MSG.equals(message.getType()) 
				|| Constants.SHAKE_MSG.equals(message.getType())) {
			Message backMsg = new Message();
			Message backMessage = new Message();
			
			// 表明为普通消息（区分服务器回文）
			if (Constants.GENRAL_MSG.equals(message.getType())) {
				backMsg.setType(Constants.GENRAL_MSG);
			}
			if (Constants.SHAKE_MSG.equals(message.getType())) {
				backMsg.setType(Constants.SHAKE_MSG);
			}
			// 双方基本信息
			backMsg.setSenderId(message.getSenderId());
			backMsg.setSenderName(message.getSenderName());
			backMsg.setReceiverId(message.getReceiverId());
			backMsg.setReceiverName(message.getReceiverName());
			// 发送方IP
			String info = channel.remoteAddress().toString();
			backMsg.setSenderAddress(info.substring(1, info.indexOf(":")));
			backMsg.setSenderPort(info.substring(info.indexOf(":") + 1, info.length()));
			
			// 消息内容
			if (Constants.GENRAL_MSG.equals(message.getType())) {
				backMsg.setContent(message.getContent());
				// 样式
				backMsg.setSize(message.getSize());
				backMsg.setFamily(message.getFamily());
				backMsg.setFore(message.getFore());
				backMsg.setBack(message.getBack());
				backMsg.setStyle(message.getStyle());
				// 图片
				backMsg.setImageMark(message.getImageMark());
				backMsg.setSendTime(TimeUtil.getCurrentTimeForServer());
			}
			// 接收方IP
			Channel temp = clientMap.get(message.getReceiverId());
			if(Constants.FRIEND.equals(message.getSenderType())) {
				if(temp != null) {
					String tempInfo = temp.remoteAddress().toString();
					backMsg.setReceiverAddress(tempInfo.substring(1, tempInfo.indexOf(":")));
					backMsg.setReceiverPort(tempInfo.substring(tempInfo.indexOf(":") + 1, tempInfo.length()));
					
				}
				backMsg.setSenderType(Constants.FRIEND);
				charRecordDao.saveMessage(backMsg);
				//对方是否在线
				if (null != clientMap.get(message.getReceiverId())) {
					sendMsg(clientMap.get(message.getReceiverId()), backMsg);
				}else {
					boolean flag = offLineDao.saveMessage(backMsg);
					if(!flag) {
						// TODO 离线消息发送失败  ，系统消息待实现
					}
				}
			}else{
				backMsg.setSenderType(Constants.GROUPCHAT);
				List<Integer> userIdList = groupUserDao.selectUserIdByGroupId(message.getReceiverId());
				if(userIdList != null && userIdList.size() >= 0) {
					for(Integer userId : userIdList) {
						System.out.println("用户id为：" + userId);
						if(!message.getSenderId().equals(String.valueOf(userId))){
							temp = clientMap.get(String.valueOf(userId));
							System.out.println("获取channel");
							if(temp != null) {
								String tempInfo = temp.remoteAddress().toString();
								backMsg.setReceiverAddress(tempInfo.substring(1, tempInfo.indexOf(":")));
								backMsg.setReceiverPort(tempInfo.substring(tempInfo.indexOf(":") + 1, tempInfo.length()));
								sendMsg(clientMap.get(String.valueOf(userId)), backMsg);
							}else {
								backMsg.setReceiverAddress(String.valueOf(userId));
								boolean flag = offLineDao.saveMessage(backMsg);
								if(!flag) {
									// TODO 离线消息发送失败  ，系统消息待实现
								}
							}
						}
					}
				}
			}
			
			
			
		}
		// 注册
		if (null != message && Constants.REGISTER_MSG.equals(message.getType())) {
			Message backMsg = register(message, channel);
			backMsg.setPalindType(Constants.REGISTER_MSG);
			sendMsg(channel, backMsg);
		}
		//修改个性签名
		if(null != message && Constants.CHANGE_SIGNINFO.equals(message.getType())) {
			String msgs[] = message.getContent().split(Constants.LEFT_SLASH);
			userDao.updateSign(msgs[0], msgs[1]);
			
		}
		//修改密码
		if(null != message && Constants.CHANGE_PASSWORD.equals(message.getType())) {
			Message backMsg = new Message();
			backMsg.setType(Constants.PALIND_MSG);
			backMsg.setPalindType(Constants.ECHO_CHANGE_PASSWORD);
			String msgs[] = message.getContent().split(Constants.LEFT_SLASH);
			String password = userDao.getPasswordById(msgs[2]);
			if(msgs[0].equals(password)) {
				userDao.updatePassword(msgs[1], msgs[2]);
				System.out.println(msgs[3]);
				setupDao.changeInfo(msgs[3]);
				backMsg.setStatus(Constants.SUCCESS);
			}else {
				backMsg.setStatus(Constants.FAILURE);
			}
			sendMsg(channel, backMsg);
			
		}
		//修改个人信息
		if(null != message && Constants.CHANGE_INFO.equals(message.getType())) {
			boolean flag = false;
			Message backMsg = new Message();
			backMsg.setType(Constants.PALIND_MSG);
			String msgs[] = message.getContent().split(Constants.LEFT_SLASH);
			System.out.println(msgs[0]);
			System.out.println(msgs[1]);
			System.out.println(msgs[2]);
			System.out.println(msgs[3]);
			User user = userDao.getById(msgs[3]);
			if(!user.getNickName().equals(msgs[0])) {
				System.out.println("执行到这里 ，昵称被修改");
				if(userDao.getByNickName(msgs[0]) != null) {
					System.out.println("执行到这里 ，昵称已经重复");
					backMsg.setPalindType(Constants.ECHO_CHANGE_INFO_FAILURE);
					backMsg.setStatus(Constants.FAILURE);
					sendMsg(channel, backMsg);
				}else {
					System.out.println("执行到这里 ，昵称被修改但是未重复");
					if(msgs[1].equals("***********")) {
						flag = userDao.updateUser(msgs[0], msgs[2], msgs[3]);
						backMsg.setPalindType(Constants.ECHO_CHANGE_INFO_WITHOUT_PASSWORD);
					}else {
						flag = userDao.updateUser(msgs[0], msgs[1], msgs[2], msgs[3]);
						backMsg.setPalindType(Constants.ECHO_CHANGE_INFO_WITH_PASSWORD);
					}
					if(flag) {
						backMsg.setStatus(Constants.SUCCESS);
					}else {
						backMsg.setStatus(Constants.FAILURE);
					}
					sendMsg(channel, backMsg);
				}
			}else {
				System.out.println("执行到这里 ，昵称未被修改");
				if(msgs[1].equals("***********")) {
					System.out.println("执行到这里 ，面膜未被修改");
					flag = userDao.updateUser(msgs[0], msgs[2], msgs[3]);
					backMsg.setPalindType(Constants.ECHO_CHANGE_INFO_WITHOUT_PASSWORD);
				}else {
					System.out.println("执行到这里 ，密码被修改");
					flag = userDao.updateUser(msgs[0], msgs[1], msgs[2], msgs[3]);
					backMsg.setPalindType(Constants.ECHO_CHANGE_INFO_WITH_PASSWORD);
				}
				if(flag) {
					backMsg.setStatus(Constants.SUCCESS);
				}else {
					backMsg.setStatus(Constants.FAILURE);
				}
				sendMsg(channel, backMsg);
			}
			
			
		}
		// 修改
		if (null != message && Constants.INFO_MSG.equals(message.getType())) {
			Message backMsg = update(message, channel);
			sendMsg(channel, backMsg);
		}
		// 请求添加好友
		if (null != message && Constants.REQUEST_ADD_MSG.equals(message.getType())) {
			Message backMsg = new Message();
			// cateId/account
			String content[] = message.getContent().split(Constants.LEFT_SLASH);
			User user = userDao.getByAccountOrNickName(content[1]);
			if (null != user) {
				// 告诉发送者
				Category gory = cateDao.getById(content[0]);
				CateMember cm = memberDao.getByCidAndMid(gory.getId(), user.getId());
				if (null != cm) {// 已经是你的好友
					backMsg.setContent("对方已经是您的好友，请不要重复添加！");
					backMsg.setStatus(Constants.FAILURE);
					backMsg.setType(Constants.PALIND_MSG);
					backMsg.setPalindType(Constants.REQUEST_ADD_MSG);
					sendMsg(channel, backMsg);
				} else {
					backMsg.setContent("请求已发送，静待对方回答！");
					backMsg.setType(Constants.PALIND_MSG);
					backMsg.setPalindType(Constants.REQUEST_ADD_MSG);
					sendMsg(channel, backMsg);
					// 告诉接收者
					backMsg.setType(Constants.REQUEST_ADD_MSG);
					backMsg.setSenderId(message.getSenderId());
					backMsg.setSenderName(message.getSenderName());
					backMsg.setContent(content[0]);// 将群组id带过去
					if(clientMap.get(user.getId()) != null ) {
						sendMsg(clientMap.get(user.getId()), backMsg);
					}else {
						backMsg.setReceiverId(user.getId());
						backMsg.setSize(0);
						backMsg.setBack(0);
						backMsg.setFore(0);
						backMsg.setStyle(0);
						offLineDao.saveMessage(backMsg);
					}
					
				}
			} else {
				backMsg.setType(Constants.PALIND_MSG);
				backMsg.setStatus(Constants.FAILURE);
				backMsg.setPalindType(Constants.REQUEST_ADD_MSG);
				backMsg.setContent("用户不存在！");
				sendMsg(channel, backMsg);
			}
		}
		// 回应添加好友
		if (null != message && Constants.ECHO_ADD_MSG.equals(message.getType())) {//为啥默认我的好友里面会有呢
			// cateId/yes、no
			String content[] = message.getContent().split(Constants.LEFT_SLASH);
			if (Constants.YES.equals(content[1])) {
				// 要保存的成员Id
				String memberId = message.getSenderId();// 回应方
				String ownerId = message.getReceiverId();// 请求方
				// 把回应方也加到请求方的好友列表
				// 请求方
				Category cate = cateDao.getById(content[0]);
				if (null != cate) {
					CateMember cateMember = memberDao.saveCateMember(cate.getId(), ownerId, memberId);
					if (null != cateMember) {
						User self = userDao.getById(ownerId);
						User friend = userDao.getById(memberId);
						List<Category> cateList = cateDao.getListByUIdAndType(self.getId(), Constants.USER);
						
						Message backMsg = new Message();
						backMsg.setType(Constants.PALIND_MSG);
						backMsg.setUser(self);
						backMsg.setCategoryList(cateList);
						backMsg.setMemberList(getMemberList(cateList));
						backMsg.setFriend(friend);
						
						backMsg.setPalindType(Constants.REQUEST_ADD_MSG);
						backMsg.setSenderName(message.getSenderName());
						backMsg.setContent(cate.getId());
						backMsg.setStatus(Constants.SUCCESS);
						sendMsg(clientMap.get(ownerId), backMsg);
					}
				}
				// 把请求方也加到回应方的好友列表
				// 默认分组：我的好友
				// 回应方
				Category cate2 = cateDao.getByCondition(memberId, Constants.USER, Constants.DEFAULT_CATE);
				if (null != cate2) {
					CateMember cateMember2 = memberDao.saveCateMember(cate2.getId(), memberId, ownerId);
					if (null != cateMember2) {
						// TODO 通知自己
						User user = userDao.getById(memberId);
						User friend = userDao.getById(ownerId);
						List<Category> cateList = cateDao.getListByUIdAndType(user.getId(), Constants.USER);
						
						Message backMsg = new Message();
						backMsg.setType(Constants.PALIND_MSG);
						backMsg.setUser(user);
						backMsg.setCategoryList(cateList);
						backMsg.setMemberList(getMemberList(cateList));
						backMsg.setFriend(friend);
						
						backMsg.setPalindType(Constants.ECHO_ADD_MSG);
						backMsg.setContent(cate2.getId());
						backMsg.setStatus(Constants.SUCCESS);
						sendMsg(channel, backMsg);
					}
				}
			}
			if (Constants.NO.equals(content[1])) {
				Message backMsg = new Message();
				backMsg.setType(Constants.PALIND_MSG);
				backMsg.setStatus(Constants.FAILURE);
				backMsg.setPalindType(Constants.REQUEST_ADD_MSG);
				backMsg.setContent(message.getSenderName() + "拒绝了您的好友请求！");
				sendMsg(clientMap.get(message.getReceiverId()), backMsg);
			}
		}
		// 删除分组
		if (null != message && Constants.DELETE_USER_CATE_MSG.equals(message.getType())) {
			Message backMsg = new Message();
			backMsg.setType(Constants.PALIND_MSG);
			backMsg.setPalindType(Constants.DELETE_USER_CATE_MSG);
			// 默认分组不允许删除
			// 这个判断也可以写到客户端
			String content[] = message.getContent().split(Constants.LEFT_SLASH);
			Category cate = cateDao.getById(content[0]);
			List<CateMember> list = memberDao.getListByCateId(cate.getId());
			cateDao.deleteCate(cate.getId());// 删除分组
			for (CateMember cm : list) {// 删除分组下的人
				memberDao.delete(cm.getId());
				// 删除对方的数据
				memberDao.deleteByOidAndMid(cm.getMemberId(), message.getSenderId());
				Message backMsg2 = new Message();
				backMsg2.setType(Constants.PALIND_MSG);
				backMsg2.setPalindType(Constants.DELETE_USER_MEMBER_MSG);
				backMsg2.setContent(message.getSenderName());
				sendMsg(clientMap.get(cm.getMemberId()), backMsg2);
			}
			// 回文
			backMsg.setContent(cate.getId());
			backMsg.setList(getMemberNickList(list));
			sendMsg(channel, backMsg);
		}
		//删除群聊
		if(null != message && Constants.DELETE_GROUP.equals(message.getType())) {
			Message backMsg = new Message();
			String content[] = message.getContent().split(Constants.LEFT_SLASH);
			groupUserDao.deleteByGroupIdAndUserId(content[0], content[1]);
			backMsg.setType(Constants.PALIND_MSG);
			backMsg.setPalindType(Constants.ECHO_DELETE_GROUP);
			backMsg.setContent(message.getReceiverName());
			sendMsg(channel, backMsg);
		}
		//创建群聊
		if(null != message && Constants.CREATE_GROUP.equals(message.getType())) {
			Message backMsg = new Message();
			String groupName = message.getContent();
			groupDao.saveGroup(groupName);
			String groupId = groupDao.selectGroupIdByGroupName(groupName);
			
			backMsg.setType(Constants.PALIND_MSG);
			backMsg.setPalindType(Constants.ECHO_CREATE_GROUP);
			backMsg.setContent(groupName);
			if(groupId == null) {
				backMsg.setStatus(Constants.FAILURE);
			}else {
				groupUserDao.Save(groupId, message.getSenderId());
				backMsg.setSenderName(groupName);
				backMsg.setSenderId(groupId);
				backMsg.setStatus(Constants.SUCCESS);
				sendMsg(channel, backMsg);
				
			}
		}
		//加入群聊
		if(null != message && Constants.ADD_GROUP.equals(message.getType())) {
			Message backMsg = new Message();
			String groupName = message.getContent();
//			backMsg.setSenderName(message.getSenderName());
//			backMsg.setSenderId(message.getSenderId());
			backMsg.setType(Constants.PALIND_MSG);
			backMsg.setPalindType(Constants.ECHO_ADD_GROUP);
			backMsg.setContent(groupName);
			String groupId = groupDao.selectGroupIdByGroupName(groupName);
			//sendMsg(channel, backMsg);
			if(groupId == null) {
				backMsg.setStatus(Constants.FAILURE);
			}else {
				groupUserDao.Save(groupId, message.getSenderId());
				backMsg.setSenderName(groupName);
				backMsg.setSenderId(groupId);
				backMsg.setStatus(Constants.SUCCESS);
				sendMsg(channel, backMsg);
			}
		}
		// 删除成员
		if (null != message && Constants.DELETE_USER_MEMBER_MSG.equals(message.getType())) {
			Message backMsg = new Message();
			backMsg.setType(Constants.PALIND_MSG);
			backMsg.setPalindType(Constants.DELETE_USER_MEMBER_MSG);
			// cateId/memberId
			String content[] = message.getContent().split(Constants.LEFT_SLASH);
			User user = userDao.getById(content[1]);
			memberDao.deleteByCidAndMid(content[0], content[1]);
			backMsg.setContent(user.getNickName());
			sendMsg(channel, backMsg);
			// 将对方好友中的你也移除掉
			memberDao.deleteByOidAndMid(user.getId(), message.getSenderId());
			Message backMsg2 = new Message();
			backMsg2.setType(Constants.PALIND_MSG);
			backMsg2.setPalindType(Constants.DELETE_USER_MEMBER_MSG);
			backMsg2.setContent(message.getSenderName());
			sendMsg(clientMap.get(user.getId()), backMsg2);
		}
		// 添加分组
		if (null != message && Constants.ADD_USER_CATE_MSG.equals(message.getType())) {
			// 暂时只处理好友列表,QQ群什么的暂时不管
			Message backMsg = new Message();
			backMsg.setType(Constants.PALIND_MSG);
			backMsg.setPalindType(Constants.ADD_USER_CATE_MSG);
			Category cate = cateDao.saveCategory(message.getSenderId(), 
						Constants.USER, message.getContent());
			backMsg.setCategory(cate);
			backMsg.setStatus(Constants.SUCCESS);
			sendMsg(channel, backMsg);
		}
		// 修改分组
		if (null != message && Constants.EDIT_USER_CATE_MSG.equals(message.getType())) {
			String content[] = message.getContent().split(Constants.LEFT_SLASH);
			Category cate = cateDao.editCategory(content[0], content[1]);
			Message backMsg = new Message();
			backMsg.setType(Constants.PALIND_MSG);
			backMsg.setPalindType(Constants.EDIT_USER_CATE_MSG);
			backMsg.setCategory(cate);
			sendMsg(channel, backMsg);
		}
		System.err.println("map大小:" + map.size());
		System.err.println("clientMap大小:" + clientMap.size());
	}
	
	/**
	 * 分组下面的成员昵称
	 * @param list 分组下的成员集合
	 * @return
	 */
	private List<String> getMemberNickList(List<CateMember> list) {
		List<String> strList = new ArrayList<String>();
		if (null != list && list.size() > 0) {
			for (CateMember cm : list) {
				User user = userDao.getById(cm.getMemberId());
				strList.add(user.getNickName());
			}
			return strList;
		}
		return null;
	}

	private Message update(Message message, Channel channel) {
		return null;
	}

	private Message register(Message message, Channel channel) {
		User user = null;
		String msgStr[] = message.getContent().split(Constants.LEFT_SLASH);
		if (null != userDao.getByNickName(msgStr[0])) {
			return new Message(Constants.PALIND_MSG, Constants.REGISTER_MSG, "昵称已被占用，请重新输入！");
		}
		if (null != userDao.getByUserName(msgStr[1])) {
			return new Message(Constants.PALIND_MSG, Constants.REGISTER_MSG, "账号已经注册，请尝试找回密码！");
		}
		if (msgStr.length < 4) {
			user = userDao.saveUser(msgStr[0], msgStr[1], msgStr[2], null);
		} else {
			user = userDao.saveUser(msgStr[0], msgStr[1], msgStr[2], msgStr[3]);
		}
		if (null != user) {
			// 创建一个默认分组
			Category cate = cateDao.saveCategory(user.getId(), Constants.USER, Constants.DEFAULT_CATE);
			if (null != cate) {
				return new Message(Constants.PALIND_MSG, Constants.REGISTER_MSG, Constants.SUCCESS);
			}
		}
		return new Message(Constants.PALIND_MSG, Constants.REGISTER_MSG, Constants.FAILURE);
	}

	private Message login(Message message, Channel channel) {
		String content = message.getContent();
		String msgStr[] = content.split(Constants.LEFT_SLASH);
		User user = userDao.getByUserName(msgStr[0]);
		if (null == user) {
			return new Message(Constants.PALIND_MSG, Constants.LOGIN_MSG, "该账号不存在！");
		}
		user = userDao.login(msgStr[0], msgStr[1]);
		if (null == user) {
			return new Message(Constants.PALIND_MSG, Constants.LOGIN_MSG, "账号或密码输入有误！");
		}
		if (clientMap.containsKey(user.getId())) {
			return new Message(Constants.PALIND_MSG, Constants.LOGIN_MSG, "请不要重复登录！");
		}
		// 保存客户端
		clientMap.put(user.getId(), channel);
		map.put(channel.remoteAddress(), user.getId());
		
		
		
		// TODO 暂时不管群组
		List<String> ids = groupUserDao.selectGroupByUserId(user.getId());
		List<Group> groupList = groupDao.selectGroupById(ids);
		List<Category> cateList = cateDao.getListByUIdAndType(user.getId(), Constants.USER);
		return new Message(Constants.PALIND_MSG, user, cateList, getMemberList(cateList), groupList);
	}
	
	/**
	 * getMemberList: 获取分组以及分组下面的人	<br/>
	 * @author SongFei
	 * @param cateList 分组集合
	 * @return List	<br/>
	 * @since JDK 1.7
	 */
	private List<Map<String, List<User>>> getMemberList(List<Category> cateList) {
		List<Map<String, List<User>>> memberList = new ArrayList<Map<String, List<User>>>();
		if (null != cateList && cateList.size() > 0) {
			for (Category cate : cateList) {
				Map<String, List<User>> map = new HashMap<String, List<User>>();// 分组信息Map
				List<User> list = new ArrayList<User>();// 分组下好友集合
				List<CateMember> cmList = memberDao.getListByCateId(cate.getId());
				if (null != cmList && cmList.size() > 0) {
					for (CateMember cm : cmList) {
						User friend = userDao.getById(cm.getMemberId());
						list.add(friend);
					}
				}
				map.put(cate.getId(), list);
				memberList.add(map);
			}
		}
		return memberList;
	}

}
