import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;


public class TreeGUI extends JPanel implements ItemListener {
  private static final long serialVersionUID = 1L; // required to give it a serialVersionUID
  private static JFrame frame_;
  private JTree tree_;
  private JTextArea file_info_;
  private JTextField field_filter_;

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

    // create JLabel filter
    JPanel filter = new JPanel();
    filter.setLayout(new BoxLayout(filter, BoxLayout.X_AXIS));
    JLabel lbl_filter = new JLabel("Filter: ");
    field_filter_ = new JTextField();
    lbl_filter.setLabelFor(field_filter_);

    filter.add(lbl_filter, BorderLayout.WEST);
    filter.add(field_filter_, BorderLayout.CENTER);
    filter.setAlignmentX(Component.LEFT_ALIGNMENT);

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
        CustomFile file = (CustomFile) node.getUserObject();
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
    view_file.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));

    // create split_pane that contains the JTree and JTextArea
    JSplitPane split_pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    split_pane.setTopComponent(view_tree);
    split_pane.setBottomComponent(view_file);

    int min_width = 100;
    int min_height = 50;
    int divider_location = 190;
    int preferred_width = 400;
    int preferred_height = 300;
    Dimension minimumSize = new Dimension(min_width, min_height);
    view_tree.setMinimumSize(minimumSize);
    view_file.setMinimumSize(minimumSize);
    split_pane.setDividerLocation(divider_location);
    split_pane.setPreferredSize(new Dimension(preferred_width, preferred_height));
    split_pane.setAlignmentX(Component.LEFT_ALIGNMENT);

    // create checkbox component
    JCheckBox cbx_view_hidden_files = new JCheckBox("Display hidden files");
    cbx_view_hidden_files.setSelected(false);
    cbx_view_hidden_files.addItemListener(this);
    cbx_view_hidden_files.setAlignmentX(Component.LEFT_ALIGNMENT);

    // add components to JPanel
    total.add(filter);
    total.add(split_pane);
    total.add(cbx_view_hidden_files);
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
      /* TODO implement change view
       * http://docs.oracle.com/javase/tutorial/uiswing/components/tree.html
       * for DynamicTreeDemo
       */
    }
  }

  public static void main(String[] args) {
    frame_ = new JFrame("File System");
    frame_.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame_.add(new TreeGUI());
    frame_.pack();               // sets size based on dimensions of components
    frame_.setVisible(true);
  }
}
