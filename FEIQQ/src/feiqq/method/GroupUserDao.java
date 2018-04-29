package feiqq.method;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import feiqq.bean.Group;

public class GroupUserDao extends BaseDao {
	
	/*
	 * 保存
	 */
	public void Save(String groupId, String userId) {
		String sql = "insert into fqq_group_user (group_id , user_id) values ('"
				+ Integer.parseInt(groupId) + "','"
				+ Integer.parseInt(userId) + "')";
		operate(sql);
	}

	/*
	 * 通过成员id查询群组
	 */
	public List<String> selectGroupByUserId(String userId){
		List<String> groupIdList = new ArrayList<>();
		String sql = "select group_id from fqq_group_user where user_id = '" + userId + "'";
		ResultSet result = select(sql);
		try {
			while(result != null && result.next()) {
				String groupId = result.getString("group_id");
				groupIdList.add(groupId);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return groupIdList;
		
	}
	
	/*
	 * 通过群组id查询成员id
	 */
	public List<Integer> selectUserIdByGroupId(String groupId){
		List<Integer> userIdList = new ArrayList<>();
		String sql = "select user_id from fqq_group_user where group_id = '" + groupId + "'";
		ResultSet result = select(sql);
		try {
			while(result != null && result.next()) {
				Integer userId = result.getInt("user_id");
				userIdList.add(userId);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return userIdList;
	}
	
	/*
	 * 通过群id和用户id退出群聊
	 */
	public void deleteByGroupIdAndUserId(String groupId, String userId) {
		String sql = "delete from fqq_group_user where group_id = '"
				+ Integer.parseInt(groupId) + "' and "
				+ "user_id = '" + Integer.parseInt(userId) + "'";
		operate(sql);
	}

	
}
