package feiqq;

import feiqq.socket.server.Server;
import feiqq.util.MailUtil;

public class StartServer {

	public static void main(String[] args) {
		try {
			Server.getInstance();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
}
