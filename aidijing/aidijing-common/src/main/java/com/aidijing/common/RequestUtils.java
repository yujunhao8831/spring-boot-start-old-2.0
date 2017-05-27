package com.aidijing.common;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : 余峻豪
 * @date : 16/6/16
 */
public abstract class RequestUtils {

    // \b 是单词边界(连着的两个(字母字符 与 非字母字符) 之间的逻辑上的间隔),
    // 字符串在编译时会被转码一次,所以是 "\\b"
    // \B 是单词内部逻辑间隔(连着的两个字母字符之间的逻辑上的间隔)
    private static final String  phoneReg = "\\b(ip(hone|od)|android|opera m(ob|in)i"
            + "|windows (phone|ce)|blackberry"
            + "|s(ymbian|eries60|amsung)|p(laybook|alm|rofile/midp"
            + "|laystation portable)|nokia|fennec|htc[-_]"
            + "|mobile|up.browser|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";
    //移动设备正则匹配：手机端、平板
    private static final Pattern phonePat = Pattern.compile( phoneReg, Pattern.CASE_INSENSITIVE );
    private static final String  tableReg = "\\b(ipad|tablet|(Nexus 7)|up.browser" + "|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";
    private static final Pattern tablePat = Pattern.compile( tableReg, Pattern.CASE_INSENSITIVE );

    /**
     * 在webapp中得到request
     *
     * @return null or HttpServletRequest
     */
    public static HttpServletRequest getRequest () {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if ( null == requestAttributes ) {
            return null;
        }
        return ( ( ServletRequestAttributes ) requestAttributes ).getRequest();
    }

    /**
     * isAjaxRequest:判断请求是否为Ajax请求.
     */
    public static boolean isAjaxRequest ( HttpServletRequest request ) {
        return "XMLHttpRequest".equals( request.getHeader( "X-Requested-With" ) );
    }

    public static String getRequestIp () {
        HttpServletRequest request = ( ( ServletRequestAttributes ) RequestContextHolder.getRequestAttributes() ).getRequest();
        String             ip      = request.getHeader( "X-Real-IP" );
        if ( StringUtils.isNotBlank( ip ) && ! "unknown".equalsIgnoreCase( ip ) ) {
            return ip.trim();
        }

        ip = request.getHeader( "X-Forwarded-For" );
        if ( StringUtils.isNotBlank( ip ) && ! "unknown".equalsIgnoreCase( ip ) ) {
            // 多次反向代理后会有多个IP值，第一个为 真实 ip
            return ip.split( "," )[0].trim();
        }

        ip = request.getHeader( "Proxy-Client-IP" );
        if ( StringUtils.isNotBlank( ip ) && ! "unknown".equalsIgnoreCase( ip ) ) {
            return ip.trim();
        }

        ip = request.getHeader( "WL-Proxy-Client-IP" );
        if ( StringUtils.isNotBlank( ip ) && ! "unknown".equalsIgnoreCase( ip ) ) {
            return ip.trim();
        }

        ip = request.getHeader( "HTTP_CLIENT_IP" );
        if ( StringUtils.isNotBlank( ip ) && ! "unknown".equalsIgnoreCase( ip ) ) {
            return ip.trim();
        }

        ip = request.getHeader( "X-Cluster-Client-IP" );
        if ( StringUtils.isNotBlank( ip ) && ! "unknown".equalsIgnoreCase( ip ) ) {
            return ip.trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * 查询浏览器终端
     *
     * @param userAgent : 浏览器代理
     * @return @see {{@link TerminalEnum}}
     */
    public static TerminalEnum queryTerminal ( String userAgent ) {
        if ( null == userAgent ) {
            userAgent = StringUtils.EMPTY;
        }
        userAgent = userAgent.toLowerCase();

        if ( userAgent.indexOf( "micromessenger" ) > 0 ) {
            return TerminalEnum.WX;
        }
        // 匹配
        Matcher matcherPhone = phonePat.matcher( userAgent );
        Matcher matcherTable = tablePat.matcher( userAgent );
        if ( matcherPhone.find() || matcherTable.find() ) {
            return TerminalEnum.APP;
        } else {
            return TerminalEnum.PC;
        }
    }

    /**
     * 存放数据把获取过来的数据倒到params中
     *
     * @param request
     * @return
     */
    public static Map< String, String > getParameter ( HttpServletRequest request ) {
        Map< String, String > params = new HashMap<>();
        for ( Map.Entry< String, String[] > param : request.getParameterMap().entrySet() ) {
            String paramKey      = param.getKey(); // 参数key
            String paramValues[] = param.getValue(); // 参数value(数组)
            String paramValue    = "";
            for ( int i = 0 ; i < paramValues.length ; i++ ) {
                paramValue = ( i == paramValues.length - 1 )
                             ? paramValue + paramValues[i]
                             : paramValue + paramValues[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            // paramKey = new String(paramValue.getBytes("ISO-8859-1"), "gbk");
            params.put( paramKey, paramValue );
        }
        return params;
    }

    /**
     * 得到域名
     *
     * @param request
     * @return
     */
    public static String getRequestDomain ( HttpServletRequest request ) {
        StringBuffer url = request.getRequestURL();
        final String domain = url.delete( url.length() - request.getRequestURI().length(), url.length() )
                                 .append( "/" )
                                 .toString();
        return domain;
    }

    /**
     * 是否是 Content-Type=application/json; json传输
     *
     * @param request
     * @return
     */
    public static boolean isApplicationJsonHeader ( HttpServletRequest request ) {
        String contentType = request.getHeader( HttpHeaders.CONTENT_TYPE );
        return contentType != null && contentType.trim()
                                                 .replaceAll( StringUtils.SPACE, StringUtils.EMPTY )
                                                 .contains( MediaType.APPLICATION_JSON_VALUE );
    }

    /**
     * 得到请求信息
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static String getRequestMessage ( HttpServletRequest request, Object userId, String username ) throws
                                                                                                          IOException {
        StringBuffer parameters = new StringBuffer();
        parameters.append( "\n用户ID : " + userId )
                  .append( "\n用户姓名 : " + username );
        return getRequestMessage( request, parameters );
    }

    /**
     * 得到请求信息
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static String getRequestMessage ( HttpServletRequest request ) throws IOException {
        StringBuffer parameters = new StringBuffer();
        return getRequestMessage( request, parameters );
    }

    /**
     * 得到请求参数
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static String getRequestParameters ( HttpServletRequest request ) throws IOException {
        final Map< String, String[] > parameterMap = request.getParameterMap();
        StringBuffer                  parameters   = new StringBuffer();
        if ( RequestUtils.isApplicationJsonHeader( request ) ) {
            parameters.append( IOUtils.toString( request.getInputStream(), "UTF-8" ) );
        } else {
            for ( Map.Entry< String, String[] > parameter : parameterMap.entrySet() ) {
                String[] values = parameter.getValue();
                parameters.append( parameter.getKey() + "=" + Arrays.toString( values ) + "\t" );
            }
        }
        return parameters.toString();
    }

    private static String getRequestMessage ( HttpServletRequest request, StringBuffer parameters ) throws IOException {
        parameters.append( "\n请求URL : " + request.getRequestURI() )
                  .append( "\n请求URI : " + request.getRequestURL() )
                  .append( "\n请求方式 : " + request.getMethod() + ( RequestUtils.isAjaxRequest( request ) == true
                                                                 ? "\tajax请求"
                                                                 : "\t同步请求" ) )
                  .append( "\n请求者IP : " + request.getRemoteAddr() )
                  .append( "\n请求时间 : " + Instant.now() );
        final Enumeration< String > headerNames = request.getHeaderNames(); // 请求头
        while ( headerNames.hasMoreElements() ) {
            String element = headerNames.nextElement();
            if ( null != element ) {
                String header = request.getHeader( element );
                parameters.append( "\n请求头内容 : " + element + "=" + header );
            }
        }
        parameters.append( "\n请求参数 : " + getRequestParameters( request ) );
        final Enumeration< String > sessionAttributeNames = request.getSession().getAttributeNames(); // 请求Session内容
        while ( sessionAttributeNames.hasMoreElements() ) {
            parameters.append( "\nSession内容 : " + sessionAttributeNames.nextElement() );
        }
        return parameters.toString();
    }


    public enum TerminalEnum {
        WX, PC, APP
    }

}