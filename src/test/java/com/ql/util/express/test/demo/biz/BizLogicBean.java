package com.ql.util.express.test.demo.biz;

public class BizLogicBean {
	
	
	/**
	 * 注册新用户
	 * @param nick
	 * @return
	 */
	public UserDO signUser(String nick){
 		UserDO user = new UserDO();
		user.setNick(nick);
		user.setScore(0L);
		user.setShopOpen(false);
		user.setShopType("no_shop");
		System.out.println("创建用户成功："+user);
		return user;
	}
	/**
	 * 用户店铺激活
	 * @param user
	 */
	public void openShop(UserDO user){
		user.setShopOpen(true);
		user.setShopType("c2c");
		System.out.println("店铺已经激活："+user);
		user.setShopOpen(true);
	}
	
	/**
	 * 用户店铺关闭
	 * @param user
	 */
	public void closeShop(UserDO user){
		user.setShopOpen(false);
		System.out.println("店铺已经关闭："+user);
		user.setShopOpen(false);
	}
	
	/**
	 * 用户星级+1
	 * @param user
	 */
	public void addScore(UserDO user){
		user.setScore(user.getScore()+1);
		System.out.println("用户星级 +1 ："+user);
	}
	
	public boolean isShopOpening(UserDO user){
		if(user.isShopOpen()){
			System.out.println("当前店铺为营业状态.");
			return true;
		}
		System.out.println("当前店铺为关闭状态.");
		return false;
	}
	
	/**
	 * 店铺升级
	 * @param user
	 * @return
	 */
	public boolean upgradeShop(UserDO user){
		if(user.getShopType().equals("b2c")){
			System.out.println("您已经是B商家，不用升级了.");
			return false;
		}
		if(user.getScore().longValue()>5L){
			user.setShopType("b2c");
			System.out.println("成功升级为B商家："+user);
			return true;
		}else{
			System.out.println("需要5星级以上卖家，你现在才"+user.getScore()+"星级，再接再厉哦!");
			return false;
		}
	}
	
	public void showShop(UserDO user){
		System.out.println(user);
	}

}
