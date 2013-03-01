import javax.swing.tree.DefaultMutableTreeNode;
import java.util.regex.Pattern;

public class TreeNodeBuilder {

    private String textToMatch;

    public TreeNodeBuilder(String textToMatch) {
        this.textToMatch = textToMatch;
    }

    public DefaultMutableTreeNode prune(DefaultMutableTreeNode root) {

        boolean badLeaves = true;

        //keep looping through until tree contains only leaves that match
        while (badLeaves) {
            badLeaves = removeBadLeaves(root);
        }
        return root;
    }

    /**
     *
     * @param root
     * @return boolean bad leaves were returned
     */
    private boolean removeBadLeaves(final DefaultMutableTreeNode root) {

        //no bad leaves yet
        boolean badLeaves = false;

        //reference first leaf
        DefaultMutableTreeNode leaf = root.getFirstLeaf();

        //if leaf is root then its the only node
        if (leaf.isRoot()) {
            return false;
        }
        int leafCount = root.getLeafCount(); //  this get method changes if
        for (int i = 0; i < leafCount; i++) { // in for loop so have to
            // define outside of it
            DefaultMutableTreeNode nextLeaf = leaf.getNextLeaf();

            //if it does not start with the text then snip it off its parent
            if (!patternMatches(textToMatch, leaf.getUserObject().toString())) {
                DefaultMutableTreeNode parent
                = (DefaultMutableTreeNode) leaf.getParent();

                if (parent != null) {
                    parent.remove(leaf);
                }
                badLeaves = true;
            }
            leaf = nextLeaf;
        }
        return badLeaves;
    }

    public static boolean patternMatches(final String pattern, final String s) {
        // determine if string or regex, use indexOf if not, pa
        if (containsMetaCharacters(pattern)) {
            return Pattern.matches(pattern, s);
        } else {
            return s.indexOf(pattern) != -1;
        }
    }

    private static boolean containsMetaCharacters(final String s) {
        String expression = s;
        if (expression.equals("")) return false;
        String escaped = Pattern.quote(expression);
        return !expression.equals(escaped);
    }
}
