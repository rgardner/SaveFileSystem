SaveFileSystem
==============

Save information about files located on an external volume.
The GUI displays the hierarchy of files and information about each file.


Files:
  src:
    ReadWriteUtil -  contains the read and write methods to serialize
                     DefaultMutableTreeNode. Both methods use booleans to determine
                     if they use XMLEncoder or ObjectOutputStream. XMLEncoder /
                     XMLDecoder throws runtime exceptions.

    FileSystem -     contains directoryCrawler, treeToTxtFile methods, and the main
                     method that takes a directory as its argument

    TreeGUI -        contains all the gui code. Chechbox and filter unimplemented,
                     but the gui's laid out.

    CustomFile -     extends java.io.File, saves the time it was indexed

    FileSystemTest - currently blank (originally used to test directory crawler)

    FileTree -       extends DefaultMutableTreeNode, contains unimplemented
                     hideHiddenFiles method


  testDirectory -    a dummy directory used for testing purposes

  treeObjects -      contains the serialized DefaultMutableTreeNode files

  txtFiles -         contains txtFiles showing hierarchy of files