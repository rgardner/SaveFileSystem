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

public class ReadWriteUtil {
  // TODO XMLEncoder's broken. Write a persistence delegate, look for another solution, or disregard and use ObjectOut/In Stream
  private static String file_location_ = FileSystem.getCodeDirectory() + "treeObjects/";
  
  public static void serializeTree(DefaultMutableTreeNode tree, boolean use_xml) throws IOException {
    DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getRoot();
    CustomFile file = (CustomFile)root.getUserObject();
    String file_path = file_location_ + file.getName();
    
    if (use_xml) {
      XMLEncoder e = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(file_path + ".xml")));
      //e.setPersistenceDelegate(DefaultMutableTreeNode.class, new TreeNodePersistenceDelegate()); // FIXME runtime exceptions thrown
      e.writeObject(tree);
      e.close();
    } else {
      FileOutputStream fos = new FileOutputStream(file_path + ".tree");
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(tree);
      oos.close();
    }
  }
  
  public static List<DefaultMutableTreeNode> deserializeTreesFromFiles(boolean use_xml) throws IOException, ClassNotFoundException {
    // TODO handle IOException and ClassNotFoundException
    CustomFile saved_trees = new CustomFile(file_location_);
    CustomFile[] files = saved_trees.listCustomFiles();
    if (files.length == 0) {
      return null;
    }

    List<DefaultMutableTreeNode> list_of_trees = new ArrayList<DefaultMutableTreeNode>();
    for (CustomFile file : files) {
      if (!file.isFile()) continue; 
      
      if (use_xml) {
        if (!file.getName().endsWith(".xml")) continue;

        XMLDecoder d = new XMLDecoder(new BufferedInputStream(new FileInputStream(file.getPath())));
        Object tree_obj = d.readObject();
        d.close();
        DefaultMutableTreeNode tree_node = (DefaultMutableTreeNode) tree_obj; 
        list_of_trees.add(tree_node);        
      } else {
        if (!file.getName().endsWith(".tree")) continue;
        
        FileInputStream fis = new FileInputStream(file.getPath());
        ObjectInputStream ois = new ObjectInputStream(fis);
        DefaultMutableTreeNode tree = (DefaultMutableTreeNode) ois.readObject();
        ois.close();
        list_of_trees.add(tree);
      }
    }
    return list_of_trees;
  }
}

/*class TreeNodePersistenceDelegate extends PersistenceDelegate {
  // XXX Major problem here
  
  protected boolean mutatesTo(Object oldInstance, Object newInstance) {
    return oldInstance == newInstance;
  }
  
  @Override
  protected Expression instantiate(Object oldInstance, Encoder out) {
    DefaultMutableTreeNode d = (DefaultMutableTreeNode) oldInstance;
    DefaultMutableTreeNode root = (DefaultMutableTreeNode)d.getRoot();
    File file = (File)root.getUserObject();
    return new Expression(d, d.getClass(), "toString", new Object[]{file.getPath()});
  }
}*/