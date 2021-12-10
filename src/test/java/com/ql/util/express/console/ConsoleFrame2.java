package com.ql.util.express.console;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import javax.swing.*;
import javax.swing.tree.TreePath;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.console.FileTree.PathNode;

public class ConsoleFrame2
    extends JFrame {
    private static final long serialVersionUID = 1L;
    JPanel contentPane;
    BorderLayout borderLayout1 = new BorderLayout();
    JMenuBar jMenuBar1 = new JMenuBar();
    JMenu jMenuFile = new JMenu();
    JMenuItem jMenuFileExit = new JMenuItem();
    JToolBar jToolBar = new JToolBar();
    JButton jButton1 = new JButton();
    ImageIcon image1 = new ImageIcon(ConsoleFrame2.class.
        getResource("run.png"));
    JLabel statusBar = new JLabel();
    JSplitPane jSplitPaneMain = new JSplitPane();
    FileTree jTreeFileSelect = new FileTree(System.getProperty("user.dir"));
    JTabbedPane jTabbedPaneContent = new JTabbedPane();
    JPanel jPaneRunner = new JPanel();
    JPanel jPanelHelp = new JPanel();
    JPanel jPanel3 = new JPanel();
    JSplitPane jSplitPaneRun = new JSplitPane();
    BorderLayout borderLayout2 = new BorderLayout();
    JTextArea jTextAreaScript = new JTextArea();
    JScrollPane jScrollPaneScript = new JScrollPane();
    JSplitPane jSplitPaneS_C = new JSplitPane();
    JScrollPane jScrollPaneContext = new JScrollPane();
    JScrollPane jScrollPaneResult = new JScrollPane();
    JTextArea jTextAreaContext = new JTextArea();
    JTextArea jTextAreaResult = new JTextArea();
    JScrollPane jScrollPaneTree = new JScrollPane();
    JPanel jPanelResult = new JPanel();
    BorderLayout borderLayout3 = new BorderLayout();
    JLabel jLabelScript = new JLabel();
    JLabel jLabelResult = new JLabel();
    JLabel jLabelContext = new JLabel();
    JPanel jPanelScript = new JPanel();
    BorderLayout borderLayout4 = new BorderLayout();
    JPanel jPanelContext = new JPanel();
    BorderLayout borderLayout5 = new BorderLayout();

    public ConsoleFrame2() {
        try {
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            jbInit();
            //      PrintStream ps = new PrintStream(System.out) {
            //          public void println(String x) {
            //        	  jTextAreaResult.insert(x,jTextAreaResult.getLineCount());
            //          }
            //        };
            //      System.setOut(ps);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Component initialization.
     *
     */
    private void jbInit() {
        contentPane = (JPanel)getContentPane();
        contentPane.setLayout(borderLayout1);
        setSize(new Dimension(1000, 600));
        setTitle("QLExpressConsole");
        statusBar.setText(" ");
        jMenuFile.setText("File");
        jMenuFileExit.setText("Exit");
        jMenuFileExit.addActionListener(new
            ConsoleFrame_jMenuFileExit_ActionAdapter2(this));
        jTreeFileSelect.addMouseListener(new
            ConsoleFrame_jTreeFileSelect_mouseAdapter2(this));
        jSplitPaneMain.add(jScrollPaneTree, JSplitPane.LEFT);
        jSplitPaneMain.add(jTabbedPaneContent, JSplitPane.RIGHT);
        jSplitPaneMain.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        jSplitPaneRun.setOrientation(JSplitPane.VERTICAL_SPLIT);
        jSplitPaneRun.setDividerSize(2);
        jPaneRunner.setLayout(borderLayout2);
        jTextAreaScript.setText("");
        jTextAreaContext.setText("");
        jTextAreaResult.setText("");
        contentPane.setMinimumSize(new Dimension(500, 400));
        contentPane.setPreferredSize(new Dimension(500, 400));
        jButton1.addActionListener(new ConsoleFrame_jButton1_actionAdapter2(this));
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
        contentPane.add(jSplitPaneMain, BorderLayout.CENTER);
        jTabbedPaneContent.add(jPaneRunner, "执行代码");
        jTabbedPaneContent.add(jPanelHelp, "关键字");
        jTabbedPaneContent.add(jPanel3, "帮助");
        jPaneRunner.add(jSplitPaneRun, BorderLayout.CENTER);
        jScrollPaneTree.getViewport().add(this.jTreeFileSelect);
        jScrollPaneContext.getViewport().add(jTextAreaContext);
        jScrollPaneResult.getViewport().add(jTextAreaResult);
        jPanelResult.add(jScrollPaneResult, BorderLayout.CENTER);
        jPanelResult.add(jLabelResult, BorderLayout.NORTH);
        jPanelScript.add(jLabelScript, BorderLayout.NORTH);
        jPanelScript.add(jScrollPaneScript, BorderLayout.CENTER);
        jScrollPaneScript.getViewport().add(jTextAreaScript);
        jPanelContext.add(jLabelContext, BorderLayout.NORTH);
        jPanelContext.add(jScrollPaneContext, BorderLayout.CENTER);
        jSplitPaneS_C.setDividerSize(2);
        jSplitPaneS_C.setLastDividerLocation(200);
        jSplitPaneS_C.add(jPanelContext, JSplitPane.RIGHT);
        jSplitPaneS_C.add(jPanelScript, JSplitPane.LEFT);
        jSplitPaneS_C.setDividerLocation(500);
        jSplitPaneRun.add(jPanelResult, JSplitPane.RIGHT);
        jSplitPaneRun.add(jSplitPaneS_C, JSplitPane.TOP);
        contentPane.add(jToolBar, BorderLayout.NORTH);
        contentPane.add(jScrollPaneTree, BorderLayout.WEST);
        jScrollPaneTree.getViewport().add(jTreeFileSelect);
        jSplitPaneRun.setDividerLocation(200);
        jSplitPaneMain.setDividerLocation(400);
    }

    /**
     * File | Exit action performed.
     *
     * @param actionEvent ActionEvent
     */
    void jMenuFileExit_actionPerformed(ActionEvent actionEvent) {
        System.exit(0);
    }

    @SuppressWarnings("unchecked")
    public void jButton1_actionPerformed(ActionEvent e) {
        String script = jTextAreaScript.getText();
        String[] lines = jTextAreaContext.getText().split("\n");
        String contextText = "";
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].trim().length() > 0) {
                String[] tempStr = lines[i].trim().split(":");
                if (contextText.length() > 0) {
                    contextText = contextText + ",";
                }
                contextText = contextText + "\"" + tempStr[0] + "\":" + tempStr[1];
            }
        }
        Object r;
        StringWriter writer = new StringWriter();
        try {
            ExpressRunner runner = new ExpressRunner(false, true);
            contextText = "NewMap(" + contextText + ")";
            Map<String, Object> tempMap = (Map<String, Object>)runner.execute(contextText, null, null, false, false);
            DefaultContext<String, Object> context = new DefaultContext<>();
            context.putAll(tempMap);
            r = runner.execute(script, context, null, false, true);
            writer.write("QL>\n" +
                "-------------------原始执行脚本--------------------------------\n" +
                script + "\n" +
                "-------------------脚本运行结果--------------------------------\n" +
                r + "\n" +
                "-------------------运行后上下文--------------------------------\n" +
                context
                + "\nQL>");
        } catch (Exception e1) {
            e1.printStackTrace(new PrintWriter(writer));

        }
        //System.out.println(writer.toString());
        jTextAreaResult.setText(writer.toString());
    }

    public void jTreeFileSelect_mouseClicked(MouseEvent me) {
        StringWriter writer = new StringWriter();
        try {
            TreePath tp = ((FileTree)me.getSource()).getPathForLocation(me.getX(), me.getY());
            PathNode node = (PathNode)tp.getPath()[tp.getPathCount() - 1];
            String fileName = node.getValue();
            ExampleDefine define = ReadExample.readExampleDefine(fileName);
            jTextAreaScript.setText(define.getScript());
            jTextAreaContext.setText(define.getContext());
        } catch (Exception e) {
            e.printStackTrace(new PrintWriter(writer));
        }
    }
}

class ConsoleFrame_jTreeFileSelect_mouseAdapter2
    extends MouseAdapter {
    private final ConsoleFrame2 adaptee;

    ConsoleFrame_jTreeFileSelect_mouseAdapter2(ConsoleFrame2 adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseClicked(MouseEvent e) {
        adaptee.jTreeFileSelect_mouseClicked(e);
    }
}

class ConsoleFrame_jButton1_actionAdapter2
    implements ActionListener {
    private final ConsoleFrame2 adaptee;

    ConsoleFrame_jButton1_actionAdapter2(ConsoleFrame2 adaptee) {
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

class ConsoleFrame_jMenuFileExit_ActionAdapter2
    implements ActionListener {
    ConsoleFrame2 adaptee;

    ConsoleFrame_jMenuFileExit_ActionAdapter2(ConsoleFrame2 adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        adaptee.jMenuFileExit_actionPerformed(actionEvent);
    }
}
