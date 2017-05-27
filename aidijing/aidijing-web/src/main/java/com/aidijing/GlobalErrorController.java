package com.aidijing;

import com.aidijing.common.LogUtils;
import com.aidijing.common.ResponseEntity;
import com.aidijing.common.exception.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/**
 * @author : 披荆斩棘
 * @date : 2017/5/18
 */
@RestControllerAdvice
public class GlobalErrorController {

    @ExceptionHandler( ServiceException.class )
    public ResponseEntity serviceErrorHandler ( Throwable e ) {
        if ( LogUtils.getLogger().isErrorEnabled() ) {
            LogUtils.getLogger().error("error", e );
        }
        return ResponseEntity.error( "服务器内部错误,EXCEPTION_CODE:" + ExceptionCode.SERVICE_EXCEPTION.getCode() );
    }

    @ExceptionHandler( { SQLException.class , DataAccessException.class } )
    public ResponseEntity sqlErrorHandler ( Throwable e ) {
        if ( LogUtils.getLogger().isErrorEnabled() ) {
            LogUtils.getLogger().error("error", e );
        }
        return ResponseEntity.error( "服务器内部错误,EXCEPTION_CODE:" + ExceptionCode.SQL_EXCEPTION.getCode() );
    }


    @ExceptionHandler( Throwable.class )
    public ResponseEntity globalErrorHandler ( Throwable e ) {
        if ( LogUtils.getLogger().isErrorEnabled() ) {
            LogUtils.getLogger().error("error", e );
        }
        return ResponseEntity.error( "error : " + e.getMessage() );
    }


    private enum ExceptionCode {
        SQL_EXCEPTION( "9001", "SQL异常" ),
        SERVICE_EXCEPTION( "9002", "Service异常" );

        private String code;
        private String comment;

        ExceptionCode ( String code, String comment ) {
            this.code = code;
            this.comment = comment;
        }

        /**
         * 判断传入的code是否是枚举中所定义的code
         *
         * @param code
         * @return
         */
        public static boolean isCode ( final String code ) {
            for ( ExceptionCode value : ExceptionCode.values() ) {
                if ( value.getCode().equalsIgnoreCase( code ) ) {
                    return true;
                }
            }
            return false;
        }

        public static String codeValue ( final String code ) {
            for ( ExceptionCode value : ExceptionCode.values() ) {
                if ( value.getCode().equalsIgnoreCase( code ) ) {
                    return value.getComment();
                }
            }
            return null;
        }

        public static boolean isNotCode ( final String code ) {
            return ! isCode( code );
        }

        public String getComment () {
            return comment;
        }

        public String getCode () {
            return code;
        }
    }

}
