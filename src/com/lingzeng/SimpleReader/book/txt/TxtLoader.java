package com.lingzeng.SimpleReader.book.txt;

import com.lingzeng.SimpleReader.Config;
import com.lingzeng.SimpleReader.ContentLine;
import com.lingzeng.SimpleReader.UString;
import com.lingzeng.SimpleReader.book.*;
import com.lingzeng.SimpleReader.vfs.VFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhanglu
 * Date: 11-3-5
 * Time: 上午10:23
 */

public class TxtLoader implements BookLoader.Loader
{
	private static final String suffix = "txt";

	public boolean isBelong(VFile f)
	{
		return f.getPath().toLowerCase().endsWith("." + suffix);
	}

	private static String formatText(String txt)
	{
		return txt.replace("\r", "");
	}

	public Book load(VFile f, Config.ReadingInfo ri) throws Exception
	{
		List<ContentLine> lines = new ArrayList<>();
		String cs;

		InputStream fs = f.getInputStream();
		cs = BookUtil.detect(fs);
		fs.close();

		BufferedReader br = new BufferedReader(new InputStreamReader(f.getInputStream(), cs));

		String line;
		while ((line = br.readLine()) != null)
			lines.add(new UString(formatText(line)));
		return new SingleChapterBook(new ContentBase(lines));
	}
}