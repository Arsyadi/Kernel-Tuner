/*
* This file is part of the Kernel Tuner.
*
* Copyright Predrag Čokulov <predragcokulov@gmail.com>
*
* Kernel Tuner is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Kernel Tuner is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Kernel Tuner. If not, see <http://www.gnu.org/licenses/>.
*/
package rs.pedjaapps.kerneltuner.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.stericson.RootTools.execution.CommandCapture;
import com.stericson.RootTools.RootTools;

public class FrequencyChanger extends AsyncTask<String, Void, String>
{
	Context context;

	public FrequencyChanger(Context context)
	{
		this.context = context;
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	final SharedPreferences preferences;
	@Override
	protected String doInBackground(String... args)
	{
		CommandCapture command = new CommandCapture(0, 
            "chmod 777 /sys/devices/system/cpu/" + args[0] + "/cpufreq/scaling_" + args[1] + "_freq",
            "echo " + args[2] + " > /sys/devices/system/cpu/" + args[0] + "/cpufreq/scaling_" + args[1] + "_freq",
            "chmod 444 /sys/devices/system/cpu/" + args[0] + "/cpufreq/scaling_" + args[1] + "_freq",
            "chown system /sys/devices/system/cpu/" + args[0] + "/cpufreq/scaling_" + args[1] + "_freq");
          try{
        RootTools.getShell(true).add(command);
		}
		catch(Exception e){
			
		}
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(args[0] + args[1], args[2]);
		editor.commit();
		return "";
	}
}	

