import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

public final class ReadWriteUtil {
  // TODO Save to CSV
  private static String fileLocation = FileSystem.getCodeDirectory()
      + "treeObjects/";

  private ReadWriteUtil() {

  }

  public static void serializeTree(final DefaultMutableTreeNode tree,
      final boolean useXML) throws IOException {
    DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getRoot();
    CustomFile file = (CustomFile) root.getUserObject();
    String filePath = fileLocation + file.getName();

    if (useXML) {
      try {
        XMLEncoder e = new XMLEncoder(
            new BufferedOutputStream(
                new FileOutputStream(filePath + ".xml")));
        e.writeObject(tree);
        e.close();
      } catch (IOException e) {
        throw new RuntimeException("unexpected error doing...", e);
      }
    } else {
      FileOutputStream fos = new FileOutputStream(filePath + ".tree");
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(tree);
      oos.close();
    }
  }

  public static List<DefaultMutableTreeNode>
  deserializeTreesFromFiles(final boolean useXML)
      throws IOException, ClassNotFoundException {
    // TODO handle IOException and ClassNotFoundException
    CustomFile savedTrees = new CustomFile(fileLocation);
    CustomFile[] files = savedTrees.listCustomFiles();
    if (files.length == 0) {
      return null;
    }

    List<DefaultMutableTreeNode> listOfTrees =
        new ArrayList<DefaultMutableTreeNode>();
    for (CustomFile file : files) {
      if (!file.isFile()) {
        continue;
      }

      if (useXML) {
        if (!file.getName().endsWith(".xml")) {
          continue;
        }

        XMLDecoder d =
            new XMLDecoder(new BufferedInputStream(
                new FileInputStream(
                    file.getPath()
            )));
        Object treeObj = d.readObject();
        d.close();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treeObj;
        listOfTrees.add(treeNode);
      } else {
        if (!file.getName().endsWith(".tree")) {
          continue;
        }

        FileInputStream fis = new FileInputStream(file.getPath());
        ObjectInputStream ois = new ObjectInputStream(fis);
        DefaultMutableTreeNode tree = (DefaultMutableTreeNode) ois.readObject();
        ois.close();
        listOfTrees.add(tree);
      }
    }
    return listOfTrees;
  }
}
