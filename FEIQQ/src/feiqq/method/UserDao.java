package feiqq.method;

import java.sql.ResultSet;
import java.sql.SQLException;

import feiqq.bean.User;
import feiqq.util.StringUtil;

public class UserDao extends BaseDao {

	/**
	 * login: 登陆   <br/>
	 * @author SongFei
	 * @param name	账号
	 * @param pass	密码
	 * @return	User <br/>
	 * @since JDK 1.7
	 */
	public User login(String name, String pass) {
		String sql = "select * from fqq_user fu where fu.user_name = '"+
				name+"' and fu.user_password = '"+pass+"'";
		ResultSet result = select(sql);
		return assembleUser(result);
	}
	
	/**
	 * saveUser: 保存用户信息 <br/>
	 * @author SongFei
	 * @param nick	昵称
	 * @param name	账号
	 * @param pass	密码
	 * @param sign	签名
	 * @return User	<br/>
	 * @since JDK 1.7
	 */
	public User saveUser(String nick, String name, String pass, String sign) {
		String sql = "insert into fqq_user (nick_name , user_name , user_password , user_signature ) values('"+nick+"','"+name+"','"+pass+"' ";
		if (!StringUtil.isEmpty(sign)) {
			sql += " ,'"+sign+"' ";
		}
		sql += ")";
		int num =  operate(sql);
		if (num > 0) {
			return getByUserName(name);
		}
		return null;
	}
	/*
	 * 修改信息有密码
	 */
	public boolean updateUser(String nick, String pass, String sign, String id) {
		String sql = "update fqq_user set nick_name = '" + nick + "', user_password = '" + pass + "' , user_signature = '" + sign + "' "
				+ "where id = '" + Integer.parseInt(id) + "'";
		int num = operate(sql);
		if(num > 0) return true;
		return false;
	}
	
	public void updatePassword(String password, String id) {
		String sql = "update fqq_user set user_password = '" + password + "' "
				+ "where id = '" + Integer.parseInt(id) + "'";
		operate(sql);
	}
	
	/*
	 * 修改信息无密码
	 */
	public boolean updateUser(String nick, String sign, String id) {
		String sql = "update fqq_user set nick_name = '" + nick + "' " + ",user_signature = '" + sign + "' "
				+ "where id = '" + Integer.parseInt(id) + "'";
		int num = operate(sql);
		if(num > 0) return true;
		return false;
	}
	
	/*
	 * 更新个性签名
	 */
	public void updateSign(String sign , String id) {
		String sql = "update fqq_user set user_signature = '" + sign + "' "
				+ "where id = '" + Integer.parseInt(id) + "'";
		operate(sql);
	}
	
	/**
	 * getById: 通过ID查询	<br/>
	 * @author SongFei
	 * @param id	用户ID（数据库记录ID）
	 * @return User	<br/>
	 * @since JDK 1.7
	 */
	public User getById(String id) {
		String sql = "select * from fqq_user fu where fu.id = " + Integer.valueOf(id);
		ResultSet result = select(sql);
		return assembleUser(result);
	}
	
	public String getPasswordById(String id) {
		String sql = "select * from fqq_user fu where fu.id = " + Integer.valueOf(id);
		ResultSet result = select(sql);
		try {
			if(result != null && result.next()) {
				return result.getString("user_password");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * getByUserName: 通过账号查询 	<br/>
	 * @author SongFei
	 * @param value	账号
	 * @return User	<br/>
	 * @since JDK 1.7
	 */
	public User getByUserName(String value) {
		String sql = "select * from fqq_user fu where fu.user_name = '" + value + "'";
		ResultSet result = select(sql);
		return assembleUser(result);
	}
	
	/**
	 * getByNickName: 通过昵称查询	<br/>
	 * @author SongFei
	 * @param value	昵称
	 * @return User	<br/>
	 * @since JDK 1.7
	 */
	public User getByNickName(String value) {
		String sql = "select * from fqq_user fu where fu.nick_name = '"+value+"' ";
		ResultSet result = select(sql);
		return assembleUser(result);
	}
	
	/**
	 * getByAccountOrNickName: 通过账号或昵称查询	<br/>
	 * @author SongFei
	 * @param value	账号或昵称
	 * @return User	<br/>
	 * @since JDK 1.7
	 */
	public User getByAccountOrNickName(String value) {
		String sql = "select * from fqq_user where user_name = '" +
				value + "' or nick_name = '"+value+"' ";
		ResultSet result = select(sql);
		return assembleUser(result);
	}
	
	/**
	 * 删除用户
	 * @param id 用户Id
	 */
	public int deleteUser(String id) {
		String sql = "delete from fqq_user where id = " + Integer.valueOf(id);
		return operate(sql);
	}
	
	/*
	 * 通过用户名查找密码
	 */
	public String findPassWordByUserName(String userName) {
		String passWord = null;
		String sql  = "select * from fqq_user where user_name = '" + userName + "'";
		ResultSet result = select(sql);
		try {
			if(null != result && result.next()) {
				passWord = result.getString("user_password");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return passWord;
	}
	
	// 组装User对象
	private User assembleUser(ResultSet result) {
		try {
			if (null != result && result.next()) {
				String id = String.valueOf(result.getInt("id"));
				String userName = result.getString("user_name");
				String nickName = result.getString("nick_name");
				String signature = result.getString("user_signature");
				return new User(id, userName, nickName, signature);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
