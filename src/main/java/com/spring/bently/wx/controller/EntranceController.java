package com.spring.bently.wx.controller;

import com.spring.bently.wx.service.IEntranceService;
import com.spring.bently.wx.utils.ResponseUtils;
import com.spring.bently.wx.utils.StringUtils;
import com.spring.bently.wx.utils.WeixinPropertiesUtils;
import com.spring.bently.wx.utils.XmlUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.Document;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by wgq on 16-4-2.
 */
@Controller
public class EntranceController {

    private static Logger log = LoggerFactory.getLogger(EntranceController.class) ;

    @Autowired
    private IEntranceService entranceService ;

    @RequestMapping(value = "/entrance", method = RequestMethod.GET)
    public void entranceGet(HttpServletRequest request, HttpServletResponse response) {

        log.info("get请求...");
        String echostr = request.getParameter("echostr") ;
        log.info("echostr = " + echostr);
        //第一次接入的时候需要检查
        if(echostr != null) {
            if (checkSignature(request)) {
                responseMsg(echostr,response) ;
                return ;
            }
        }
        String msg = getMessagePost(request,response) ;
        if(msg == null) {
            responseMsg("",response) ;
        }else {
            responseMsg(msg,response) ;
        }


        return ;
    }

    @RequestMapping(value = "/entrance", method = RequestMethod.POST)
    public void entrancePost(HttpServletRequest request, HttpServletResponse response) {
        log.info("post请求...");

        String msg = getMessagePost(request,response) ;
        if(msg == null) {
            responseMsg("",response) ;
        }else {
            responseMsg(msg,response) ;
        }
    }

    //处理微信发送的请求
    private String getMessagePost(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding("UTF-8");
            InputStream inputStream = request.getInputStream() ;
            if(inputStream == null) {
                return null ;
            }
            Map<String,String> msgMap = XmlUtils.xmlToMap(inputStream) ;
            log.info("msgMap = " + msgMap.toString());
            String returnMsg = entranceService.entrance(msgMap) ;
            log.info("returnMsg = " + returnMsg);

            return returnMsg ;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null ;

    }

    private void responseMsg(String msg, HttpServletResponse response) {
        //response.setContentType("application/xml");
        response.setCharacterEncoding("UTF-8");
        //OutputStream outputStream = null ;
        try {
            //outputStream = response.getOutputStream() ;
            //outputStream.write(msg.getBytes("UTF-8"));
            //outputStream.flush();
            response.getWriter().println(msg);
            response.getWriter().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
               // outputStream.close();
                response.getWriter().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



    //微信接入检查参数
    private boolean checkSignature(HttpServletRequest request) {
        String signature = request.getParameter("signature") ;
        String timestamp = request.getParameter("timestamp") ;
        String nonce = request.getParameter("nonce") ;

        log.info("signature = " + signature +
        "timestamp = " + timestamp +
        "nonce = " + nonce ) ;

        String token = WeixinPropertiesUtils.getProperties("token");

        if(StringUtils.isEmpty(signature)) {
            return false ;
        }

        String[] arry = {timestamp, nonce, token} ;
        Arrays.sort(arry);

        String tmpStr = arry[0] + arry[1] + arry[2] ;
        String tmpStr1 = DigestUtils.sha1Hex(tmpStr) ;
        log.info("tmpStr1 = " + tmpStr1) ;

        if(signature.equals(tmpStr1)) {
            return true ;
        }

        return false ;
    }
}