package com.aidijing;

import com.aidijing.common.ResponseEntity;
import com.aidijing.entity.User;
import org.junit.Test;

/**
 * @author : 披荆斩棘
 * @date : 2017/5/25
 */
public class ResponseEntityTest {
    @Test
    public void responseEntityTest () throws Exception {
        System.err.println( "new User() = " + new User() );

        final ResponseEntity< User > responseEntity = new ResponseEntity<>().setResponseContent( new User() );

        System.err.println( "responseEntity.toJson() = " + responseEntity.toJson() );


    }
}
