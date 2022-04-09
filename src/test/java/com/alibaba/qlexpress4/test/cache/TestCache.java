package com.alibaba.qlexpress4.test.cache;

import com.alibaba.qlexpress4.config.QLExpressRunStrategy;
import com.alibaba.qlexpress4.test.property.Parent;
import com.alibaba.qlexpress4.utils.ExpressUtil;
import java.lang.reflect.Field;


/**
 * @Author TaoKan
 * @Date 2022/4/9 下午5:16
 */
public class TestCache {
    public static void main(String[] args){
        QLExpressRunStrategy.initUseCacheClear(true);
        for(int i = 0; i< 2; i++){
            Field field = ExpressUtil.getFieldCacheElement(Parent.class,"age");
            System.out.println(field);
        }
        Field field = ExpressUtil.getFieldCacheElement(Parent.class,"sex");
        System.out.println(field);
//        ExpressUtil.getConstructorWithCache();
//        ExpressUtil.getMethodWithCache();

    }
}
