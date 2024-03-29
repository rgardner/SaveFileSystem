import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;


public class TreeGUI extends JPanel implements ItemListener {
    private static final long serialVersionUID = 1L;
    private DefaultMutableTreeNode originalTree;
    private DefaultTreeModel originalTreeModel;
    private static JFrame frame;
    private JTree tree = new JTree();
    private JTextArea fileInfo;
    private JTextField fieldFilter;
    private String filteredText = "";
    private boolean displayHiddenFiles = false;

    public TreeGUI() {
        // TODO handle null case from readTreesFromFiles
        super(new GridLayout(1, 0));
        List<DefaultMutableTreeNode> directories =
                new ArrayList<DefaultMutableTreeNode>();
        try {
            directories = ReadWriteUtil.deserializeTreesFromFiles();
        } catch (IOException e) {
            System.err.println("Failed to read trees from file");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        JPanel panel = buildDisplay(directories);
        add(panel);
    }

    public TreeGUI(final DefaultMutableTreeNode directory) {
        super(new GridLayout(1, 0));
        List<DefaultMutableTreeNode> directories =
                new ArrayList<DefaultMutableTreeNode>();
        directories.add(directory);
        JPanel panel = buildDisplay(directories);
        add(panel);
    }

    public final JPanel buildDisplay(
            final List<DefaultMutableTreeNode> directory) {
        JPanel total = new JPanel();
        total.setLayout(new BoxLayout(total, BoxLayout.Y_AXIS));

        // add trees from parameter to one tree
        originalTree = new DefaultMutableTreeNode();
        for (DefaultMutableTreeNode file : directory) {
            originalTree.add(file);
        }

        // create JLabel filter
        JPanel filter = new JPanel();
        filter.setLayout(new BoxLayout(filter, BoxLayout.X_AXIS));
        JLabel lblFilter = new JLabel("Filter: ");
        fieldFilter = new JTextField();
        lblFilter.setLabelFor(fieldFilter);

        filter.add(lblFilter, BorderLayout.WEST);
        filter.add(fieldFilter, BorderLayout.CENTER);
        filter.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldFilter.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(final DocumentEvent e) {
                filterTree(fieldFilter.getText());
            }
            @Override
            public void removeUpdate(final DocumentEvent e) {
                filterTree(fieldFilter.getText());
            }
            @Override
            public void insertUpdate(final DocumentEvent e) {
                filterTree(fieldFilter.getText());
            }
        });

        // set up tree JTree
        originalTreeModel = new DefaultTreeModel(originalTree);
        tree.setModel(originalTreeModel);
        tree.setCellRenderer(new Renderer());
        tree.setRootVisible(false);
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(final TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                        tree.getLastSelectedPathComponent();
                if (node == null) {
                    return;
                }
                CustomFile file = (CustomFile) node.getUserObject();
                displayInfo(file);
            }
        });
        filterTree("^[^\\.]*$");
        JScrollPane viewTree = new JScrollPane(tree);
        viewTree.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

        // set up JTextArea
        fileInfo = new JTextArea();
        fileInfo.setEditable(false);
        JScrollPane viewFile = new JScrollPane(fileInfo);
        viewFile.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        viewFile.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));

        // create split_pane that contains the JTree and JTextArea
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(viewTree);
        splitPane.setBottomComponent(viewFile);

        final int minWidth = 100;
        final int minHeight = 50;
        final int dividerLocation = 190;
        final int preferredWidth = 400;
        final int preferredHeight = 300;
        Dimension minimumSize = new Dimension(minWidth, minHeight);
        viewTree.setMinimumSize(minimumSize);
        viewFile.setMinimumSize(minimumSize);
        splitPane.setDividerLocation(dividerLocation);
        splitPane.setPreferredSize(
                new Dimension(preferredWidth, preferredHeight));
        splitPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        // create checkbox component
        JCheckBox cBXHiddenFiles = new JCheckBox("Display hidden files");
        cBXHiddenFiles.setSelected(false);
        cBXHiddenFiles.addItemListener(this);
        cBXHiddenFiles.setAlignmentX(Component.LEFT_ALIGNMENT);

        // add components to JPanel
        total.add(filter);
        total.add(splitPane);
        total.add(cBXHiddenFiles);
        return total;
    }

    public final void displayInfo(final CustomFile file) {
        String canRead = (file.canRead()) ? "Yes" : "No";
        String fileInfoStr
                = String.format("MIME Type:\t%s\n", file.getMimeType());
        fileInfoStr +=       String.format("File Path:\t%s\n", file.getPath());
        fileInfoStr +=       String.format("Read Access:\t%s\n", canRead);
        fileInfoStr +=       String.format("Last Modified:\t%s\n",
                new Date(file.lastModified()).toString());
        fileInfoStr +=       String.format("Last Indexed:\t%s\n",
                new Date(file.lastIndexed()).toString());
        fileInfo.setText(fileInfoStr);
    }

    @Override
    public final void itemStateChanged(final ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            // actually, the opposite of this is desired, consider regex
            displayHiddenFiles = true;
            filterTree("");
        } else if (e.getStateChange() == ItemEvent.DESELECTED) {
            displayHiddenFiles = false;
            filterTree("^[^\\.]*$");
        }
    }

    private void filterTree(final String filTxt) {
        filteredText = filTxt;

        if (filteredText.trim().toString().equals("")) {
            // reset the original root
            if (originalTreeModel.getRoot().equals(originalTree)) return;
            originalTreeModel.setRoot(originalTree);
        } else {
            TreeNodeBuilder tnb = new TreeNodeBuilder(filteredText);
            DefaultMutableTreeNode filteredRoot = copyNode(originalTree);
            filteredRoot
                = tnb.prune((DefaultMutableTreeNode) filteredRoot.getRoot());
            originalTreeModel.setRoot(filteredRoot);
        }
        tree.setModel(originalTreeModel);
        tree.updateUI();

        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }

    }

    private DefaultMutableTreeNode copyNode(DefaultMutableTreeNode orig) {

        DefaultMutableTreeNode newOne = new DefaultMutableTreeNode();
        newOne.setUserObject(orig.getUserObject());

        Enumeration enm = orig.children();

        while (enm.hasMoreElements()) {

            DefaultMutableTreeNode child
                = (DefaultMutableTreeNode) enm.nextElement();
            newOne.add(copyNode(child));
        }
        return newOne;
    }

    class Renderer extends DefaultTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row,
                boolean hasfocus) {

            super.getTreeCellRendererComponent(tree, value,
                    selected, expanded, leaf, row, hasfocus);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            if (leaf && !filteredText.equals("")
            && TreeNodeBuilder.patternMatches(filteredText, value.toString())) {

                Font f = getFont();
                f = new Font("Dialog", Font.BOLD, f.getSize());
                setFont(f);
            } else {
                Font f = getFont();
                f = new Font("Dialog", Font.PLAIN , f.getSize());
                setFont(f);
            }
            return this;
        }
    }

    public static void main(final String[] args) {
        frame = new JFrame("File System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new TreeGUI());
        frame.pack(); // sets size based on dimensions of components
        frame.setVisible(true);
    }
}
