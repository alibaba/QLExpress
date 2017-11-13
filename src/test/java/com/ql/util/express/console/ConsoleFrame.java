package com.ql.util.express.console;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.console.FileTree.PathNode;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.Map;

public class ConsoleFrame
    extends JFrame {
	private static final long serialVersionUID = 1L;
	JPanel contentPane;
  BorderLayout borderLayout1 = new BorderLayout();
  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenuFile = new JMenu();
  JMenuItem jMenuFileExit = new JMenuItem();
  JToolBar jToolBar = new JToolBar();
  JButton jButton1 = new JButton();
  ImageIcon image1 = new ImageIcon(ConsoleFrame.class.
                                   getResource("run.png"));
  JLabel statusBar = new JLabel();
  JTabbedPane jTabbedPaneContent = new JTabbedPane();
  JPanel jPaneRunner = new JPanel();
  JSplitPane jSplitPaneRun = new JSplitPane();
  BorderLayout borderLayout2 = new BorderLayout();
  JTextArea jTextAreaScript = new JTextArea();
  JScrollPane jScrollPaneScript = new JScrollPane();
  JSplitPane jSplitPaneS_C = new JSplitPane();
  JScrollPane jScrollPaneContext = new JScrollPane();
  JScrollPane jScrollPaneResult = new JScrollPane();
  JTextArea jTextAreaContext = new JTextArea();
  JTextArea jTextAreaResult = new JTextArea();
  JPanel jPanelResult = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  JLabel jLabelScript = new JLabel();
  JLabel jLabelResult = new JLabel();
  JLabel jLabelContext = new JLabel();
  JPanel jPanelScript = new JPanel();
  BorderLayout borderLayout4 = new BorderLayout();
  JPanel jPanelContext = new JPanel();
  BorderLayout borderLayout5 = new BorderLayout();
  StringBufferOutputStream output = new StringBufferOutputStream(jTextAreaResult);
	public ConsoleFrame() {
		try {
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			jbInit();

			PrintStream ps = new PrintStream(output,true);
			System.setOut(ps);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

  /**
   * Component initialization.
   *
   * @throws Exception
   */
  private void jbInit() throws Exception {
    contentPane = (JPanel) getContentPane();
    contentPane.setLayout(borderLayout1);
    setSize(new Dimension(1000, 600));
    setTitle("QLExpressConsole");
    statusBar.setText(" ");
    jMenuFile.setText("File");
    jMenuFileExit.setText("Exit");
    jMenuFileExit.addActionListener(new
                                    ConsoleFrame_jMenuFileExit_ActionAdapter(this));
    jSplitPaneRun.setOrientation(JSplitPane.VERTICAL_SPLIT);
    jSplitPaneRun.setDividerSize(2);
    jPaneRunner.setLayout(borderLayout2);
    jTextAreaScript.setText("");
    jTextAreaContext.setText("");
    jTextAreaResult.setText("");
    contentPane.setMinimumSize(new Dimension(500, 400));
    contentPane.setPreferredSize(new Dimension(500, 400));
    jButton1.addActionListener(new ConsoleFrame_jButton1_actionAdapter(this));
    jPanelResult.setLayout(borderLayout3);
    jLabelScript.setText("运行脚本");
    jLabelResult.setText("运行结果");
    jLabelContext.setText("脚本上下文");
    jPanelScript.setLayout(borderLayout4);
    jPanelContext.setLayout(borderLayout5);
    jMenuBar1.add(jMenuFile);
    jMenuFile.add(jMenuFileExit);
    setJMenuBar(jMenuBar1);
    jButton1.setIcon(image1);
    jButton1.setToolTipText("执行");
    jToolBar.add(jButton1);
    contentPane.add(statusBar, BorderLayout.SOUTH);
    jPanelResult.add(jScrollPaneResult, BorderLayout.CENTER);
    jPanelResult.add(jLabelResult, BorderLayout.NORTH);
    jSplitPaneRun.add(jSplitPaneS_C, JSplitPane.TOP);
    jScrollPaneResult.getViewport().add(jTextAreaResult);

    jPanelScript.add(jLabelScript, BorderLayout.NORTH);
    jPanelScript.add(jScrollPaneScript, BorderLayout.CENTER);
    jScrollPaneScript.getViewport().add(jTextAreaScript);
    jPanelContext.add(jLabelContext, BorderLayout.NORTH);
    jPanelContext.add(jScrollPaneContext, BorderLayout.CENTER);
    jSplitPaneS_C.add(jPanelScript, JSplitPane.LEFT);
    jScrollPaneContext.getViewport().add(jTextAreaContext);
    jSplitPaneS_C.setDividerSize(2);
    jSplitPaneS_C.setLastDividerLocation(200);
    jSplitPaneS_C.add(jPanelContext, JSplitPane.RIGHT);
    jSplitPaneS_C.setDividerLocation(500);
    jSplitPaneRun.add(jPanelResult, JSplitPane.RIGHT);
    jTabbedPaneContent.add(jPaneRunner, "\u6267\u884c\u4ee3\u7801");
    jPaneRunner.add(jSplitPaneRun, BorderLayout.CENTER);
    contentPane.add(jTabbedPaneContent, BorderLayout.CENTER);
    contentPane.add(jToolBar, BorderLayout.NORTH);
    jSplitPaneRun.setDividerLocation(200);
  }

  /**
   * File | Exit action performed.
   *
   * @param actionEvent ActionEvent
   */
  void jMenuFileExit_actionPerformed(ActionEvent actionEvent) {
    System.exit(0);
  }

  public void jButton1_actionPerformed(ActionEvent e) {
      String script =jTextAreaScript.getText();
      String[] lines = jTextAreaContext.getText().split("\n");
      String contextText ="";
      for(int i =0;i< lines.length;i++){
    	  if(lines[i].trim().length() >0){
    		  String[] tempStr = lines[i].trim().split(":");
    		  if(contextText.length() >0){
    			  contextText = contextText +",";
    		  }
    		  contextText = contextText +"\"" + tempStr[0] +"\":"  + tempStr[1];
    	  }
      }
      Object r = null;
	try {
		 output.clear();
		 ExpressRunner runner = new ExpressRunner(false,false);
		  contextText = "NewMap(" + contextText + ")";
	      @SuppressWarnings("unchecked")
		  Map<String,Object> tempMap =  (Map<String,Object>)runner.execute(contextText,null,null,false,false);
	      DefaultContext<String, Object> context = new DefaultContext<String, Object>();
	      context.putAll(tempMap);
	      r = runner.execute(script, context, null,false,false);
	      System.out.print("QL>\n" +
				"-------------------原始执行脚本--------------------------------\n" +
				script +  "\n" +
				"-------------------脚本运行结果--------------------------------\n" +
				r +"\n" +
				"-------------------运行后上下文--------------------------------\n" +
				context
				+ "\nQL>");
	} catch (Exception e1) {
	    e1.printStackTrace(System.out);
	}
  }

  public void jTreeFileSelect_mouseClicked(MouseEvent me) {
	  StringWriter writer = new StringWriter();
	  try {
		  TreePath tp = ((FileTree)me.getSource()).getPathForLocation(me.getX(), me.getY());
		  PathNode node  = (PathNode)tp.getPath()[tp.getPathCount() -1];
		  String fileName = node.getValue();
		  ExampleDefine define = ReadExample.readExampleDefine(fileName);
		  jTextAreaScript.setText(define.getScript());
		  jTextAreaContext.setText(define.getContext());
	} catch (Exception e) {
		  e.printStackTrace(new PrintWriter(writer));
	}

  }
}

class ConsoleFrame_jButton1_actionAdapter
    implements ActionListener {
  private ConsoleFrame adaptee;
  ConsoleFrame_jButton1_actionAdapter(ConsoleFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    try {
		adaptee.jButton1_actionPerformed(e);
	} catch (Exception e1) {
		e1.printStackTrace();
	}
  }
}

class ConsoleFrame_jMenuFileExit_ActionAdapter
    implements ActionListener {
  ConsoleFrame adaptee;

  ConsoleFrame_jMenuFileExit_ActionAdapter(ConsoleFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent actionEvent) {
    adaptee.jMenuFileExit_actionPerformed(actionEvent);
  }
}

class StringBufferOutputStream extends OutputStream
{
  protected ByteArrayOutputStream buffer;
  JTextArea jTextArea;
  public StringBufferOutputStream(JTextArea aJTextAreaResult)
  {
	  jTextArea = aJTextAreaResult;
	  buffer = new ByteArrayOutputStream();
  }
  public void clear(){
	  this.buffer.reset();
  }
  public void write(int ch)
    throws IOException
  {
    this.buffer.write(ch);
   
  }
  public void flush() throws IOException {
	  jTextArea.setText(this.buffer.toString());
  }
}
