package feiqq.bean;

public class Setup {

	private String userName;
	private String userPassword;
	private boolean autoLogin;
	private boolean savePassword;
	private String macAddress;
	
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public boolean isAutoLogin() {
		return autoLogin;
	}
	public void setAutoLogin(boolean autoLogin) {
		this.autoLogin = autoLogin;
	}
	public boolean isSavePassword() {
		return savePassword;
	}
	public void setSavePassword(boolean savePassword) {
		this.savePassword = savePassword;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	
}
