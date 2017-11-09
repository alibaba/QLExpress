package com.ql.util.express.test.spring;

import java.util.Date;

/**
 * Created by tianqiao on 17/3/8.
 */
public class BizLogicBean {
    
    public UserDO getUserInfo(String nick)
    {
        if(nick.equals("小王")) {
            UserDO userDO = new UserDO();
            userDO.setNick(nick);
            userDO.setGmtCreate(new Date());
            userDO.setUserId(10086L);
            userDO.setPosition("salesman");
            userDO.setSalary(5000.0);
            return userDO;
        }else if(nick.equals("马总")) {
            UserDO userDO = new UserDO();
            userDO.setNick(nick);
            userDO.setGmtCreate(new Date());
            userDO.setUserId(1L);
            userDO.setPosition("boss");
            userDO.setSalary(999999999999.0);
            return userDO;
        }
        return null;
    }
}
