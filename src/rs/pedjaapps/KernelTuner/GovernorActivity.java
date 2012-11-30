package rs.pedjaapps.KernelTuner;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class GovernorActivity extends Activity
{

	GovernorSettingsAdapter govAdapter ;
	ListView govListView;
	String[] filesx;
	List<String> fileList;
	List<String> availableGovs = CPUInfo.availableGovs();
	List<String> govValues;
	String newvalue;
	String curfile;
	String governor = "xondemand";
	List<String> governors;
	List<String> temp;
	public static final String ARG_SECTION_NUMBER = "section_number";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.governor_settings);

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean ads = sharedPrefs.getBoolean("ads", true);
		if (ads == true)
		{AdView adView = (AdView)findViewById(R.id.ad);
			adView.loadAd(new AdRequest());}
		govListView = (ListView) findViewById(R.id.list);
		if (!availableGovs.isEmpty())
		{

			govAdapter = new GovernorSettingsAdapter(this, R.layout.governor_list_item);
			govListView.setAdapter(govAdapter);

			for (final GovEntry entry : getGovEntries())
			{
				govAdapter.add(entry);
			}
		}
		else
		{
			TextView tv = (TextView)findViewById(R.id.textView1); 
			tv.setVisibility(View.VISIBLE);
			tv.setText(getResources().getString(R.string.gov_not_supported));
		}
		govListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, final int position, long id) 
				{

					String[] valuess = govValues.toArray(new String[0]);
					AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

					builder.setTitle(fileList.get(position));

					builder.setMessage(getResources().getString(R.string.gov_new_value));

					builder.setIcon(R.drawable.ic_menu_edit);


					final EditText input = new EditText(view.getContext());
					input.setHint(valuess[position]);
					input.selectAll();
					input.setInputType(InputType.TYPE_CLASS_NUMBER);
					input.setGravity(Gravity.CENTER_HORIZONTAL);

					builder.setPositiveButton(getResources().getString(R.string.apply), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								newvalue = String.valueOf(input.getText());
								curfile = fileList.get(position);
								new ChangeGovernorSettings(GovernorActivity.this).execute(new String[] {String.valueOf(input.getText()), fileList.get(position), governors.get(position)});

								try
								{
									Thread.sleep(700);
								}
								catch (InterruptedException e)
								{
									new LogWriter().execute(new String[] {getClass().getName(), e.getMessage()});
								}
								getGovEntries();

								govAdapter.clear();
								for (final GovEntry entry : getGovEntries())
								{
									govAdapter.add(entry);
								}
								govAdapter.notifyDataSetChanged();
								govListView.invalidate();
							}
						});
					builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface arg0, int arg1)
							{
								// TODO Auto-generated method stub

							}

						});
					builder.setView(input);

					AlertDialog alert = builder.create();

					alert.show();


				} 
			});

	}

	

	private List<GovEntry> getGovEntries()
	{

		final List<GovEntry> entries = new ArrayList<GovEntry>();
		fileList = new ArrayList<String>();
		govValues = new ArrayList<String>();
		governors = new ArrayList<String>();
		temp = new ArrayList<String>();

		for (String s : availableGovs)
		{
			File gov = new File("/sys/devices/system/cpu/cpufreq/" + s + "/");

			if (gov.exists())
			{
				File[] files = gov.listFiles();
				if(files!=null){
				for (File file : files)
				{
					temp.add(file.getName());
					fileList.add(file.getName());

				}


				for (int i = 0; i < temp.size(); i++)
				{

					try
					{

		    			File myFile = new File("/sys/devices/system/cpu/cpufreq/" + s + "/" + temp.get(i).trim());
		    			FileInputStream fIn = new FileInputStream(myFile);
		    			BufferedReader myReader = new BufferedReader(
							new InputStreamReader(fIn));
		    			String aDataRow = "";
		    			String aBuffer = "";
		    			while ((aDataRow = myReader.readLine()) != null)
						{
		    				aBuffer += aDataRow + "\n";
		    			}	    			


		    			myReader.close();

		    			entries.add(new GovEntry(temp.get(i), aBuffer));
		    			govValues.add(aBuffer);
		    			governors.add(s);


		    		}
					catch (Exception e)
					{
						new LogWriter().execute(new String[] {getClass().getName(), e.getMessage()});
		    		}

				}
				}

			}

			temp.clear();


		}


		return entries;
	}
}
