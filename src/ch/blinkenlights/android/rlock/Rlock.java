/*
 * This file is part of Rlock. - (C) 2011 Adrian Ulrich
 *
 * ApnSwitch is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ApnSwitch is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ApnSwitch. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.blinkenlights.android.rlock;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.widget.RemoteViews;
import android.provider.Settings;
import android.os.Vibrator;


public class Rlock extends AppWidgetProvider {
	static public final String CLICK = "ch.blinkenlights.android.rlock.CLICK";
	
	@Override
	public void onUpdate(Context ctx, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		updateWidget(ctx);
	}
	@Override
	public void onEnabled(Context ctx) {
		// rxo.attachObserver(ctx);
		attachObserver(ctx);
	}
	@Override
	public void onDisabled(Context ctx) {
		// detachObserver();
		detachObserver();
	}
	
	@Override
	public void onReceive(Context ctx, Intent intent) {
		final String action = intent.getAction();

		// should create observer
		// attachObserver(ctx);
		if(action.equals(Intent.ACTION_TIME_TICK)) {
		}
		else if(action.equals(CLICK)) {
			log("onReceive: Widget was clicked -> changing state");
			toggleRotateStatus(ctx);
			updateWidget(ctx); /* fixme: remove if we have an observer */
		}
		else {
			log("onReceive: calling super due to other_event: "+action);
			super.onReceive(ctx,intent);
		}
	}
	
	class RlockObserver extends ContentObserver {
		public RlockObserver() {
			super(null);
		}
		@Override public void onChange(boolean arg) {
			super.onChange(arg);
			Rlock.log("++ updating widget due to config change ++");
		}
	}
	
	
	private void attachObserver(Context ctx) {
	/*	if(observer == null) {
			observer = new RlockObserver();
			obctx    = ctx;
			ctx.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), false, observer);
			log("--> attached observer: <"+observer+">  // <"+ctx+">");
		}*/
		updateWidget(ctx);
	}
	private void detachObserver() {
/*		if(observer != null) {
			log("--> detaching observer "+observer);
			obctx.getContentResolver().unregisterContentObserver(observer);
			observer = null;
			obctx    = null;
		}
*/
	}
	
	
	public static void log(String lmsg) {
		android.util.Log.d("Rlock INFO: ", lmsg);
	}
	
	/*
	 * Refresh widget
	*/
	public void updateWidget(Context ctx) {
		RemoteViews      rview = new RemoteViews(ctx.getPackageName(), R.layout.widget);
		ComponentName    cname = new ComponentName(ctx, Rlock.class);
		AppWidgetManager amgr  = AppWidgetManager.getInstance(ctx);
		rview.setImageViewResource(R.id.Icon, rotateIsEnabled(ctx) ? R.drawable.rotate_on : R.drawable.rotate_off);
		makeClickable(ctx,rview);
		amgr.updateAppWidget(cname,rview);
		log("--> widget refreshed <--");
	}
	
	private void makeClickable(Context ctx, RemoteViews rview) {
		Intent xint = new Intent(CLICK);
		PendingIntent pint = PendingIntent.getBroadcast(ctx, 0, xint, 0);
		rview.setOnClickPendingIntent(R.id.WidgetLayout, pint);
	}
	
	/*
	 * Returns current RotateStatus: true = autorotate enabled
	*/
	private boolean rotateIsEnabled(Context ctx) {
		boolean state = Settings.System.getInt(ctx.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) != 0;
		return state;
	}
	
	/*
	 * Toggle Autorotate on/off
	*/
	private void toggleRotateStatus(Context ctx) {
		setRotateStatus(ctx, (rotateIsEnabled(ctx) ? false : true));
	}
	
	/*
	 * Enables Auto-Rotation if 'on' is TRUE
	*/
	private void setRotateStatus(Context ctx, boolean on) {
		Settings.System.putInt(ctx.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, on ? 1 : 0);
		log("Rotate status is now: " + (on ? "on" : "off"));
		
		((Vibrator)ctx.getSystemService(ctx.VIBRATOR_SERVICE)).vibrate(40);
		
	}
}

