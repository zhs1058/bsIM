package feiqq.method;

import java.sql.ResultSet;
import java.sql.SQLException;

import feiqq.bean.Setup;
import feiqq.bean.User;

public class SetupDao extends BaseDao {
	
	public void save(Setup setup) {
		int autoLogin = 0;
		int savePassword = 0;
		if(setup.isAutoLogin()) {
			autoLogin = 1;
		}
		if(setup.isSavePassword()) {
			savePassword = 1;
		}
		String sql = "insert into fqq_setup (user_name , user_password , auto_login , save_password , mac_address) values ('"
				+ setup.getUserName() + "','"+ setup.getUserPassword() + "','" + autoLogin + "','" 
				+ savePassword + "','" + setup.getMacAddress() + "')";
		operate(sql);
	}
	
	public Setup getMessageByMac(String mac) {
		String sql = "select * from fqq_setup where mac_address = '" + mac + "'";
		ResultSet result = select(sql);
		if(result == null) {
			return null;
		}
		return assembleSetup(result);
		
	}
	
	public void update(Setup setup) {
		int autoLogin = 0;
		int savePassword = 0;
		if(setup.isAutoLogin()) {
			autoLogin = 1;
		}
		if(setup.isSavePassword()) {
			savePassword = 1;
		}
		String sql = "update fqq_setup set user_name = '" + setup.getUserName() + "', user_password = '" 
					+ setup.getUserPassword() + "', auto_login = '" + autoLogin + "' , save_password = '" 
					+ savePassword + "' where mac_address = '" + setup.getMacAddress() + "'";
		operate(sql);
	}
	
	public void changeInfo(String mac) {
		String sql = "update fqq_setup set auto_login = '0' where mac_address = '" + mac + "'";
		operate(sql);
	}
	
	public void delSetup(String mac) {
		String sql = "delete from fqq_setup where mac_address = '" + mac + "'";
		operate(sql);
	}
	
	private Setup assembleSetup(ResultSet result) {
		try {
			if (null != result && result.next()) {
				Setup setup = new Setup();
				setup.setUserName(result.getString("user_name"));
				setup.setUserPassword(result.getString("user_password"));
				if(result.getInt("auto_login") == 1) {
					setup.setAutoLogin(true);
				}else {
					setup.setAutoLogin(false);
				}
				if(result.getInt("save_password") == 1) {
					setup.setSavePassword(true);
				}else {
					setup.setSavePassword(false);
				}
				return setup;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
