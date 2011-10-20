package zhang.lu.SimpleReader.Book;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: zhanglu
 * Date: 11-9-6
 * Time: 下午8:32
 */
public abstract class BookContent
{
	public static class ContentPosInfo
	{
		public int line, offset;
	}

	public static class ChapterInfo
	{
		protected String title;

		public ChapterInfo(String t) {title = t;}

		public String title() {return title;}
	}

	// return chapter count
	public int getChapterCount()
	{
		return 1;
	}

	// return current chapter title
	public String getChapterTitle()
	{
		return getChapterTitle(getCurrChapter());
	}

	// return chapter title at index
	public String getChapterTitle(int index)
	{
		return null;
	}

	// return all chapter title
	public ArrayList<ChapterInfo> getChapterInfoList()
	{
		return null;
	}

	// get current chapter index
	public int getCurrChapter()
	{
		return 0;
	}

	// switch to chapter index
	public boolean gotoChapter(int index)
	{
		return !((index < 0) || (index >= getChapterCount()) || (index == getCurrChapter())) &&
			loadChapter(index);
	}

	// load chapter info
	protected boolean loadChapter(int index)
	{
		return false;
	}

	// has notes?
	public boolean hasNotes()
	{
		return false;
	}

	// return null when no note at line:offset
	public String getNote(int line, int offset)
	{
		return null;
	}

	// search txt from cpi
	public ContentPosInfo searchText(String txt, ContentPosInfo cpi)
	{
		for (int i = cpi.line; i < getLineCount(); i++) {
			int pos = line(i).indexOf(txt, cpi.offset);
			if (pos >= 0) {
				cpi.line = i;
				cpi.offset = pos;
				return cpi;
			}
			cpi.offset = 0;
		}
		return null;
	}

	// get position info for <x>%
	public ContentPosInfo getPercentPos(int percent)
	{
		int p = size() * percent / 100;
		int c = 0, i;

		for (i = 0; i < getLineCount(); i++) {
			c += line(i).length();
			if (c > p)
				break;
		}
		ContentPosInfo cpi = new ContentPosInfo();
		if (c > p) {
			cpi.line = i;
			cpi.offset = line(i).length() - (c - p);
		} else {
			cpi.line = i - 1;
			cpi.offset = 0;
		}
		return cpi;
	}

	// return line at index
	public abstract String line(int index);

	// return line count
	public abstract int getLineCount();

	// return book size from line[0] to line[end - 1]
	public abstract int size(int end);

	// return book total size
	public abstract int size();
}
