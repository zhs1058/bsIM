package feiqq.method;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import feiqq.bean.Group;

public class GroupUserDao extends BaseDao {

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

	
}
