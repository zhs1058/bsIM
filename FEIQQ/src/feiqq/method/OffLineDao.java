package feiqq.method;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import feiqq.bean.Message;

public class OffLineDao extends BaseDao {
	
	/*
	 * 保存离线数据
	 * @param Message 
	 * 
	 */
	public boolean saveMessage(Message message) {
		String sql = " insert into fqq_offline (sender_id , sender_name , sender_address ,"
				+ " sender_port , receiver_id , receiver_name , receiver_address , "
				+ "receiver_port , content , type , palind_type , size , family , "
				+ "back , fore , style , image_mark ) values ("
				+ "'" + message.getSenderId() + "',"+ "'" + message.getSenderName() + "',"
				+ "'" + message.getSenderAddress() + "','" + message.getSenderPort() + "',"
				+ "'" + message.getReceiverId() + "','" + message.getReceiverName() +"',"
				+ "'" + message.getReceiverAddress() + "','" + message.getReceiverPort()+"',"
				+ "'" + message.getContent() + "','" + message.getType() +"',"
				+ "'" + message.getPalindType() + "','" + message.getSize() +"',"
				+ "'" + message.getFamily() + "','" + message.getBack() +"',"
				+ "'" + message.getFore() + "','" + message.getStyle()+"',"
				+ "'" + message.getImageMark() + "')" ;
		int num = operate(sql);
		if(num > 0) {
			return true ;
		}else {
			return false;
		}
	}
	
	/*
	 * 查询留言信息
	 */
	public List<Message> getOffLineMessage(String receiverId){
		List<Message> listMessage = new ArrayList<>();
		String sql = "select * from fqq_offline fol where  fol.receiver_id = '" + receiverId + "'";
		ResultSet result = select(sql);
		try {
			while(result != null && result.next()) {
				Message message = assembleMessage(result);
				listMessage.add(message);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listMessage;
	}
	
	/*
	 * 删除留言信息
	 */
	public void deleteMessage(String receiverId) {
		String sql = "delete from fqq_offline fo where fo.receiver_id = '" + receiverId +  "'";
		operate(sql);
	}
	private Message assembleMessage(ResultSet result) {
		Message message = new Message();
		
		try {
			message.setSenderId(result.getString("sender_id"));
			message.setSenderName(result.getString("sender_name"));
			message.setSenderAddress(result.getString("sender_address"));
			message.setSenderPort(result.getString("sender_port"));
			
			message.setReceiverId(result.getString("receiver_id"));
			message.setReceiverName(result.getString("receiver_name"));
			message.setReceiverAddress(result.getString("receiver_address"));
			message.setReceiverPort(result.getString("receiver_port"));
			
			message.setContent(result.getString("content"));
			message.setType(result.getString("type"));
			message.setPalindType(result.getString("palind_type"));
			
			message.setSize(result.getInt("size"));
			message.setFamily(result.getString("family"));
			message.setBack(result.getInt("back"));
			message.setFore(result.getInt("fore"));
			message.setStyle(result.getInt("style"));
			
			message.setImageMark(result.getString("image_mark"));
			
			return message;
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message;
	}

}
