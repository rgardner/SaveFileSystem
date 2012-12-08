Which one do you think is more readable:

# More nesting #


    public static DefaultMutableTreeNode dirToTree(CustomFile directory) throws SecurityException, FileNotFoundException {
      if (directory.exists() && directory.isDirectory()) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(directory);
        CustomFile[] files = directory.listFiles();
        if (files == null) {                 // listFiles() returns null if it doesn't give read permission
          System.err.println("Failed on: " + directory.getPath());
          throw new FileNotFoundException();
        }
        for (CustomFile file : files) {
          if (file.isDirectory()) {
            root.add(dirToTree(file));
          } else {
            root.add(new DefaultMutableTreeNode(file));
          }
        }
        return root;
      } else {
        throw new FileNotFoundException();
      }
    }

# Less nesting #

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