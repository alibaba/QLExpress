package com.ql.util.express.test.newmatch;

import org.junit.Test;

import com.ql.util.express.match.INodeTypeManager;
import com.ql.util.express.match.NodeTypeManagerTestImpl;
import com.ql.util.express.match.QLPattern;
import com.ql.util.express.match.QLPatternNode;

public class QLPatternDefineTest {
	INodeTypeManager manager = new NodeTypeManagerTestImpl();

	@Test
	public void testDefine() throws Exception {
		String[] defines = new String[] {
				//"ABC",
				//"ABC^*",
//				"\\(~$ID$\\)~#()",
//				"CONST$(+^$(CONST|ID))^{1:222}#SQL",
//				"(CONST|ID)$((*|/)^$CONST)^*",
//				"(CONST|ID)$(,~$(CONST|ID))*#PARAMETER_LIST",
//				"OPDATA$(.->FIELD_CALL^$ID->CONST_STRING)^*",
//				"\\(->CHILD_EXPRESS",
				"OP_LEVEL1|OP_LEVEL2|OP_LEVEL3|OP_LEVEL4|OP_LEVEL5|OP_LEVEL6|OP_LEVEL7|OP_LEVEL8|OP_LEVEL9|=|LEFT_BRACKET|RIGHT_BRACKET"
				};
		for (String s : defines) {
			QLPatternNode  t = QLPattern.createPattern(manager,"ANONY_PATTERN", s);
			System.out.println(t);
		}
	}
}
