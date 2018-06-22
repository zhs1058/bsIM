package feiqq.util;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

/**
 * Description: 工具类 <br/>
 * 
 * 
 * @version
 * @since JDK 1.7
 * @see
 */
public class Constants {

	// 服务器IP，一般来说就是本机
	public static String SERVER_IP = "127.0.0.1";
	// 服务器port
	public static int SERVER_PORT = 5555;

	// 消息类型
	/** 群聊普通 */
	public static String GROUP_GENRAL_MSG = "-1";
	/** 普通 */
	public static String GENRAL_MSG = "0";
	/** 抖动 */
	public static String SHAKE_MSG = "1";
	/** 回文 */
	public static String PALIND_MSG = "2";
	/** 图片 */
//	感觉用不上
//	public static String PICTURE_MSG = "3";
	/** 登录 */
	public static String LOGIN_MSG = "4";
	/** 退出 */
	public static String EXIT_MSG = "5";
	/** 注册 */
	public static String REGISTER_MSG = "6";
	/** 用户信息(修改、查看) */
	public static String INFO_MSG = "7";
	/** 请求添加好友 */
	public static String REQUEST_ADD_MSG = "8";
	/** 回应添加好友 */
	public static String ECHO_ADD_MSG = "9";
	/** 删除分组（user） */
	public static String DELETE_USER_CATE_MSG = "10";
	/** 删除成员（user） */
	public static String DELETE_USER_MEMBER_MSG = "11";
	/** 添加分组（user） */
	public static String ADD_USER_CATE_MSG = "12";
	/** 修改分组（user） */
	public static String EDIT_USER_CATE_MSG = "13";
	/** 好友 */
	public static String FRIEND = "20";
	/** 群聊 */
	public static String GROUPCHAT = "21";
	/** 退出群聊 */
	public static String DELETE_GROUP = "22";
	/** 添加群聊*/
	public static String ADD_GROUP = "23";
	/** 查找群聊*/
	public static String SEARCH_GROUP = "24";
	/** 回应退出群聊 */
	public static String ECHO_DELETE_GROUP = "25";
	/** 回应添加群聊*/
	public static String ECHO_ADD_GROUP = "26";
	/** 回应查找群聊*/
	public static String ECHO_SEARCH_GROUP = "27";
	/** 创建群聊*/
	public static String CREATE_GROUP = "28";
	/** 回应创建群聊*/
	public static String ECHO_CREATE_GROUP = "29";
	/** 修改个人信息*/
	public static String CHANGE_INFO = "30";
	/** 回应修改个人信息(修改密码) */
	public static String ECHO_CHANGE_INFO_WITH_PASSWORD = "31";
	/** 回应修改个人信息(未修改密码) */
	public static String ECHO_CHANGE_INFO_WITHOUT_PASSWORD = "32";
	/** 回应修改个人信息失败 */
	public static String ECHO_CHANGE_INFO_FAILURE = "33";
	
	/** 修改个性签名 */
	public static String CHANGE_SIGNINFO = "34";
	/** 回应修改个人信息 */
	public static String ECHO_CHANGE_SIGNINFO = "35";
	
	/** 修改密码 */
	public static String CHANGE_PASSWORD = "36";
	/** 回应修改密码 */
	public static String ECHO_CHANGE_PASSWORD = "37";
	
	/** 找回密码 */
	public static String FIND_PASSWORD = "38";
	/** 回应找回密码 */
	public static String ECHO_FIND_PASSWORD = "39";
	/** 查询预置信息 */
	public static String SEARCH_PRESET_INFO = "40";
	/** 回应查询预置信息 */
	public static String ECHO_SEARCH_PRESET_INFO = "41";
	/** 查询聊天记录 */
	public static String SEARCH_CHAR_RECORD = "42";
	/** 回应查询聊天记录 */
	public static String ECHO_SEARCH_CHAR_RECORD = "43";

	public static String USER = "user";
	public static String GROUP = "group";
	public static String SUCCESS = "success";
	public static String FAILURE = "failure";
	public static String NOTICE = "notice";
	public static String YES = "yes";
	public static String NO = "no";

	/** 空格代码 */
	public static String SPACE = "\u0008";
	/** 换行代码 */
	public static String NEWLINE = "\n";
	/** 左斜杠 */
	public static String LEFT_SLASH = "/";
	public static String STAR = "%";
	/** 横线 */
	public static String LINE = "_";
	
	public static String NULL = "null";

	public static String SEARCH_TXT = "搜索：联系人、群组";
	public static String DEFAULT_CATE = "friends";
	public static String NONAME_CATE = "Unnamed";
	
	// 微软雅黑
	public static Font BASIC_FONT = new Font("微软雅黑", Font.PLAIN, 12);
	public static Font BASIC_FONT2 = new Font("微软雅黑", Font.TYPE1_FONT, 12);
	// 楷体
	public static Font DIALOG_FONT = new Font("楷体", Font.PLAIN, 16);
	public static Font DIALOG_FONT2 = new Font("楷体", Font.PLAIN, 20);
	
	public static Border GRAY_BORDER = BorderFactory.createLineBorder(Color.GRAY);
	public static Border ORANGE_BORDER = BorderFactory.createLineBorder(Color.ORANGE);
	public static Border LIGHT_GRAY_BORDER = BorderFactory.createLineBorder(Color.LIGHT_GRAY);

	public static int NO_OPTION = 1;
	public static int YES_OPTION = 0;
	public static String changeInformationStatic = null;
	public static String historyInfo = null;
}
