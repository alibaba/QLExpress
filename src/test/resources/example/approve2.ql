function 审批通过(String a,int b){
  System.out.println(a + "审批:金额:" + b);
  if(b > 6000)
    return false;
  return true;
}

function 报销入账(int a){
  System.out.println("报销入卡:金额:"+a);
}

function 打回修改(String a){
  System.out.println("重填:申请人:"+a);
}
