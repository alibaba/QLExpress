package com.alibaba.qlexpress4.security;

import com.alibaba.qlexpress4.member.IMethod;
import java.util.Map;

/**
 * @Author TaoKan
 * @Date 2023/4/9 上午10:35
 */
public class QLStrategyWhiteList extends StrategyWhiteList {

    public QLStrategyWhiteList(Map<Class,String> whiteStrategyStructList) {
        super(whiteStrategyStructList);
    }

    @Override
    public boolean checkInRules(IMethod iMethod) {
        if(this.getWhiteStrategyStructList().containsKey(iMethod.getClazz())){
            String name = this.getWhiteStrategyStructList().get(iMethod.getClazz());
            if(name == null || "".equals(name)){
                return true;
            }else {
                return iMethod.getName().equals(name);
            }
        }
        return false;
    }
}
