package com.aidijing.controller;

import com.aidijing.common.LogUtils;
import com.aidijing.common.ResponseEntity;
import com.aidijing.entity.User;
import com.aidijing.service.IUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageRowBounds;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户 前端控制器
 * </p>
 *
 * @author 披荆斩棘
 * @since 2017-05-11
 */
@RestController
@RequestMapping( "api/users" )
public class UserController {
    @Autowired
    private IUserService userService;

    @Autowired
    private RedissonClient redissonClient;

    @GetMapping( "distributed-lock" )
    public Object distributedLock () throws InterruptedException {
        int timeout = 10;

        // 分布式锁
        final RLock lock = redissonClient.getLock( this.getClass().getSimpleName() + "LOCK" );
        lock.lock();

        for ( int i = 0 ; i < timeout ; i++ ) {
            TimeUnit.SECONDS.sleep( 1 );
            LogUtils.getLogger().warn( "lock ing ... ... ... " );
        }
        // N 时间后 后解锁
        lock.unlock();

        LogUtils.getLogger().warn( "unlock" );

        return "success";
    }


    @GetMapping( "async" )
    public Object asyncUpdate () {
        userService.asyncUpdate();
        return "success";
    }

    @GetMapping
    public PageInfo listPage ( PageRowBounds pageRowBounds ) {
        return userService.listPage( pageRowBounds );
    }

    @GetMapping( "list" )
    public List< User > listPageCache () throws JsonProcessingException {
        return userService.list();
    }

    @PutMapping( "{id}" )
    public ResponseEntity update ( @PathVariable Long id,
                                   User user ) {
        userService.update( user.setId( id ) );
        return ResponseEntity.ok();
    }

    @PostMapping
    public ResponseEntity save ( User user ) {
        userService.save( user );
        return ResponseEntity.ok();
    }

    @DeleteMapping( "{id}" )
    public ResponseEntity delete ( @PathVariable Long id,
                                   User user ) {
        userService.update( user.setId( id ) );
        return ResponseEntity.ok();
    }


}
