package zhang.lu.SimpleReader;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by IntelliJ IDEA.
 * User: zhanglu
 * Date: 10-12-9
 * Time: 下午8:38
 */
public class OptionDialog extends Dialog implements AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener
{
	public static interface OnOptionAcceptListener
	{
		void onOptionAccept(String optstr);
	}

	private static final String[] fontSizeStringList = new String[]{"20", "22", "24", "26", "28", "30", "32", "34",};
	private static final int[] fontSizeList = new int[]{20, 22, 24, 26, 28, 30, 32, 34,};
	private static final String colorFormatString = "%03d";
	private static final String[] zipEncodeList = new String[]{"GBK", "BIG5", "UTF8"};
	private static String[] pagingDirectList = null;
	private static String[] colorModeList;
	private TextView tp;
	private OnOptionAcceptListener oal;

	private int r, g, b;
	private int br, bg, bb;
	int color, bcolor, ncolor, nbcolor;
	int fs;
	boolean isBright;
	private Config conf;
	private Config.PagingDirect pds[] = Config.PagingDirect.values();

	public OptionDialog(Context context)
	{
		super(context);
	}


	public void init(OnOptionAcceptListener listener)
	{
		oal = listener;
		setContentView(R.layout.optdlg);
		setTitle(getContext().getString(R.string.dialog_option_title));

		tp = (TextView) findViewById(R.id.text_preview);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
									android.R.layout.simple_spinner_item,
									fontSizeStringList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner spinner = (Spinner) findViewById(R.id.font_size);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);

		Button btn = (Button) findViewById(R.id.button_cancel);
		btn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				dismiss();
			}
		});
		btn = (Button) findViewById(R.id.button_ok);
		btn.setOnClickListener(this);

		adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, zipEncodeList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner = (Spinner) findViewById(R.id.zip_encode);
		spinner.setAdapter(adapter);

		//paging direct
		if (pagingDirectList == null) {
			pagingDirectList = new String[pds.length];
			for (int i = 0; i < pds.length; i++)
				pagingDirectList[i] = getPagingDirectText(pds[i]);
		}

		adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,
						   pagingDirectList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner = (Spinner) findViewById(R.id.paging_direct);
		spinner.setAdapter(adapter);

		if (colorModeList == null)
			colorModeList = new String[]{getContext().getString(R.string.color_mode_day), getContext()
				.getString(R.string.color_mode_night)};
		adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, colorModeList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner = (Spinner) findViewById(R.id.color_mode);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);

	}

	public void update(String optstr)
	{
		conf = new Config(null);
		conf.optFromString(optstr);
		color = conf.getColor();
		bcolor = conf.getBColor();
		ncolor = conf.getNColor();
		nbcolor = conf.getNBColor();
		fs = conf.getFontSize();

		tp.setTextSize(fs);

		isBright = conf.isColorBright();
		loadColor(isBright);

		Spinner spinner = (Spinner) findViewById(R.id.font_size);
		for (int i = 0; i < fontSizeList.length; i++)
			if (fontSizeList[i] == fs) {
				spinner.setSelection(i, true);
				break;
			}


		RadioGroup rg = (RadioGroup) findViewById(R.id.view_style);
		if (conf.isHanStyle())
			rg.check(R.id.han_style);
		else
			rg.check(R.id.xi_style);

		spinner = (Spinner) findViewById(R.id.zip_encode);
		for (int i = 0; i < zipEncodeList.length; i++)
			if (zipEncodeList[i].equals(conf.getZipEncode())) {
				spinner.setSelection(i, true);
				break;
			}

		spinner = (Spinner) findViewById(R.id.paging_direct);
		for (int i = 0; i < pagingDirectList.length; i++)
			if (pagingDirectList[i].equals(getPagingDirectText(conf.getPagingDirect()))) {
				spinner.setSelection(i, true);
				break;
			}

		spinner = (Spinner) findViewById(R.id.color_mode);
		spinner.setSelection(conf.isColorBright() ? 0 : 1, true);

		CheckBox de = (CheckBox) findViewById(R.id.dict_enabled);
		de.setChecked(conf.isDictEnabled());

		spinner = (Spinner) findViewById(R.id.dict_file);
		String[] fl = (new File(Reader.dictPath)).list(new FilenameFilter()
		{
			public boolean accept(File file, String s)
			{
				return s.endsWith(Reader.dictSuffix);
			}
		});
		if ((fl != null) && (fl.length > 0)) {
			for (int i = 0; i < fl.length; i++)
				fl[i] = fl[i].substring(0, fl[i].length() - Reader.dictSuffix.length());
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
										android.R.layout.simple_spinner_item,
										fl);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);

			if (!conf.getDictFile().equals(Reader.DICT_FILE_NONE))
				for (int i = 0; i < fl.length; i++)
					if (fl[i].equals(conf.getDictFile())) {
						spinner.setSelection(i, false);
						break;
					}
			de.setOnCheckedChangeListener(this);
		} else
			de.setOnCheckedChangeListener(null);
		if (!conf.isDictEnabled())
			spinner.setVisibility(View.INVISIBLE);

		ScrollView sv = (ScrollView)findViewById(R.id.opt_scroll_view);
		sv.scrollTo(0, 0);
	}

	public void onClick(View view)
	{
		saveColor(isBright);

		conf.setFontSize(fs);
		conf.setColor(color);
		conf.setBColor(bcolor);
		conf.setNColor(ncolor);
		conf.setNBColor(nbcolor);
		//conf.setColorBright(isBright);
		conf.setHanStyle(
			((RadioGroup) findViewById(R.id.view_style)).getCheckedRadioButtonId() == R.id.han_style);

		conf.setDictEnabled(((CheckBox) findViewById(R.id.dict_enabled)).isChecked());

		Object df = ((Spinner) findViewById(R.id.dict_file)).getSelectedItem();
		if (df == null)
			conf.setDictFile(Reader.DICT_FILE_NONE);
		else
			conf.setDictFile(df.toString());

		String pdstring = ((Spinner) findViewById(R.id.paging_direct)).getSelectedItem().toString();
		int i;
		for (i = 0; i < pagingDirectList.length; i++)
			if (pagingDirectList[i].equals(pdstring))
				break;
		conf.setPagingDirect(pds[i]);
		conf.setZipEncode(((Spinner) findViewById(R.id.zip_encode)).getSelectedItem().toString());
		oal.onOptionAccept(conf.optToString());
		dismiss();
	}

	public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
	{
		Spinner s = (Spinner) adapterView;
		switch (s.getId()) {
			case R.id.font_size:
				fs = fontSizeList[s.getSelectedItemPosition()];
				tp.setTextSize(fs);
				break;
			case R.id.color_mode:
				isBright = (s.getSelectedItemPosition() == 0);
				saveColor(!isBright);
				loadColor(isBright);
				break;
		}
	}

	public void onNothingSelected(AdapterView<?> adapterView)
	{
	}

	public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser)
	{
		TextView v;

		switch (seekBar.getId()) {
			case R.id.font_color_red:
				v = (TextView) findViewById(R.id.font_color_red_value);
				r = i;
				break;
			case R.id.font_color_green:
				v = (TextView) findViewById(R.id.font_color_green_value);
				g = i;
				break;
			case R.id.font_color_blue:
				v = (TextView) findViewById(R.id.font_color_blue_value);
				b = i;
				break;
			case R.id.font_bcolor_red:
				v = (TextView) findViewById(R.id.font_bcolor_red_value);
				br = i;
				break;
			case R.id.font_bcolor_green:
				v = (TextView) findViewById(R.id.font_bcolor_green_value);
				bg = i;
				break;
			case R.id.font_bcolor_blue:
				v = (TextView) findViewById(R.id.font_bcolor_blue_value);
				bb = i;
				break;
			default:
				Log.println(Log.ERROR, "OptionDialog", "onProgressChanged id = " + seekBar.getId());
				return;
		}

		v.setText(String.format(colorFormatString, i));
		tp.setTextColor(Color.rgb(r, g, b));
		tp.setBackgroundColor(Color.rgb(br, bg, bb));
	}

	public void onStartTrackingTouch(SeekBar seekBar)
	{
	}

	public void onStopTrackingTouch(SeekBar seekBar)
	{
	}

	private void saveColor(boolean bright)
	{
		if (bright) {
			color = Color.rgb(r, g, b);
			bcolor = Color.rgb(br, bg, bb);
		} else {
			ncolor = Color.rgb(r, g, b);
			nbcolor = Color.rgb(br, bg, bb);
		}

	}

	private void loadColor(boolean bright)
	{
		tp = (TextView) findViewById(R.id.text_preview);
		if (bright) {
			r = Color.red(color);
			g = Color.green(color);
			b = Color.blue(color);
			br = Color.red(bcolor);
			bg = Color.green(bcolor);
			bb = Color.blue(bcolor);
			tp.setTextColor(color);
			tp.setBackgroundColor(bcolor);

		} else {
			r = Color.red(ncolor);
			g = Color.green(ncolor);
			b = Color.blue(ncolor);
			br = Color.red(nbcolor);
			bg = Color.green(nbcolor);
			bb = Color.blue(nbcolor);
			tp.setTextColor(ncolor);
			tp.setBackgroundColor(nbcolor);
		}

		SeekBar seekBar = (SeekBar) findViewById(R.id.font_color_red);
		seekBar.setProgress(r);
		seekBar.setOnSeekBarChangeListener(this);
		TextView v = (TextView) findViewById(R.id.font_color_red_value);
		v.setText(String.format(colorFormatString, r));

		seekBar = (SeekBar) findViewById(R.id.font_color_green);
		seekBar.setProgress(g);
		seekBar.setOnSeekBarChangeListener(this);
		v = (TextView) findViewById(R.id.font_color_green_value);
		v.setText(String.format(colorFormatString, g));

		seekBar = (SeekBar) findViewById(R.id.font_color_blue);
		seekBar.setProgress(b);
		seekBar.setOnSeekBarChangeListener(this);
		v = (TextView) findViewById(R.id.font_color_blue_value);
		v.setText(String.format(colorFormatString, b));

		seekBar = (SeekBar) findViewById(R.id.font_bcolor_red);
		seekBar.setProgress(br);
		seekBar.setOnSeekBarChangeListener(this);
		v = (TextView) findViewById(R.id.font_bcolor_red_value);
		v.setText(String.format(colorFormatString, br));

		seekBar = (SeekBar) findViewById(R.id.font_bcolor_green);
		seekBar.setProgress(bg);
		seekBar.setOnSeekBarChangeListener(this);
		v = (TextView) findViewById(R.id.font_bcolor_green_value);
		v.setText(String.format(colorFormatString, bg));

		seekBar = (SeekBar) findViewById(R.id.font_bcolor_blue);
		seekBar.setProgress(bb);
		seekBar.setOnSeekBarChangeListener(this);
		v = (TextView) findViewById(R.id.font_bcolor_blue_value);
		v.setText(String.format(colorFormatString, bb));
	}

	private String getPagingDirectText(Config.PagingDirect pd)
	{
		switch (pd) {
			case up:
				return getContext().getString(R.string.paging_up_label);
			case down:
				return getContext().getString(R.string.paging_down_label);
			case left:
				return getContext().getString(R.string.paging_left_label);
			case right:
				return getContext().getString(R.string.paging_right_label);
			case clickUp:
				return getContext().getString(R.string.paging_click_up_label);
			case clickDown:
				return getContext().getString(R.string.paging_click_down_label);
			case clickLeft:
				return getContext().getString(R.string.paging_click_left_label);
			case clickRight:
				return getContext().getString(R.string.paging_click_right_label);
		}
		return null;
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		View df = findViewById(R.id.dict_file);
		df.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
	}
}

