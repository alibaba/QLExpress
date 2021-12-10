package com.ql.util.express.example;

import java.util.ArrayList;
import java.util.List;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;

/**
 * QLExpress的一种典型应用场景
 *
 * @author xuannan
 */
public class TypicalDemo {

    private final ExpressRunner runner = new ExpressRunner();

    /**
     * 判断一个用户TAG的第X位是否为1。这个的demo,其实现合理性不考虑
     *
     * @param user
     * @param tagBitIndex
     * @return
     */
    public boolean userTagJudge(UserInfo user, int tagBitIndex) {
        return (user.getUserTag() & ((long)Math.pow(2, tagBitIndex))) > 0;
    }

    /**
     * 判断一个用户是否订购过某个商品
     *
     * @param user
     * @param goodsId
     * @return
     */
    public boolean hasOrderGoods(UserInfo user, long goodsId) {
        //随机模拟一个
        return user.getUserId() % 2 == 1;
    }

    /**
     * 判断逻辑执行函数
     *
     * @param userInfo
     * @param expression
     * @return
     * @throws Exception
     */
    public String hasPermission(UserInfo userInfo, String expression) throws Exception {
        IExpressContext<String, Object> expressContext = new DefaultContext<>();
        expressContext.put("userInfo", userInfo);
        List<String> errorInfo = new ArrayList<>();
        Boolean result = (Boolean)runner.execute(expression, expressContext, errorInfo, true, false);
        String resultStr = "";
        if (result) {
            resultStr = "可以订购此商品";
        } else {
            for (int i = 0; i < errorInfo.size(); i++) {
                if (i > 0) {
                    resultStr = resultStr + ",";
                }
                resultStr = resultStr + errorInfo.get(i);
            }
            resultStr = resultStr + ",所以不能订购此商品";
        }
        return "亲爱的" + userInfo.getName() + " : " + resultStr;
    }

    public void initial() throws Exception {
        runner.addOperatorWithAlias("而且", "and", null);
        runner.addFunctionOfClassMethod("userTagJudge", TypicalDemo.class.getName(), "userTagJudge",
            new String[] {UserInfo.class.getName(), "int"}, "你不是三星卖家");
        runner.addFunctionOfClassMethod("hasOrderGoods", TypicalDemo.class.getName(), "hasOrderGoods",
            new String[] {UserInfo.class.getName(), "long"}, "你没有开通淘宝店铺");
        runner.addMacro("三星卖家", "userTagJudge(userInfo,3)");//3表示三星卖家的标志位
        runner.addMacro("已经开店", "hasOrderGoods(userInfo,100)");//100表示旺铺商品的ID
    }

    public static void main(String[] args) throws Exception {
        TypicalDemo demo = new TypicalDemo();
        demo.initial();
        System.out.println(demo.hasPermission(new UserInfo(100, "xuannan", 7), "三星卖家   而且   已经开店"));
        System.out.println(demo.hasPermission(new UserInfo(101, "qianghui", 8), "三星卖家   而且   已经开店"));
        System.out.println(demo.hasPermission(new UserInfo(100, "张三", 8), "三星卖家 and 已经开店"));
        System.out.println(demo.hasPermission(new UserInfo(100, "李四", 7), "三星卖家 and 已经开店"));
    }
}

class UserInfo {
    final long id;
    final long tag;
    final String name;

    public UserInfo(long aId, String aName, long aUserTag) {
        this.id = aId;
        this.tag = aUserTag;
        this.name = aName;
    }

    public String getName() {
        return name;
    }

    public long getUserId() {
        return id;
    }

    public long getUserTag() {
        return tag;
    }
}
