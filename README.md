SaveFileSystem
==============

Save information about files located on an external volume.
The GUI displays the hierarchy of files and the information about each file.

# To Run #

    To test savingObjects using XMLEncoder or ObjectOutputStream, ReadWriteUtil, CustomFile, and FileSystem need to be
    compiled. 

    Pass a directory into FileSystem and it'll save the files to the treeObjects directory. By running TreeGUI, you can
    see the hierarchy or alternatively, you can view the text files created in the directory 'txtFiles'.

# Files #
## src: ##
  
    ReadWriteUtil -  contains the read and write methods to serialize DefaultMutableTreeNode. Both methods use booleans
                     to determine if they use XMLEncoder or ObjectOutputStream. XMLEncoder / XMLDecoder throw runtime
                     exceptions.

    FileSystem -     contains dirToTree, treeToTxtFile, and main method that takes a dir as its argument

    TreeGUI -        contains all the gui code. Chechbox and filter unimplemented, but the gui's laid out.

    CustomFile -     extends java.io.File, saves the time it was indexed

    FileSystemTest - currently blank (originally used to test directory crawler)

    FileTree -       extends DefaultMutableTreeNode, contains unimplemented hideHiddenFiles method


##  other:  ##
  
    testDirectory -  a dummy directory used for testing purposes

    treeObjects -    contains the serialized DefaultMutableTreeNode files

    txtFiles -       contains txtFiles showing hierarchy of files

# Development #
## Not yet implemented ##

    filter trees based on filename         -- TreeGUI

    enable show/hide hidden files checkbox -- TreeGUI

## bugs: ##

    fix window resizing                    -- TreeGUI

## Food for thought: ##

    move fileInfo to the right of the file hierarchy -- TreeGUI
    Reasoning: trees take up lots of vertical space, so by moving the file info to the right of it, more of the tree is visible at any given moment, takes advantage of empty space