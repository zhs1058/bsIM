package feiqq;


import feiqq.bean.Message;
import feiqq.socket.client.Client;
import feiqq.ui.frame.LoginWindow;
import feiqq.util.Constants;
import feiqq.util.MacUtil;
/*
 * zhanghongsheng
 */
public class StartClient {
	

	public static void main(String[] args) {
		try {
			Client client = new Client();
			//查询预置信息
			String mac = MacUtil.getMac();
			client.sendMsg(new Message(Constants.SEARCH_PRESET_INFO, mac));
			LoginWindow inst = LoginWindow.getInstance(client);
			client.setLogin(inst);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
