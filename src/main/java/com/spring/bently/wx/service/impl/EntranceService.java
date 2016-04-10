package com.spring.bently.wx.service.impl;

import com.spring.bently.wx.common.MsgTypeEnum;
import com.spring.bently.wx.service.IEntranceService;
import com.spring.bently.wx.service.IEventService;
import com.spring.bently.wx.utils.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by wgq on 16-4-9.
 */
@Service
public class EntranceService implements IEntranceService {

    private static Logger log = LoggerFactory.getLogger(EntranceService.class) ;

    @Autowired
    private IEventService eventService ;

    @Override
    public String entrance(Map<String,String> map) {
        log.info("进入entranceService中......");
        if(map == null) {
            return "" ;
        }

        String event = map.get("msgtype") ;
        String msg = "" ;

        switch (MsgTypeEnum.valueOf(event)) {

            case image:
                return ResponseUtils.textResponse(map, "正在开发中...") ;
            case text:
                return ResponseUtils.textResponse(map,"正在开发中...") ;
            case voice:
                return ResponseUtils.textResponse(map,"正在开发中...") ;
            case video:
                return ResponseUtils.textResponse(map,"正在开发中...") ;
            case shortvideo:
                return ResponseUtils.textResponse(map,"正在开发中...") ;
            case location:
                return ResponseUtils.textResponse(map,"正在开发中...") ;
            case link:
                return ResponseUtils.textResponse(map,"正在开发中...") ;
            case event:
                log.info("进入event事件中......");
                msg = eventService.event(map) ;
                break;
            default:
                return ResponseUtils.textResponse(map,"正在开发中...") ;
        }

        log.info("event = " + event);

        return msg;
    }

}
