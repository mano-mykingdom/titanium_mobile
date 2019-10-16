/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2012-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package org.appcelerator.titanium.proxy;

import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiFileHelper;
import org.appcelerator.titanium.util.TiUrl;
import org.appcelerator.titanium.util.TypefaceSpan;

import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.graphics.drawable.ColorDrawable;

import java.util.HashMap;

@SuppressWarnings("deprecation")
@Kroll.proxy(propertyAccessors = { TiC.PROPERTY_ON_HOME_ICON_ITEM_SELECTED })
public class ActionBarProxy extends KrollProxy
{
	private static final int MSG_FIRST_ID = KrollProxy.MSG_LAST_ID + 1;
	private static final int MSG_DISPLAY_HOME_AS_UP = MSG_FIRST_ID + 100;
	private static final int MSG_SET_BACKGROUND_IMAGE = MSG_FIRST_ID + 101;
	private static final int MSG_SET_TITLE = MSG_FIRST_ID + 102;
	private static final int MSG_SHOW = MSG_FIRST_ID + 103;
	private static final int MSG_HIDE = MSG_FIRST_ID + 104;
	private static final int MSG_SET_LOGO = MSG_FIRST_ID + 105;
	private static final int MSG_SET_ICON = MSG_FIRST_ID + 106;
	private static final int MSG_SET_HOME_BUTTON_ENABLED = MSG_FIRST_ID + 107;
	private static final int MSG_SET_NAVIGATION_MODE = MSG_FIRST_ID + 108;
	private static final int MSG_SET_SUBTITLE = MSG_FIRST_ID + 109;
	private static final int MSG_SET_DISPLAY_SHOW_HOME = MSG_FIRST_ID + 110;
	private static final int MSG_SET_DISPLAY_SHOW_TITLE = MSG_FIRST_ID + 111;
	private static final int MSG_SET_BACKGROUND_COLOR = MSG_FIRST_ID + 112;
	private static final int MSG_SET_TITLE_ATTRIBUTES = MSG_FIRST_ID + 113;
	private static final int MSG_SET_SUBTITLE_ATTRIBUTES = MSG_FIRST_ID + 114;
	private static final int MSG_SET_CUSTOM_VIEW = MSG_FIRST_ID + 115;
	private static final int MSG_SET_DISPLAY_SHOW_CUSTOM = MSG_FIRST_ID + 116;
	private static final String SHOW_HOME_AS_UP = "showHomeAsUp";
	private static final String HOME_BUTTON_ENABLED = "homeButtonEnabled";
	private static final String BACKGROUND_IMAGE = "backgroundImage";
	private static final String TITLE = "title";
	private static final String LOGO = "logo";
	private static final String ICON = "icon";
	private static final String NAVIGATION_MODE = "navigationMode";
	private static final String BACKGROUND_COLOR = "backgroundColor";
	private static final String SUBTITLE_ATTRIBUTES = "subtitleAttributes";
	private static final String TAG = "ActionBarProxy";

	private ActionBar actionBar;
	private TiViewProxy customView;
	private boolean showTitleEnabled = true;

	public ActionBarProxy(AppCompatActivity activity)
	{
		super();
		actionBar = activity.getSupportActionBar();
		// Guard against calls to ActionBar made before inflating the ActionBarView
		if (actionBar != null) {
			actionBar.setDisplayOptions(ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_HOME
										| ActionBar.DISPLAY_SHOW_TITLE);
		} else {
			Log.w(TAG, "Trying to get a reference to ActionBar before its container was inflated.");
		}
	}

	// clang-format off
	@Kroll.method
	@Kroll.setProperty
	public void setDisplayHomeAsUp(boolean showHomeAsUp)
	// clang-format on
	{
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(showHomeAsUp);
		} else {
			Log.w(TAG, "ActionBar is not enabled");
		}
	}

	// clang-format off
	@Kroll.method
	@Kroll.setProperty
	public void setHomeButtonEnabled(boolean homeButtonEnabled)
	// clang-format on
	{
		if (actionBar != null) {
			actionBar.setHomeButtonEnabled(homeButtonEnabled);
		} else {
			Log.w(TAG, "ActionBar is not enabled");
		}
	}

	// clang-format off
	@Kroll.method
	@Kroll.setProperty
	public void setNavigationMode(int navigationMode)
	// clang-format on
	{
		actionBar.setNavigationMode(navigationMode);
	}

	// clang-format off
	@Kroll.method
	@Kroll.setProperty
	public void setBackgroundImage(String url)
	// clang-format on
	{
		if (actionBar == null) {
			Log.w(TAG, "ActionBar is not enabled");
			return;
		}

		Drawable backgroundImage = getDrawableFromUrl(url);
		//This is a workaround due to https://code.google.com/p/styled-action-bar/issues/detail?id=3. [TIMOB-12148]
		if (backgroundImage != null) {
			actionBar.setDisplayShowTitleEnabled(!showTitleEnabled);
			actionBar.setDisplayShowTitleEnabled(showTitleEnabled);
			actionBar.setBackgroundDrawable(backgroundImage);
		}
	}

	@Kroll
		.method
		@Kroll.setProperty
		public void setBackgroundColor(String bgColor)
	{
		if (TiApplication.isUIThread()) {
			handleSetBackgroundColor(bgColor);
		} else {
			Message message = getMainHandler().obtainMessage(MSG_SET_BACKGROUND_COLOR, bgColor);
			message.getData().putString(BACKGROUND_COLOR, bgColor);
			message.sendToTarget();
		}
	}

	// clang-format off
	@Kroll.method
	@Kroll.setProperty
	public void setTitle(String title)
	// clang-format on
	{
		if (actionBar != null) {
			actionBar.setTitle(title);
		} else {
			Log.w(TAG, "ActionBar is not enabled");
		}
	}

	// clang-format off
	@Kroll.method
	@Kroll.setProperty
	public void setSubtitle(String subTitle)
	// clang-format on
	{
		if (actionBar != null) {
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setSubtitle(subTitle);
		} else {
			Log.w(TAG, "ActionBar is not enabled");
		}
	}

	@Kroll
		.method
		@Kroll.setProperty
		public void setTitleProperties(HashMap d)
	{
		if (TiApplication.isUIThread()) {
			handleSetTitleAttributes(d);
		} else {
			Message message = getMainHandler().obtainMessage(MSG_SET_TITLE_ATTRIBUTES, d);
			message.getData().putSerializable(TiC.PROPERTY_TITLE_ATTRIBUTES, d);
			message.sendToTarget();
		}
	}

	@Kroll
		.method
		@Kroll.setProperty
		public void setSubtitleProperties(HashMap d)
	{
		if (TiApplication.isUIThread()) {
			handleSetSubtitleAttributes(d);
		} else {
			Message message = getMainHandler().obtainMessage(MSG_SET_SUBTITLE_ATTRIBUTES, d);
			message.getData().putSerializable(SUBTITLE_ATTRIBUTES, d);
			message.sendToTarget();
		}
	}

	@Kroll
		.method
		@Kroll.setProperty
		public void setCustomView(TiViewProxy viewProxy)
	{
		if (viewProxy == null) {
			Log.w(TAG, "Invalid value for customView");
			return;
		}

		customView = viewProxy;

		if (TiApplication.isUIThread()) {
			handleSetCustomView();
		} else {
			Message message = getMainHandler().obtainMessage(MSG_SET_CUSTOM_VIEW);
			message.sendToTarget();
		}
	}

	@Kroll
		.method
		@Kroll.getProperty
		public TiViewProxy getCustomView()
	{
		return customView;
	}

	@Kroll.method
	public void setDisplayShowCustomEnabled(boolean show)
	{
		if (actionBar == null) {
			return;
		}

		if (TiApplication.isUIThread()) {
			actionBar.setDisplayShowCustomEnabled(show);
		} else {
			Message message = getMainHandler().obtainMessage(MSG_SET_DISPLAY_SHOW_CUSTOM, show);
			message.sendToTarget();
		}
	}

	@Kroll.method
	public void setDisplayShowHomeEnabled(boolean show)
	{
		if (actionBar == null) {
			return;
		}

		if (TiApplication.isUIThread()) {
			actionBar.setDisplayShowHomeEnabled(show);
		}
	}

	@Kroll.method
	public void setDisplayShowTitleEnabled(boolean show)
	{
		if (actionBar != null) {
			actionBar.setDisplayShowTitleEnabled(show);
			showTitleEnabled = show;
		}
	}

	// clang-format off
	@Kroll.method
	@Kroll.getProperty
	public String getSubtitle()
	// clang-format on
	{
		if (actionBar == null) {
			return null;
		}
		return (String) actionBar.getSubtitle();
	}

	// clang-format off
	@Kroll.method
	@Kroll.getProperty
	public String getTitle()
	// clang-format on
	{
		if (actionBar == null) {
			return null;
		}
		return (String) actionBar.getTitle();
	}

	// clang-format off
	@Kroll.method
	@Kroll.getProperty
	public int getNavigationMode()
	// clang-format on
	{
		if (actionBar == null) {
			return 0;
		}
		return (int) actionBar.getNavigationMode();
	}

	@Kroll.method
	public void show()
	{
		if (actionBar != null) {
			actionBar.show();
		} else {
			Log.w(TAG, "ActionBar is not enabled");
		}
	}

	@Kroll.method
	public void hide()
	{
		if (actionBar != null) {
			actionBar.hide();
		} else {
			Log.w(TAG, "ActionBar is not enabled");
		}
	}

	// clang-format off
	@Kroll.method
	@Kroll.setProperty
	public void setLogo(String url)
	// clang-format on
	{
		if (actionBar == null) {
			Log.w(TAG, "ActionBar is not enabled");
			return;
		}

		Drawable backgroundImage = getDrawableFromUrl(url);
		//This is a workaround due to https://code.google.com/p/styled-action-bar/issues/detail?id=3. [TIMOB-12148]
		if (backgroundImage != null) {
			actionBar.setDisplayShowTitleEnabled(!showTitleEnabled);
			actionBar.setDisplayShowTitleEnabled(showTitleEnabled);
			actionBar.setBackgroundDrawable(backgroundImage);
		}
	}

	private void handleSetBackgroundColor(String bgColor)
	{
		if (actionBar == null) {
			Log.w(TAG, "ActionBar is not enabled");
			return;
		}

		//This is a workaround due to https://code.google.com/p/styled-action-bar/issues/detail?id=3. [TIMOB-12148]
		if (bgColor != null) {
			actionBar.setDisplayShowTitleEnabled(!showTitleEnabled);
			actionBar.setDisplayShowTitleEnabled(showTitleEnabled);
			actionBar.setBackgroundDrawable(new ColorDrawable(TiConvert.toColor(bgColor)));
		}
	}

	private void handleSetTitleProperties(HashMap d)
	{
		if (actionBar == null) {
			Log.w(TAG, "ActionBar is not enabled");
			return;
		}

		SpannableStringBuilder ssb;
		String title = "";

		if (d.containsKey(TiC.PROPERTY_TITLE)) {
			title = (String) d.get(TiC.PROPERTY_TITLE);
		} else if (actionBar.getTitle() instanceof String) {
			title = TiConvert.toString(actionBar.getTitle());
		}

		if (actionBar.getTitle() instanceof SpannableStringBuilder) {
			ssb = (SpannableStringBuilder) actionBar.getTitle();
			ssb.clear();
			ssb.append(title);
		} else {
			ssb = new SpannableStringBuilder(title);
		}

		if (d.containsKey(TiC.PROPERTY_COLOR)) {
			ssb.setSpan(new ForegroundColorSpan(TiConvert.toColor((String) d.get(TiC.PROPERTY_COLOR))), 0, ssb.length(),
						Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		}

		if (d.containsKey(TiC.PROPERTY_FONT)) {
			Object font = d.get(TiC.PROPERTY_FONT);
			String fontFamily;
			if (font instanceof HashMap) {
				fontFamily = (String) ((HashMap) font).get(TiC.PROPERTY_FONTFAMILY);
			} else {
				fontFamily = (String) font;
			}
			ssb.setSpan(new TypefaceSpan(TiApplication.getInstance(), fontFamily), 0, ssb.length(),
						Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		}

		actionBar.setTitle(ssb);
	}

	private void handleSetSubtitleProperties(HashMap d)
	{
		if (actionBar == null) {
			Log.w(TAG, "ActionBar is not enabled");
			return;
		}

		TiApplication appContext = TiApplication.getInstance();
		SpannableStringBuilder ssb;
		String subtitle = "";

		if (d.containsKey(TiC.PROPERTY_SUBTITLE)) {
			subtitle = (String) d.get(TiC.PROPERTY_SUBTITLE);
		} else if (actionBar.getSubtitle() instanceof String) {
			subtitle = TiConvert.toString(actionBar.getSubtitle());
		}

		if (actionBar.getSubtitle() instanceof SpannableStringBuilder) {
			ssb = (SpannableStringBuilder) actionBar.getSubtitle();
			ssb.clear();
			ssb.append(subtitle);
		} else {
			ssb = new SpannableStringBuilder(subtitle);
		}

		if (d.containsKey(TiC.PROPERTY_COLOR)) {
			ssb.setSpan(new ForegroundColorSpan(TiConvert.toColor((String) d.get(TiC.PROPERTY_COLOR))), 0, ssb.length(),
						Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		}

		if (d.containsKey(TiC.PROPERTY_FONT)) {
			Object font = d.get(TiC.PROPERTY_FONT);
			String fontFamily;
			if (font instanceof HashMap) {
				fontFamily = (String) ((HashMap) font).get(TiC.PROPERTY_FONTFAMILY);
			} else {
				fontFamily = (String) font;
			}
			ssb.setSpan(new TypefaceSpan(TiApplication.getInstance(), fontFamily), 0, ssb.length(),
						Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		}

		actionBar.setSubtitle(ssb);
	}

	private void handleSetCustomView()
	{

		if (actionBar == null) {
			Log.w(TAG, "ActionBar is not enabled");
			return;
		}

		actionBar.setCustomView(customView.getOrCreateView().getNativeView());
	}

	private void handlesetDisplayHomeAsUp(boolean showHomeAsUp)
	{
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(showHomeAsUp);
		} else {
			Log.w(TAG, "ActionBar is not enabled");
		}
	}

	private void handlesetHomeButtonEnabled(boolean homeButtonEnabled)
	{
		if (actionBar != null) {
			actionBar.setHomeButtonEnabled(homeButtonEnabled);
		} else {
			Log.w(TAG, "ActionBar is not enabled");
		}
	}

	private void handlesetNavigationMode(int navigationMode)
	{
		actionBar.setNavigationMode(navigationMode);
	}

	private void handleSetLogo(String url)
	{
		if (actionBar == null) {
			Log.w(TAG, "ActionBar is not enabled");
			return;
		}

		Drawable logo = getDrawableFromUrl(url);
		if (logo != null) {
			actionBar.setLogo(logo);
		}
	}

	// clang-format off
	@Kroll.method
	@Kroll.setProperty
	public void setIcon(String url)
	// clang-format on
	{
		if (actionBar == null) {
			Log.w(TAG, "ActionBar is not enabled");
			return;
		}

		Drawable icon = getDrawableFromUrl(url);
		if (icon != null) {
			actionBar.setIcon(icon);
		}
	}

	private void handleSetIcon(String url)
	{
		if (actionBar == null) {
			Log.w(TAG, "ActionBar is not enabled");
			return;
		}

		Drawable icon = getDrawableFromUrl(url);
		if (icon != null) {
			actionBar.setIcon(icon);
		}
	}

	private void handleSetTitle(String title)
	{
		if (actionBar != null) {
			actionBar.setTitle(title);
		} else {
			Log.w(TAG, "ActionBar is not enabled");
		}
	}

	private void handleSetSubTitle(String subTitle)
	{
		if (actionBar != null) {
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setSubtitle(subTitle);
		} else {
			Log.w(TAG, "ActionBar is not enabled");
		}
	}

	private void handleShow()
	{
		if (actionBar != null) {
			actionBar.show();
		} else {
			Log.w(TAG, "ActionBar is not enabled");
		}
	}

	private void handleHide()
	{
		if (actionBar != null) {
			actionBar.hide();
		} else {
			Log.w(TAG, "ActionBar is not enabled");
		}
	}

	private void handleSetBackgroundImage(String url)
	{
		if (actionBar == null) {
			Log.w(TAG, "ActionBar is not enabled");
			return;
		}

		Drawable backgroundImage = getDrawableFromUrl(url);
		//This is a workaround due to https://code.google.com/p/styled-action-bar/issues/detail?id=3. [TIMOB-12148]
		if (backgroundImage != null) {
			actionBar.setDisplayShowTitleEnabled(!showTitleEnabled);
			actionBar.setDisplayShowTitleEnabled(showTitleEnabled);
			actionBar.setBackgroundDrawable(backgroundImage);
		}
	}

	private Drawable getDrawableFromUrl(String url)
	{
		TiUrl imageUrl = new TiUrl((String) url);
		TiFileHelper tfh = new TiFileHelper(TiApplication.getInstance());
		return tfh.loadDrawable(imageUrl.resolve(), false);
	}

	@Override
	public boolean handleMessage(Message msg)
	{
		switch (msg.what) {
			case MSG_DISPLAY_HOME_AS_UP:
				handlesetDisplayHomeAsUp(msg.getData().getBoolean(SHOW_HOME_AS_UP));
				return true;
			case MSG_SET_NAVIGATION_MODE:
				handlesetNavigationMode(msg.getData().getInt(NAVIGATION_MODE));
				return true;
			case MSG_SET_BACKGROUND_IMAGE:
				handleSetBackgroundImage(msg.getData().getString(BACKGROUND_IMAGE));
				return true;
			case MSG_SET_BACKGROUND_COLOR:
				handleSetBackgroundColor(msg.getData().getString(BACKGROUND_COLOR));
				return true;
			case MSG_SET_TITLE:
				handleSetTitle(msg.getData().getString(TITLE));
				return true;
			case MSG_SET_SUBTITLE:
				handleSetSubTitle(msg.getData().getString(TiC.PROPERTY_SUBTITLE));
				return true;
			case MSG_SET_TITLE_ATTRIBUTES:
				handleSetTitleAttributes((HashMap) msg.getData().getSerializable(TiC.PROPERTY_TITLE_ATTRIBUTES));
				return true;
			case MSG_SET_SUBTITLE_ATTRIBUTES:
				handleSetSubtitleAttributes((HashMap) msg.getData().getSerializable(SUBTITLE_ATTRIBUTES));
				return true;
			case MSG_SET_CUSTOM_VIEW:
				handleSetCustomView();
				return true;
			case MSG_SET_DISPLAY_SHOW_CUSTOM: {
				boolean show = TiConvert.toBoolean(msg.obj, true);
				if (actionBar != null) {
					actionBar.setDisplayShowCustomEnabled(show);
				}
				return true;
			}
			case MSG_SET_DISPLAY_SHOW_HOME: {
				boolean show = TiConvert.toBoolean(msg.obj, true);
				if (actionBar != null) {
					actionBar.setDisplayShowHomeEnabled(show);
				}
				return true;
			}
			case MSG_SET_DISPLAY_SHOW_TITLE: {
				boolean show = TiConvert.toBoolean(msg.obj, true);
				if (actionBar != null) {
					actionBar.setDisplayShowTitleEnabled(show);
					showTitleEnabled = show;
				}
				return true;
			}
			case MSG_SHOW:
				handleShow();
				return true;
			case MSG_HIDE:
				handleHide();
				return true;
			case MSG_SET_LOGO:
				handleSetLogo(msg.getData().getString(LOGO));
				return true;
			case MSG_SET_ICON:
				handleSetIcon(msg.getData().getString(ICON));
				return true;
			case MSG_SET_HOME_BUTTON_ENABLED:
				handlesetHomeButtonEnabled(msg.getData().getBoolean(HOME_BUTTON_ENABLED));
				return true;
		}
		return super.handleMessage(msg);
	}

	@Override
	public void onPropertyChanged(String name, Object value)
	{
		if (TiC.PROPERTY_ON_HOME_ICON_ITEM_SELECTED.equals(name)) {
			// If we have a listener on the home icon item, then enable the home button
			if (actionBar != null) {
				actionBar.setHomeButtonEnabled(true);
			}
		} else if (TiC.PROPERTY_CUSTOM_VIEW.equals(name)) {
			if (actionBar != null) {
				if (value != null) {
					if (value instanceof TiViewProxy) {
						actionBar.setDisplayShowCustomEnabled(true);
						actionBar.setCustomView(((TiViewProxy) value).getOrCreateView().getNativeView());
					} else {
						Log.w(TAG, "Invalid value passed for a custom view. Expected Ti.UI.View or null");
					}
				} else {
					actionBar.setCustomView(null);
				}
			}
		}
		super.onPropertyChanged(name, value);
	}

	@Override
	public String getApiName()
	{
		return "Ti.Android.ActionBar";
	}
}
