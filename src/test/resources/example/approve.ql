function 审批通过(String a, int b) {
    System.out.println(a + "审批:金额:" + b);
    return true;
}

function 报销入账(int a) {
    System.out.println("报销入卡:金额:" + a);
}

function 打回修改(String a) {
    System.out.println("重填:申请人:" + a);
}

如果 (审批通过(经理, 金额)) {
    如果 (金额 大于 5000) {
        如果 (审批通过(总监, 金额)) {
            如果 (审批通过(财务, 金额)) {
                报销入账(金额)
            } 否则 {
                打回修改(申请人)
            }
        } 否则 {
            打回修改(申请人)
        }
    } 否则 {
        如果 (审批通过(财务, 金额)) {
            报销入账(金额)
        } 否则 {
            打回修改(申请人)
        }
    }
} 否则 {
    打回修改(申请人)
}
打印("完成")
