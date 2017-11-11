package com.ql.util.express.test.spring;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * （1）打通了spring容器，通过扩展IExpressContext->QLExpressContext
 * 获取本地变量的时候，可以获取到spring的bean
 * （2）在runner初始化的时候，使用了函数映射功能：addFunctionOfServiceMethod
 * （3）在runner初始化的时候，使用了代码映射功能：addMacro
 */
public class QlExpressUtil implements ApplicationContextAware {

	private static ExpressRunner runner;
	static {
		runner = new ExpressRunner();
	}
	private static boolean isInitialRunner = false;
	private ApplicationContext applicationContext;// spring上下文

	/**
	 * 
	 * @param statement
	 *            执行语句
	 * @param context
	 *            上下文
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Object execute(String statement, Map<String, Object> context)
			throws Exception {
		initRunner(runner);
		IExpressContext expressContext = new QLExpressContext(context,
				applicationContext);
		return runner.execute(statement, expressContext, null, true, false);
	}

	private void initRunner(ExpressRunner runner) {
		if (isInitialRunner == true) {
			return;
		}
		synchronized (runner) {
			if (isInitialRunner == true) {
				return;
			}
			try {
				runner.addFunctionOfServiceMethod("读取用户信息",applicationContext.getBean("bizLogicBean"), "getUserInfo", new Class[] {String.class}, null); 
                runner.addMacro("判定用户是否vip","userDO.salary>200000");
				
			} catch (Exception e) {
				throw new RuntimeException("初始化失败表达式", e);
			}
		}
		isInitialRunner = true;
	}

	public void setApplicationContext(ApplicationContext aContext)
			throws BeansException {
		applicationContext = aContext;
	}

}
