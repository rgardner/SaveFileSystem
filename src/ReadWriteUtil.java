import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

import javax.swing.tree.DefaultMutableTreeNode;

public final class ReadWriteUtil {

    private static final int RELATIVE_PATH = 0;
    private static final int LAST_INDEXED = 1;
    private static final int LAST_MODIFIED = 2;
    private static final int CAN_READ = 3;
    private static final int IS_DIRECTORY = 4;
    private static final int MIME_TYPE = 5;

    private static String fileLocation = FileSystem.getCodeDirectory()
            + "treeObjects/";

    private ReadWriteUtil() {

    }

    public static void serializeTree(final DefaultMutableTreeNode tree)
            throws IOException {
        // get path for writing the file
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getRoot();
        CustomFile rootFile = (CustomFile) root.getUserObject();
        String filePath = fileLocation + rootFile.getName();
        // write tree as csv
        PrintWriter out = new PrintWriter(filePath + ".csv");
        Enumeration<DefaultMutableTreeNode> children
                = tree.preorderEnumeration();
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode node = children.nextElement();
            CustomFile nodeFile = (CustomFile) node.getUserObject();
            int relativePathStart
                    = nodeFile.getPath().indexOf(rootFile.getName());
            String relativePath
                    = nodeFile.getPath().substring(relativePathStart);
            String nodeInfo = relativePath + ","
                    + nodeFile.lastIndexed() + ","
                    + nodeFile.lastModified() + ","
                    + nodeFile.canRead() + ","
                    + nodeFile.isDirectory() + ","
                    + nodeFile.getMimeType();
            out.println(nodeInfo);
        }
        out.close();
    }

    public static List<DefaultMutableTreeNode>
    deserializeTreesFromFiles()
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
            if (!file.getName().endsWith(".csv")) {
                continue;
            }
            List<CustomFile> treeDirs = new ArrayList<CustomFile>();
            List<CustomFile> treeFiles = new ArrayList<CustomFile>();
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String[] fileInfo = scanner.nextLine().split(",");
                String relativePath = fileInfo[RELATIVE_PATH];
                long lastIndexed = Long.parseLong(fileInfo[LAST_INDEXED]);
                long lastModified = Long.parseLong(fileInfo[LAST_MODIFIED]);
                boolean canRead = Boolean.parseBoolean(fileInfo[CAN_READ]);
                boolean isDirectory
                        = Boolean.parseBoolean(fileInfo[IS_DIRECTORY]);
                String mimeType = fileInfo[MIME_TYPE];
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
            if (!treeDirs.get(0).getName().equals("testDirectory")) {
                for (CustomFile dummy : treeFiles) {
                    dummy.delete();
                }
                for (int i = treeDirs.size() - 1; i >= 0; i--) {
                    treeDirs.get(i).delete();
                }
            }
            scanner.close();
        }
        return listOfTrees;
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

        Enumeration<DefaultMutableTreeNode> children
                = tree.preorderEnumeration();
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
}
