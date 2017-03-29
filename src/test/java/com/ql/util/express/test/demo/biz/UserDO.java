package com.ql.util.express.test.demo.biz;

public class UserDO {
	
	/**
	 * 用户昵称
	 */
	private String nick;	
	
	/**
	 * 开店状态
	 */
	private boolean shopOpen;
	
	/**
	 * 星级
	 */
	private Long score;
	
	/**
	 * 店铺类型:c2c,b2c
	 */
	private String shopType;	

	public String getNick() {
		return nick;
	}
	
	public void setNick(String nick) {
		this.nick = nick;
	}
	
	public Long getScore() {
		return score;
	}
	
	public void setScore(Long score) {
		this.score = score;
	}
	
	public boolean isShopOpen() {
		return shopOpen;
	}
	
	public void setShopOpen(boolean shopOpen) {
		this.shopOpen = shopOpen;
	}
	
	public String getShopType() {
		return shopType;
	}
	
	public void setShopType(String shopType) {
		this.shopType = shopType;
	}
	
	
	public String toString(){
		StringBuffer sb = new StringBuffer("用户信息：");
		sb.append("\t\t昵称: ").append(nick);
		if(shopOpen){
			sb.append("\t\t店铺状态: 营业中");
		}else{
			sb.append("\t\t店铺状态: 关闭中");
		}
		sb.append("\t\t卖家星级: ").append(score);
		sb.append("\t\t店铺类型: ").append(shopType);
		return sb.toString();
	}
	

}
