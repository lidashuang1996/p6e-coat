package club.p6e.coat.resource;

/**
 * File Reader
 *
 * @author lidashuang
 * @version 1.0
 */
public interface FileReader<R> {

    /**
     * Get File Attribute Object
     *
     * @return File Attribute Object
     */
    FileAttribute getFileAttribute();

    /**
     * Execute File Read
     *
     * @return Resource Object
     */
    R execute();

    /**
     * Execute File Read
     *
     * @param position File Read Position
     * @param size     File Read Size
     * @return Resource Object
     */
    R execute(long position, long size);

}
