import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;

public final class FileSystem {

  private static final String CODE_CLASS_DIRECTORY =
      FileSystem.class.getProtectionDomain().getCodeSource()
      .getLocation().getPath();
  private static final String CODE_DIRECTORY =
      CODE_CLASS_DIRECTORY.substring(0, CODE_CLASS_DIRECTORY.length() - 4);

  public static String getCodeDirectory() {
    return CODE_DIRECTORY;
  }

  private FileSystem() {

  }
  public static DefaultMutableTreeNode dirToTree(final CustomFile directory)
      throws FileNotFoundException, SecurityException {
    if (!directory.exists() || !directory.isDirectory()) {
      System.err.println("Failed on: " + directory.getPath());
      throw new FileNotFoundException();
    }

    DefaultMutableTreeNode root = new DefaultMutableTreeNode(directory);
    CustomFile[] files = directory.listCustomFiles();
    if (files == null) {             // listFiles() returns null
      throw new SecurityException(); // if it doesn't give
    }                                // read permission

    for (CustomFile file : files) {
      if (file.isDirectory()) {
        root.add(dirToTree(file));
      } else {
        root.add(new DefaultMutableTreeNode(file));
      }
    }
    return root;
  }

  private static DefaultMutableTreeNode removeHiddenFiles(
      final DefaultMutableTreeNode directory) {
    DefaultMutableTreeNode directoryCopy = new DefaultMutableTreeNode();
    directoryCopy = directory;
    Enumeration<DefaultMutableTreeNode> children =
        directoryCopy.preorderEnumeration();
    while (children.hasMoreElements()) {
      DefaultMutableTreeNode node = children.nextElement();
      CustomFile file = (CustomFile) node.getUserObject();
      if (file.isHidden()) {
        node.removeFromParent();
        children = directoryCopy.preorderEnumeration();
      }
    }
    return directoryCopy;
  }

  public static void makeDirectory(final DefaultMutableTreeNode tree,
      final String destination) throws IOException {

  }

  public static void treeToTxtFile(DefaultMutableTreeNode tree,
      final String destination, final boolean displayHiddenFiles)
          throws IOException {
    // get name of root directory to name txt file
    DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getRoot();
    File rootFile = (File) root.getUserObject();
    PrintWriter out =
        new PrintWriter(destination + rootFile.getName() + ".txt");

    if (!displayHiddenFiles) {
      tree = removeHiddenFiles(tree);
    }

    Enumeration<DefaultMutableTreeNode> children = tree.preorderEnumeration();
    while (children.hasMoreElements()) {
      DefaultMutableTreeNode node = children.nextElement();
      File file = (File) node.getUserObject();
      for (int i = 0; i < node.getLevel(); i++) {
        out.print("--> ");
      }
      out.println(file.getName());
    }
    Date today = new Date();
    out.println("\nCreated: " + today.toString());
    out.close();
  }

  public static void main(final String[] args) {
    CustomFile directory;
    try {
      directory = new CustomFile(args[0]);
    } catch (ArrayIndexOutOfBoundsException e) {
      System.err.println("No directory specified");
      return;
    }
    try {
      String destination = CODE_DIRECTORY + "/txtFiles/";

      DefaultMutableTreeNode tree = dirToTree(directory);
      System.out.println("Built tree");

      ReadWriteUtil.serializeTree(tree, /*use XML*/ false);
      System.out.println("Saved Tree");

      treeToTxtFile(tree, destination, false);
      System.out.println("Success!");
    } catch (FileNotFoundException e) {
      System.err.println("File not found");
    } catch (SecurityException e) {
      System.err.println("The directory or one of"
          + "its subdirectories is unreadable");
    } catch (IOException e) {
      System.err.println("IOException");
    }
  }
}
