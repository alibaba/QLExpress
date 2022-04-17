package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.runtime.instruction.QLInstruction;

import java.util.List;

/**
 * QLExpress Virtual Machine
 * @author 悬衡
 * date 2022/1/12 2:28 下午
 */
public interface QVm {

    QResult execute(List<QLInstruction> instructions, QLOptions qlOptions);

}
