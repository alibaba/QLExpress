package com.ql.util.express.test.newmatch;

import org.junit.Test;

import com.ql.util.express.parse.ExpressNode;
import com.ql.util.express.parse.ExpressParse;
import com.ql.util.express.parse.KeyWordDefine4SQL;
import com.ql.util.express.parse.NodeTypeManager;

public class SqlTest {
	
	public String[] testString ={
			"select id as id2 from upp_biz_order",
			"select id as id2,name as name2 from upp_biz_order where a=1",
			"select id as id2,name from upp_biz_order where 1=1",
			};
	@Test
	public void testDefine() throws Exception {
		NodeTypeManager manager = new NodeTypeManager(new KeyWordDefine4SQL());
		ExpressParse parse = new ExpressParse(manager,null,false);
		for(String text : testString){
			ExpressNode result = parse.parse(null, text, true, null);
			System.out.print(result);
		}
		
	}
}
