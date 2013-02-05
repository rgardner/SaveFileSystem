import java.io.FileNotFoundException;
import java.io.IOException;

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

            ReadWriteUtil.serializeTree(tree);
            System.out.println("Saved Tree");

            ReadWriteUtil.treeToTxtFile(tree, destination, false);
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
