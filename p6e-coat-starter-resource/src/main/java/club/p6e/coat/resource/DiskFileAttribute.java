package club.p6e.coat.resource;

import java.io.File;

/**
 * @author lidashuang
 * @version 1.0
 */
public class DiskFileAttribute implements FileAttribute {
    private final String name;
    private final long length;

    public DiskFileAttribute(File file) {
        this(file.getName(), file.length());
    }

    public DiskFileAttribute(String name, long length) {
        this.name = name;
        this.length = length;
    }

    @Override
    public long getLength() {
        return length;
    }

    @Override
    public String getName() {
        return name;
    }

}
