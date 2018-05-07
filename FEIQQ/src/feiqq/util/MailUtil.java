package feiqq.util;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtil {
	public static void sendMail(String toM, String code) throws MessagingException {
		String title = "验证码";
		String content = "<h1>【毕设IM】您的验证码为：</h1><h3>" + code + "</h3><h2>如果不是您本人操作，请忽略本邮件</h2>";
		createMail(title, toM, content);
	}
	public static void sendPassMail(String toM, String pass) throws MessagingException {
		String title = "密码找回";
		String content = "<h1>【毕设IM】您的密码为：</h1><h3>" + pass + "</h3><h2>请登陆后，及时修改您的密码</h2>";
		createMail(title, toM, content);
	}
	private static void createMail(String title, String toM, String content) throws MessagingException {
		// 创建Properties 类用于记录邮箱的一些属性
        Properties props = new Properties();
        // 表示SMTP发送邮件，必须进行身份验证
        props.put("mail.smtp.auth", "true");
        //此处填写SMTP服务器
        props.put("mail.smtp.host", "smtp.qq.com");
        //设置是否显示debug信息  true 会在控制台显示相关信息
        //props.put("mail.debug", "true");
        //端口号，QQ邮箱给出了两个端口，但是另一个我一直使用不了，所以就给出这一个587
        props.put("mail.smtp.port", "465");
        // 此处填写你的账号
        props.put("mail.user", "897048711@qq.com");
        // 此处的密码就是前面说的16位STMP口令
        props.put("mail.password", "hcnypzzbtkvbbchg");
        
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.EnableSSL.enable","true");

        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        // 构建授权信息，用于进行SMTP进行身份验证
        Authenticator authenticator = new Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                // 用户名、密码
                String userName = props.getProperty("mail.user");
                String password = props.getProperty("mail.password");
                return new PasswordAuthentication(userName, password);
            }
        };
        // 使用环境属性和授权信息，创建邮件会话
        Session mailSession = Session.getInstance(props, authenticator);
        // 创建邮件消息
        Message message = new MimeMessage(mailSession);
        // 设置发件人
        InternetAddress form = new InternetAddress(
                props.getProperty("mail.user"));
        message.setFrom(form);

        // 设置收件人的邮箱
        InternetAddress toMail = new InternetAddress(toM);
        message.setRecipient(RecipientType.TO, toMail);

        // 设置邮件标题
        message.setSubject(title);

        // 设置邮件的内容体
        message.setContent(content, "text/html;charset=UTF-8");

        // 最后当然就是发送邮件啦
        Transport.send(message);
	}

}
