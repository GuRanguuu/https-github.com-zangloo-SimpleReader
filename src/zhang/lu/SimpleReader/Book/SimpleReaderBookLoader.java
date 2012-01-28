package zhang.lu.SimpleReader.Book;

import zhang.lu.SimpleReader.Config;
import zhang.lu.SimpleReader.VFS.CloudFile;
import zhang.lu.SimpleReader.VFS.VFile;

/**
 * Created by IntelliJ IDEA.
 * User: zhanglu
 * Date: 12-1-22
 * Time: 下午7:34
 */
public class SimpleReaderBookLoader implements BookLoader.Loader
{
	public static final String suffix = "srb";

	public boolean isBelong(VFile f)
	{
		return (!CloudFile.class.isInstance(f)) && (f.getPath().toLowerCase().endsWith("." + suffix));
	}

	public Book load(VFile f, Config.ReadingInfo ri) throws Exception
	{
		return new SimpleReaderBook(f, ri);
	}
}
