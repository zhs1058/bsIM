package feiqq.socket.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class ClientListener implements ChannelFutureListener {

	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		if (future.isSuccess()) {
			System.out.println("客户端操作完成！");
		} else {
			System.out.println("客户端操作失败！");
		}
	}

}
