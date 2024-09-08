package com.siyu.common.utils;

import com.siyu.common.enums.ErrorStatus;
import com.siyu.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

@Slf4j
@Component
public class IpUtils {

    public static final int COUNTRY = 0;

    public static final int REGION = 1;

    public static final int PROVINCE = 2;

    public static final int CITY = 3;

    public static final int ISP = 4;

    private static Searcher searcher = null;

    @PostConstruct
    private void initIp2regionResource() {
        try {
            InputStream inputStream = new ClassPathResource("/ip2region/ip2region.xdb").getInputStream();
            byte[] dbBinStr = FileCopyUtils.copyToByteArray(inputStream);
            searcher = new Searcher(null, null, dbBinStr);
        } catch (Exception e) {
            log.warn("ip2region资源解析失败, 原因: {}", e.toString());
        }
    }

    /**
     * 从request中解析IP
     * @param request
     * @return
     */
    public static String getIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if(ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if(ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)){
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    log.warn("InetAddress解析失败, 原因: {}", e.toString());
                }
                if(inet != null)
                    ipAddress = inet.getHostAddress();
            }
        }
        if(ipAddress != null && ipAddress.length() > 15){
            if(ipAddress.indexOf(",") > 0){
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    /**
     * 从IP中解析IP地址
     * @param ip
     * @return
     */
    public static String getIpAddress(String ip) {
        try {
            return searcher.search(ip);
        } catch (Exception e) {
            log.warn("ip地址解析失败，原因：{}", e.toString());
            return "0|0|0|0|0";
        }
    }

    /**
     * 从request中解析IP地址
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        return getIpAddress(getIp(request));
    }

    /**
     * 从IP中解析出IP地址数组
     * @param ip
     * @return
     */
    public static String[] getIpAddressArray(String ip) {
        try {
            return searcher.search(ip).split("\\|");
        } catch (Exception e) {
            log.warn("ip地址解析失败，原因：{}", e.toString());
            return new String[] {"0", "0", "0", "0", "0"};
        }
    }

    /**
     * 从request中解析出IP地址数组
     * @param request
     * @return
     */
    public static String[] getIpAddressArray(HttpServletRequest request) {
        return getIpAddressArray(getIp(request));
    }

    public static String getCountry(String[] ipAddressArray) {
        if(null == ipAddressArray || ipAddressArray.length != 5) {
            log.error("ipAddressArray格式错误");
            return "0";
        }
        return ipAddressArray[COUNTRY];
    }

    public static String getRegion(String[] ipAddressArray) {
        if(null == ipAddressArray || ipAddressArray.length != 5) {
            log.error("ipAddressArray格式错误");
            return "0";
        }
        return ipAddressArray[REGION];
    }

    public static String getProvince(String[] ipAddressArray) {
        if(null == ipAddressArray || ipAddressArray.length != 5) {
            log.error("ipAddressArray格式错误");
            return "0";
        }
        return ipAddressArray[PROVINCE];
    }

    public static String getCity(String[] ipAddressArray) {
        if(null == ipAddressArray || ipAddressArray.length != 5) {
            log.error("ipAddressArray格式错误");
            return "0";
        }
        return ipAddressArray[CITY];
    }

    public static String getISP(String[] ipAddressArray) {
        if(null == ipAddressArray || ipAddressArray.length != 5) {
            log.error("ipAddressArray格式错误");
            return "0";
        }
        return ipAddressArray[ISP];
    }
}
