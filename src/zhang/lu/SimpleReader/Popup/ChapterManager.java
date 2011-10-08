package zhang.lu.SimpleReader.Popup;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import zhang.lu.SimpleReader.Book.BookContent;
import zhang.lu.SimpleReader.R;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: zhanglu
 * Date: 11-9-27
 * Time: 下午1:28
 */
public class ChapterManager extends PopupList
{
	private Typeface tf = null;
	private ArrayAdapter<String> aa;
	private int chapter;

	public ChapterManager(Context context, final AdapterView.OnItemClickListener onItemClickListener)
	{
		super(context);

		setTitle(context.getString(R.string.menu_chapter));
		aa = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1)
		{
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				View v = super.getView(position, convertView, parent);
				TextView tv = (TextView) v.findViewById(android.R.id.text1);
				tv.setTypeface(tf);
				if (position == chapter)
					tv.setTextColor(Color.RED);
				else
					tv.setTextColor(Color.BLACK);
				return v;
			}
		};
		setAdapter(aa);
		setOnItemClickListener(onItemClickListener);
	}

	public void show(ArrayList<BookContent.ChapterInfo> cs, int index, Typeface typeface, int top, int width, int height)
	{
		aa.clear();
		for (BookContent.ChapterInfo c : cs)
			aa.add(c.title());
		aa.notifyDataSetChanged();

		tf = typeface;
		setWidth(width);
		setHeight(height);
		showAtLocation(getContentView(), Gravity.LEFT | Gravity.CENTER, 0, top);
		setSelection(chapter = index);
	}

	public void hide()
	{
		tf = null;
		dismiss();
	}
}