package com.ql.util.express.console;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
/**
* <p>Description: 系统目录树，动态生成，解决载入慢的问题 </p>
*/
public class FileTree extends JTree {
	private static final long serialVersionUID = 1L;
	private DefaultTreeModel model;
    public FileTree (String dir) {
      this.initData(dir);
       this.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                node_mouseAction(e);
            }
        });
    }
    private void node_mouseAction(MouseEvent e){
        int row = this.getRowForLocation(e.getX(), e.getY());
        PathNode pathNode =null;
        if(row != -1){
        TreePath path = this.getPathForRow(row);
        pathNode = (PathNode)path.getLastPathComponent();
        if(pathNode.isFolder()&&pathNode.getChildCount()==0){
            builderNode(pathNode);
            this.expandPath(path);
        }
        }
    }
    private PathNode builderNode(PathNode pathNode){
        String filePath= pathNode.getValue().toString();
        File file=new File(filePath);
        File[] files=file.listFiles();
       for(int i=0;i<files.length;i++){
    	   if(files[i].isDirectory() || files[i].getName().endsWith(".ql")){
            PathNode node=new PathNode(files[i].getName(), files[i].getAbsolutePath(),files[i].isDirectory());
            pathNode.add(node);
           }    	   
       }
        return pathNode;
    }
    public void initData(String  rootPath){
        File f=new File(rootPath);
        PathNode root=new PathNode(f.getName(), rootPath,f.isDirectory());
        File[] files=f.listFiles();
        for(int i=0;i<files.length;i++){
        	if(files[i].isDirectory() || files[i].getName().endsWith(".ql")){
        		PathNode node=new PathNode(files[i].getName(), files[i].getAbsolutePath(),files[i].isDirectory());
        		root.add(node);
        	}
        }
        model=new DefaultTreeModel(root);
        this.setModel(model);
        FileTreeRenderer renderer=new FileTreeRenderer();
        this.setCellRenderer(renderer);
        this.repaint();
}
    class FileTreeRenderer implements TreeCellRenderer{
        private Icon folder_open=new ImageIcon("com/ql/util/console/openFile.png");
        private Icon folder_close=new ImageIcon("com/ql/util/console/closeFile.png");
        private Icon file=new ImageIcon("com/ql/util/console/help.png");
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {
            JLabel label = null;
           if (value != null) {
                if(value instanceof PathNode){
                    PathNode pathNode = (PathNode) value;
                    if (pathNode.isFolder()) {
                        if (expanded) {
                            label = new JLabel(pathNode.getUserObject().
                                               toString(),
                                               folder_open, JLabel.RIGHT);
                        } else{// if(!expanded||leaf) {
                            label = new JLabel(pathNode.getUserObject().
                                               toString(),
                                               folder_close, JLabel.RIGHT);
                        }
                    } else {
                        label = new JLabel(pathNode.getUserObject().toString(),
                                           file, JLabel.RIGHT);
                    }
                    return label;
                }
           }
            return label;
        }
}
    class PathNode extends DefaultMutableTreeNode{
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		String value;
        boolean isFolder;
       public PathNode(String name,Object value,boolean isFolder){
           super(name);
           this.value=value.toString();
           this.isFolder=isFolder;
        }
        public String getValue(){
          return value;
        }
        public boolean isFolder(){
            return isFolder;
        }
        public String toString(){
          return this.value+":" + this.isFolder;
        }
    }
}
