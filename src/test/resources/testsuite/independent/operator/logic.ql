assert(true && true);
assertFalse(true && false);
assertFalse(true && null);

assertFalse(false && true);
assertFalse(false && false);
assertFalse(false && null);

/*
com.alibaba.qlexpress4.exception.QLRuntimeException: [Error: condition expression result must be bool]
[Near: ...alse(null && true); as...]
                    ^^
[Line: 9, Column: 18]

	at com.alibaba.qlexpress4.exception.QLException.reportErr(QLException.java:124)
	at com.alibaba.qlexpress4.exception.QLException.reportErrWithToken(QLException.java:86)
	at com.alibaba.qlexpress4.exception.QLException.reportRuntimeErr(QLException.java:73)
	at com.alibaba.qlexpress4.exception.DefaultErrorReporter.report(DefaultErrorReporter.java:26)
	at com.alibaba.qlexpress4.runtime.instruction.JumpIfInstruction.execute(JumpIfInstruction.java:38)
	at com.alibaba.qlexpress4.runtime.QLambdaInner.call(QLambdaInner.java:45)
	at com.alibaba.qlexpress4.Express4Runner.execute(Express4Runner.java:51)
	at com.alibaba.qlexpress4.TestSuiteRunner.handleFile(TestSuiteRunner.java:98)
	at com.alibaba.qlexpress4.TestSuiteRunner.featureDebug(TestSuiteRunner.java:56)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:59)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:56)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.internal.runners.statements.RunBefores.evaluate(RunBefores.java:26)
	at org.junit.runners.ParentRunner$3.evaluate(ParentRunner.java:306)
	at org.junit.runners.BlockJUnit4ClassRunner$1.evaluate(BlockJUnit4ClassRunner.java:100)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:366)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:103)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:63)
	at org.junit.runners.ParentRunner$4.run(ParentRunner.java:331)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:79)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:329)
	at org.junit.runners.ParentRunner.access$100(ParentRunner.java:66)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:293)
	at org.junit.runners.ParentRunner$3.evaluate(ParentRunner.java:306)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:413)
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:69)
	at com.intellij.rt.junit.IdeaTestRunner$Repeater$1.execute(IdeaTestRunner.java:38)
	at com.intellij.rt.execution.junit.TestsRepeater.repeat(TestsRepeater.java:11)
	at com.intellij.rt.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:35)
	at com.intellij.rt.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:232)
	at com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:55)
*/
assertFalse(null && true);
assertFalse(null && false);
assertFalse(null && null);
