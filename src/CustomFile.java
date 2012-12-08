import java.io.File;
import java.util.Date;

import javax.activation.MimetypesFileTypeMap;

public class CustomFile extends File {
  
  private static final long serialVersionUID = 1L;
  private Date last_indexed_;
  public CustomFile(String pathname) {
    super(pathname);
    Date today = new Date();
    last_indexed_ = today;
  }
  
  @Override
  public String toString() {
    return this.getName();
  }

  public Date getLastIndexed() {
    return this.last_indexed_;
  }
  
  public CustomFile[] listCustomFiles() throws SecurityException {
    File[] files = this.listFiles();
    if (files == null) {
      throw new SecurityException();
    }
    
    CustomFile[] files_custom = new CustomFile[files.length];
    for (int i = 0; i < files.length; i++) {
      CustomFile file_custom = new CustomFile(files[i].getPath());
      files_custom[i] = (file_custom);
    }
    return files_custom;
  }
  
  public String getMimeType() {
    return new MimetypesFileTypeMap().getContentType(this);
  }
}
