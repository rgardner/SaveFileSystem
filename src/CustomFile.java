import java.io.File;
import java.util.Date;

import javax.activation.MimetypesFileTypeMap;

public class CustomFile extends File {

    private static final long serialVersionUID = 1L;
    private long lastIndexed;
    private String mimeType = new MimetypesFileTypeMap().getContentType(this);
    private boolean canRead;
    public CustomFile(final String pathname) {
        super(pathname);
        canRead = canRead();
        Date today = new Date();
        lastIndexed = today.getTime();
    }

    public CustomFile(final String relativePathLoc, final long lastIndexedLoc,
            final long lastModifiedLoc,
            final boolean canReadLoc, final String mimeTypeLoc) {
        super(relativePathLoc);
        lastIndexed = lastIndexedLoc;
        setLastModified(lastModifiedLoc);
        canRead = canReadLoc;
        mimeType = mimeTypeLoc;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean canRead() {
        return canRead;
    }

    public long lastIndexed() {
        return lastIndexed;
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
        return mimeType;
    }
}
