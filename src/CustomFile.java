import java.io.File;
import java.util.Date;

import javax.activation.MimetypesFileTypeMap;

public class CustomFile extends File {

  private static final long serialVersionUID = 1L;
  private Date lastIndexed;
  public CustomFile(final String pathname) {
    super(pathname);
    Date today = new Date();
    lastIndexed = today;
  }

  @Override
  public String toString() {
    return this.getName();
  }

  public Date getLastIndexed() {
    return this.lastIndexed;
  }

  public CustomFile[] listCustomFiles() throws SecurityException {
    File[] files = this.listFiles();
    if (files == null) {
      throw new SecurityException();
    }

    CustomFile[] filesCustom = new CustomFile[files.length];
    for (int i = 0; i < files.length; i++) {
      CustomFile fileCustom = new CustomFile(files[i].getPath());
      filesCustom[i] = fileCustom;
    }
    return filesCustom;
  }

  public String getMimeType() {
    return new MimetypesFileTypeMap().getContentType(this);
  }
}
