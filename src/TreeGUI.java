import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JScrollPane;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TreeGUI extends JPanel implements ItemListener {
  private static final long serialVersionUID = 1L; // required to give it a serialVersionUID
  private static JFrame frame_;
  private JTree tree_;
  private JTextArea file_info_;
  
  public TreeGUI() {
    // TODO handle null case from readTreesFromFiles
    super(new GridLayout(1, 0));
    List<DefaultMutableTreeNode> directories = new ArrayList<DefaultMutableTreeNode>();
    try {
      directories = ReadWriteUtil.deserializeTreesFromFiles(/*use XML*/ false);
    } catch (IOException e) {
      System.err.println("Failed to read trees from file");
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    JPanel panel = buildDisplay(directories);
    add(panel);
  }
  
  public TreeGUI(DefaultMutableTreeNode directory) {
    super(new GridLayout(1, 0));
    List<DefaultMutableTreeNode> directories = new ArrayList<DefaultMutableTreeNode>();
    directories.add(directory);
    JPanel panel = buildDisplay(directories);
    add(panel);
  }
  
  public JPanel buildDisplay(List<DefaultMutableTreeNode> directory) {
    JPanel total = new JPanel();
    total.setLayout(new BoxLayout(total, BoxLayout.Y_AXIS));
    
    // add trees from parameter to one tree
    DefaultMutableTreeNode all_trees = new DefaultMutableTreeNode();
    for (DefaultMutableTreeNode file : directory) {
      all_trees.add(file);
    }
    
    // set up tree JTree
    tree_ = new JTree(all_trees);
    tree_.setRootVisible(false);
    tree_.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree_.addTreeSelectionListener(new TreeSelectionListener() {
      @Override public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree_.getLastSelectedPathComponent();
        if (node == null) return;
        CustomFile file = (CustomFile)node.getUserObject();
        displayInfo(file);
      }
    });
    JScrollPane view_tree = new JScrollPane(tree_);
    view_tree.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
    
    // set up JTextArea
    file_info_ = new JTextArea();
    file_info_.setEditable(false);
    JScrollPane view_file = new JScrollPane(file_info_);
    view_file.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
    view_file.getHorizontalScrollBar().setPreferredSize(new Dimension(0,0));
    
    // create split_pane that contains the JTree and JTextArea
    JSplitPane split_pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    split_pane.setTopComponent(view_tree);
    split_pane.setBottomComponent(view_file);
    
    Dimension minimumSize = new Dimension(100, 50);
    view_tree.setMinimumSize(minimumSize);
    view_file.setMinimumSize(minimumSize);
    split_pane.setDividerLocation(190);
    split_pane.setPreferredSize(new Dimension(400, 300));
    
    JCheckBox view_hidden_files = new JCheckBox("Display hidden files");
    view_hidden_files.setSelected(false);
    view_hidden_files.addItemListener(this);
    
    total.add(split_pane);
    total.add(view_hidden_files);
    return total;
  }
  
  public void displayInfo(CustomFile file) {
    String can_read = file.canRead() ? "Yes" : "No";
    String file_info = String.format("MIME Type:\t%s\n", file.getMimeType());
    file_info +=       String.format("File Path:\t%s\n", file.getPath());
    file_info +=       String.format("Read Access:\t%s\n", can_read);
    file_info +=       String.format("Last Modified:\t%s\n", new Date(file.lastModified()).toString());
    file_info +=       String.format("Last Indexed:\t%s\n", file.getLastIndexed().toString());
    file_info_.setText(file_info);
  }
  
  public void itemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      // TODO implement change, view http://docs.oracle.com/javase/tutorial/uiswing/components/tree.html for DynamicTreeDemo
    }
  }
  public static void main(String[] args) {
    frame_ = new JFrame("File System");
    frame_.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame_.add(new TreeGUI());
    frame_.pack();                       // sets size based on dimensions of components
    frame_.setVisible(true);
  }
}
