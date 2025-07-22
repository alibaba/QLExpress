package com.alibaba.qlexpress4.inport;

/**
 * Author: DQinYuan
 */
public class MyHome {
    
    private String sofa;
    
    private String chair;
    
    private MyDesk myDesk;
    
    private String bed;
    
    public String getSofa() {
        return sofa;
    }
    
    public void setSofa(String sofa) {
        this.sofa = sofa;
    }
    
    public String getChair() {
        return chair;
    }
    
    public MyDesk getMyDesk() {
        return myDesk;
    }
    
    public void setMyDesk(MyDesk myDesk) {
        this.myDesk = myDesk;
    }
    
    public void setChair(String chair) {
        this.chair = chair;
    }
    
    public String getBed() {
        return bed;
    }
}
