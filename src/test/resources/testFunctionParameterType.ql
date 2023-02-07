java.util.Map event = new java.util.HashMap();

java.util.Map auction = new java.util.HashMap();
auction.put("title","title");
auction.put("category","123456");
event.put("auction",auction);
System.out.println(auctionUtil.getText(event.auction.title,event.auction.category));
