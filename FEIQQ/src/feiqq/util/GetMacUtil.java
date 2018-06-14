package feiqq.util;

import java.net.NetworkInterface;
import java.util.Enumeration;

public class GetMacUtil {  
    public static void main(String[] args) {  
        try {  
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();  
            while (enumeration.hasMoreElements()) {  
                StringBuffer stringBuffer = new StringBuffer();  
                NetworkInterface networkInterface = enumeration.nextElement();  
                if (networkInterface != null) {  
                    byte[] bytes = networkInterface.getHardwareAddress();  
                    if (bytes != null) {  
                        for (int i = 0; i < bytes.length; i++) {  
                            if (i != 0) {  
                                stringBuffer.append("-");  
                            }  
                            int tmp = bytes[i] & 0xff; // 字节转换为整数  
                            String str = Integer.toHexString(tmp);  
                            if (str.length() == 1) {  
                                stringBuffer.append("0" + str);  
                            } else {  
                                stringBuffer.append(str);  
                            }  
                        }  
                        String mac = stringBuffer.toString().toUpperCase();    
                        System.out.println(mac);  
                    }  
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
} 