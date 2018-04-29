package feiqq.method;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import feiqq.bean.Group;

public class GroupDao extends BaseDao {

	/*
	 * 通过id查询群组
	 */
	public List<Group> selectGroupById(List<String> ids){
		
		List<Group> groupList = new ArrayList<>();
		ResultSet result = null;
		for(String id : ids) {
			String sql = "select * from fqq_group where id = '" + id + "'";
			result = select(sql);
			try {
				while(result != null && result.next()) {
					groupList.add(assembleGroup(result));
				}
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return groupList;
		
		
	}

	private Group assembleGroup(ResultSet result) {
		
		Group group = new Group();
		try {
			group.setId(Integer.toString(result.getInt("id")));
			group.setName(result.getString("name"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return group;
	}
}
