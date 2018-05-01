package feiqq.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ^  为限制开头  <br/>
 * $  为限制结尾  <br/>
 * .  为条件限制除/n以外任意一个单独字符  <br/>
 * +  为至少出现一次  <br/>
 * *  很多次  <br/>
 */
public class ValidateUtil {

	public static boolean isMail(String str) {
		String regex = "^[a-zA-Z0-9]{1,}@(([a-zA-z0-9]-*){1,}\\.){1,3}[a-zA-z\\-]{1,}$";
		if (str.matches(regex)) {
			return true;
		}
		return false;
	}
	
	public static boolean isString(String str) {
		Pattern pattern = Pattern.compile("^[a-zA-Z]*$");
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return true;
		}
		return false;
	}
	
	public static boolean isChinese(String str) {
		// 编码
		Pattern pattern = Pattern.compile("^[\u4E00-\u9FA5]*$");
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		//声明用于判断的邮箱
        String em1 = "17628@qq.com";
        String em2 = "deadzq@!qq.com";
        String em3 = "de@qqcom";
        String em4 = "ed123.com";
        String em5 = "deadzq@qwq.com";
        String em6 = "ukyo@qq.com";
        String em7 = "helloworld@123.com";
        String em8 = "652goldhair@12333.com.cn";
        //em9虽然可能没有这样的域名结尾 但符合(\\.\\w{2,3})*\\.\\w{2,3}条件
        //更精准的表达式应该把现有国际上域名声明为一个数组 再加以判断
        String em9 = "loveinc@ukyo.dd.sss";
        
        //把域名装入域名数组
        String[] emailArray = {em1,em2,em3,em4,em5,em6,em7,em8,em9};
        for(String e : emailArray)
        {
            //如果其中邮箱元素复合正则表达式 regex 则:
            if(isMail(e))
            {
                //输出该邮箱元素 为合规
                System.out.println(e+"为合规邮箱输入.");
            }
            else {
            	System.out.println(e+"为非法邮箱输入.");
            }
        }
	}
	
}
