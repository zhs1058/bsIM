package feiqq.method;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import feiqq.bean.Message;

public class CharRecordDao extends BaseDao {
	
	/*
	 * 保存聊天数据
	 * @param Message 
	 * 
	 */
	public boolean saveMessage(Message message) {
//		System.out.println("长度为：" + message.getContent().length());
//		if(message.getContent() == "") {
//			message.setContent("***");
//		}
		String condition = message.getSenderName() + "_" + message.getReceiverName();
		String sql = " insert into fqq_char_record (s_r , time , sender_id , sender_name ,"
				+ " receiver_id , receiver_name , "
				+ "content , type , palind_type , sender_type , size , family , "
				+ "back , fore , style , image_mark ) values ("
				+ "'" + condition + "','" + message.getSendTime() + "',"
				+ "'" + message.getSenderId() + "',"+ "'" + message.getSenderName() + "',"
				+ "'" + message.getReceiverId() + "','" + message.getReceiverName() +"',"
				+ "'" + message.getContent() + "','" + message.getType() +"',"
				+ "'" + message.getPalindType() + "','" + message.getSenderType() + "','"+ message.getSize() +"',"
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
	 * 查询聊天信息
	 */
	public List<Message> getCharRecord(String receiverName , String senderName){
		String condition1 = senderName + "_" + receiverName;
		String condition2 = receiverName + "_" + senderName;
		List<Message> listMessage = new ArrayList<>();
		String sql = "select * from fqq_char_record where  s_r = '" + condition1 + "' or s_r = '" + condition2 + "' order by time asc";
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
		String sql = "delete from fqq_offline where receiver_id = '" + receiverId +  "'";
		operate(sql);
	}
	private Message assembleMessage(ResultSet result) {
		Message message = new Message();
		
		try {
			message.setSendTime(result.getString("time"));
			
			message.setSenderId(result.getString("sender_id"));
			message.setSenderName(result.getString("sender_name"));
			
			message.setReceiverId(result.getString("receiver_id"));
			message.setReceiverName(result.getString("receiver_name"));
			
			message.setContent(result.getString("content"));
			message.setType(result.getString("type"));
			message.setPalindType(result.getString("palind_type"));
			message.setSenderType(result.getString("sender_type"));
			
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
