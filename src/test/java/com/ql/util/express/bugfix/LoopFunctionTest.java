package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.Operator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 自定义的LoopAnd，LoopOr，LoopSet function功能
 * Created by tianqiao on 18/2/8.
 */
public class LoopFunctionTest {
    
    @Test
    public void test() throws Exception{
        ExpressRunner runner = new ExpressRunner(false,true);
        runner.addFunction("loopAnd", new Operator() {
            private ExpressRunner loppRunner = new ExpressRunner();
            @Override
            public Object executeInner(Object[] list) throws Exception {
                if(list[0]==null){
                    return false;
                }
                if(!(list[0] instanceof Collection)||((Collection)list[0]).size()==0){
                    return false;
                }
    
                Collection objs = (Collection) list[0];
                String exp = (String) list[1];
                Integer index = 0;
                for(Object obj : objs) {
                    IExpressContext<String, Object> map = new DefaultContext<String, Object>();
                    map.put("x",obj);
                    map.put("index",index++);
                    try {
                        Object r = loppRunner.execute(exp, map, null, true, false);
                        if(r!=null && r instanceof Boolean && (Boolean) r){
                            continue;
                        }else{
                            return false;
                        }
                    }catch (Exception e){
                        return false;
                    }
                }
                return true;
            }
        });
        runner.addFunction("loopOr", new Operator() {
            private ExpressRunner loppRunner = new ExpressRunner();
            @Override
            public Object executeInner(Object[] list) throws Exception {
                if(list[0]==null){
                    return false;
                }
                if(!(list[0] instanceof Collection)||((Collection)list[0]).size()==0){
                    return false;
                }
        
                Collection objs = (Collection) list[0];
                String exp = (String) list[1];
                Integer index=0;
                for(Object obj : objs) {
                    IExpressContext<String, Object> map = new DefaultContext<String, Object>();
                    map.put("x",obj);
                    map.put("index",index++);
                    try {
                        Object r = loppRunner.execute(exp, map, null, true, false);
                        if(r!=null && r instanceof Boolean && (Boolean) r){
                            return true;
                        }else{
                            continue;
                        }
                    }catch (Exception e){
                        return false;
                    }
                }
                return false;
            }
        });
        runner.addFunction("loopSet", new Operator() {
            private ExpressRunner loppRunner = new ExpressRunner();
            @Override
            public Object executeInner(Object[] list) throws Exception {
                if(list[0]==null){
                    return false;
                }
                if(!(list[0] instanceof Collection)||((Collection)list[0]).size()==0){
                    return false;
                }
        
                Collection objs = (Collection) list[0];
                String exp = (String) list[1];
                Integer index=0;
                for(Object obj : objs) {
                    IExpressContext<String, Object> map = new DefaultContext<String, Object>();
                    map.put("x",obj);
                    map.put("index",index++);
                    try {
                        loppRunner.execute(exp, map, null, true, false);
                    }catch (Exception e){
                        return null;
                    }
                }
                return null;
            }
        });
    
    
        ArrayList<SkuDO> skuList = createSkuList();
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        String exp = "loopAnd(skuList,'x.price>10')";
        context.put("skuList",skuList);
        Object result = runner.execute(exp,context,null,false,true);
        assert ((Boolean)result);
    
        exp = "loopSet(skuList,'if(index>=2){x.price=9.9}')";
        runner.execute(exp,context,null,false,true);
        assert (skuList.get(0).getPrice()==10.1);
        assert (skuList.get(1).getPrice()==10.1);
        assert (skuList.get(2).getPrice()==9.9);
    
        exp = "loopOr(skuList,'x.price<10')";
        result = runner.execute(exp,context,null,false,true);
        assert ((Boolean)result);
        
        
    }
    
    private ArrayList<SkuDO> createSkuList() {
        ArrayList<SkuDO> skuList = new ArrayList<SkuDO>();
        for(int i=0;i<5;i++) {
            SkuDO sku = new SkuDO();
            sku.setPrice(10.1);
            skuList.add(sku);
        }
        return skuList;
    }
    
    public class SkuDO{
        private Long id;
        private Double price;
        private String title;
    
        public Long getId() {
            return id;
        }
    
        public void setId(Long id) {
            this.id = id;
        }
    
        public Double getPrice() {
            return price;
        }
    
        public void setPrice(Double price) {
            this.price = price;
        }
    
        public String getTitle() {
            return title;
        }
    
        public void setTitle(String title) {
            this.title = title;
        }
    }
}
