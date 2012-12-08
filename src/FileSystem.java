import java.io.File;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.SecurityException;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Date;

public class FileSystem {
  
  private static final String CODE_CLASS_DIRECTORY_ = FileSystem.class.getProtectionDomain().getCodeSource().getLocation().getPath(); 
  private static final String CODE_DIRECTORY_ = CODE_CLASS_DIRECTORY_.substring(0, CODE_CLASS_DIRECTORY_.length() - 4);
  
  public static String getCodeDirectory() {
    return CODE_DIRECTORY_;
  }
  
  public static DefaultMutableTreeNode dirToTree(CustomFile directory) throws FileNotFoundException, SecurityException {
    if (!directory.exists() || !directory.isDirectory()) {
      System.err.println("Failed on: " + directory.getPath());
      throw new FileNotFoundException();
    }
    
    DefaultMutableTreeNode root = new DefaultMutableTreeNode(directory);
    CustomFile[] files = directory.listCustomFiles();
    if (files == null) {                  // listFiles() returns null if it doesn't give read permission
      throw new SecurityException();
    }
    
    for (CustomFile file : files) {
      if (file.isDirectory()) {
        root.add(dirToTree(file));
      } else {
        root.add(new DefaultMutableTreeNode(file));
      }
    }
    return root;
  }
  
  private static DefaultMutableTreeNode removeHiddenFiles(DefaultMutableTreeNode directory) {
    DefaultMutableTreeNode directory_copy = new DefaultMutableTreeNode(); 
    directory_copy = directory;
    Enumeration<DefaultMutableTreeNode> children = directory_copy.preorderEnumeration();
    while (children.hasMoreElements()) {
      DefaultMutableTreeNode node = children.nextElement();
      CustomFile file = (CustomFile)node.getUserObject();
      if (file.isHidden()) {
        node.removeFromParent();
        children = directory_copy.preorderEnumeration();
      }
    }
    return directory_copy;
  }
  
  public static void makeDirectory(DefaultMutableTreeNode tree, String destination) throws IOException {
    
  }
  
  public static void treeToTxtFile(DefaultMutableTreeNode tree, String destination, boolean display_hidden_files) throws IOException {
    // get name of root directory to name txt file
    DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getRoot();
    File root_file = (File)root.getUserObject();
    PrintWriter out = new PrintWriter(destination + root_file.getName() + ".txt");
    
    if (!display_hidden_files) {
      tree = removeHiddenFiles(tree);
    }
    
    Enumeration<DefaultMutableTreeNode> children = tree.preorderEnumeration();
    while (children.hasMoreElements()) {
      DefaultMutableTreeNode node = children.nextElement();
      File file = (File)node.getUserObject();
      for (int i = 0; i < node.getLevel(); i++) {
        out.print("--> ");
      }
      out.println(file.getName());
    }
    Date today = new Date();
    out.println("\nCreated: " + today.toString());
    out.close();
  }
  
  public static void main(String[] args) {
    CustomFile directory;
    try {
      directory = new CustomFile(args[0]);
    } catch (ArrayIndexOutOfBoundsException e) {
      System.err.println("No directory specified");
      return;
    }
    try {
      String destination = CODE_DIRECTORY_ + "/txtFiles/";
      
      DefaultMutableTreeNode tree = dirToTree(directory);
      System.out.println("Built tree");
      
      ReadWriteUtil.serializeTree(tree, /*use XML*/ false);
      System.out.println("Saved Tree");
      
      treeToTxtFile(tree, destination, false);
      System.out.println("Success!");
    }  catch (IllegalArgumentException e) {
      System.err.println("Illegal Argsument");
    } catch (FileNotFoundException e) {
      System.err.println("File not found");
    } catch (SecurityException e) {
      System.err.println("The directory or one of its subdirectories is unreadable");
    } catch (IOException e) {
      System.err.println("IOException");
    }
  }
}
