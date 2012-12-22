import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

import javax.swing.tree.DefaultMutableTreeNode;

public final class ReadWriteUtil {
  // TODO Save to CSV
  private static String fileLocation = FileSystem.getCodeDirectory()
      + "treeObjects/";

  private ReadWriteUtil() {

  }

  public static void serializeTree(final DefaultMutableTreeNode tree,
      final boolean useCSV) throws IOException {
    DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getRoot();
    CustomFile file = (CustomFile) root.getUserObject();
    String filePath = fileLocation + file.getName();
    if (useCSV) {
      PrintWriter out = new PrintWriter(filePath + ".csv");
      Enumeration<DefaultMutableTreeNode> children = tree.preorderEnumeration();
      while (children.hasMoreElements()) {
        DefaultMutableTreeNode node = children.nextElement();
        CustomFile nodeFile = (CustomFile) node.getUserObject();
        int relativePathStart = nodeFile.getPath().indexOf(file.getName());
        String relativePath = nodeFile.getPath().substring(relativePathStart);
        String nodeInfo = relativePath + ","
                        + nodeFile.lastIndexed() + ","
                        + nodeFile.lastModified() + ","
                        + nodeFile.canRead() + ","
                        + nodeFile.isDirectory() + ","
                        + nodeFile.getMimeType();
        out.println(nodeInfo);
      }
      out.close();
    } else {
      FileOutputStream fos = new FileOutputStream(filePath + ".tree");
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(tree);
      oos.close();
    }
  }

  public static List<DefaultMutableTreeNode>
  deserializeTreesFromFiles(final boolean useCSV)
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

      if (useCSV) {
        if (!file.getName().endsWith(".csv")) {
          continue;
        }
        List<CustomFile> treeDirs = new ArrayList<CustomFile>();
        List<CustomFile> treeFiles = new ArrayList<CustomFile>();
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
          String[] fileInfo = scanner.nextLine().split(",");
          String relativePath = fileInfo[0];
          long lastIndexed = Long.parseLong(fileInfo[1]);
          long lastModified = Long.parseLong(fileInfo[2]);
          boolean canRead = Boolean.parseBoolean(fileInfo[3]);
          Boolean isDirectory = Boolean.parseBoolean(fileInfo[4]);
          String mimeType = fileInfo[5];
          CustomFile dummy = new CustomFile(relativePath,
              lastIndexed, lastModified, canRead, mimeType);
          if (isDirectory) {
            treeDirs.add(dummy);
          } else {
            treeFiles.add(dummy);
          }
        }

        // make files
        for (CustomFile dir : treeDirs) {
          dir.mkdir();
        }
        for (CustomFile dummy : treeFiles) {
          dummy.createNewFile();
        }
        // make tree and add to listOfTrees
        listOfTrees.add(FileSystem.dirToTree(treeDirs.get(0)));

        // delete files
        System.out.println(treeDirs.get(0).getName());
        if (!treeDirs.get(0).getName().equals("testDirectory")) {
          for (CustomFile dummy : treeFiles) {
            dummy.delete();
          }
          for (int i = treeDirs.size() - 1; i >= 0; i--) {
            treeDirs.get(i).delete();
          }
        }
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
