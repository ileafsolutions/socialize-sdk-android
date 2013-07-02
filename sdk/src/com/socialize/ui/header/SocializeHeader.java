/*
 * Copyright (c) 2012 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.ui.header;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nexercise.client.android.R;
import com.socialize.i18n.LocalizationService;
import com.socialize.ui.util.Colors;
import com.socialize.util.DisplayUtils;
import com.socialize.util.Drawables;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class SocializeHeader extends LinearLayout {
	
	private TextView titleText;
	private ImageView titleImage;
	private DisplayUtils displayUtils;
	private Drawables drawables;
	private Colors colors;
	private LocalizationService localizationService;
	private String headerTextKey;
	private String headerText;
	/**Code for Nexercise project Starts*/
	private Activity mContext;
	SlidingMenu menu;
	
	private ImageView menubarImage;	
	/**Code for Nexercise project Ends*/
	public void setHeaderText(String headerText) {
		this.headerText = headerText;
	}

	public void setDisplayUtils(DisplayUtils deviceUtils) {
		this.displayUtils = deviceUtils;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}

	public SocializeHeader(Context context) {
		super(context);
		mContext = (Activity) context; //Code for Nexercise project

	}

	public TextView getTitleText() {
		return titleText;
	}

	public void setTitleText(TextView titleText) {
		this.titleText = titleText;
	}
	
	public void setText(String text) {
		this.titleText.setText(text);
	}
	
	public ImageView getTitleImage() {
		return titleImage;
	}

	public void setTitleImage(ImageView titleImage) {
		this.titleImage = titleImage;
	}
	/**Code for Nexercise project Starts*/
	public void displayMenu() {
		this.menubarImage.setVisibility(View.VISIBLE);
	}
	public void hideMenu() {
		this.menubarImage.setVisibility(View.GONE);
	}
	
	public void displayTitleImage() {
		this.titleImage.setVisibility(View.VISIBLE);
	}
	public void hideTitleImage() {
		this.titleImage.setVisibility(View.GONE);
	}
	/**Code for Nexercise project Ends*/
	public void init() {
		int four = displayUtils.getDIP(4);
		int eight = displayUtils.getDIP(8);
		int height = displayUtils.getDIP(59); // Code added for Nexercise project
		
		LayoutParams titlePanelLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, height);
		titlePanelLayoutParams.gravity = Gravity.CENTER_VERTICAL;
		this.setLayoutParams(titlePanelLayoutParams);
		this.setOrientation(LinearLayout.HORIZONTAL);
		this.setPadding(four, four, four, four);
		
		
		ColorDrawable background = new ColorDrawable(Color.BLACK);
		
		Drawable headerBG = drawables.getDrawable("header.png", true, false, true);
		
		Drawable[] layers = new Drawable[] {background, headerBG};
		
		LayerDrawable bg = newLayerDrawable(layers);
		bg.setLayerInset(1, 0, 0, 0, 1);
		
		this.setBackgroundDrawable(bg);
		
		titleText = new TextView(getContext());
		titleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		titleText.setTextColor(colors.getColor(Colors.HEADER));
		
		if(!StringUtils.isEmpty(headerTextKey)) {
			titleText.setText(localizationService.getString(headerTextKey));
		}
		else {
			titleText.setText(getHeaderText());
		}
		
		titleText.setPadding(0, 0, 0, displayUtils.getDIP(2));
		titleText.setSingleLine(true);
		titleText.setEllipsize(TruncateAt.END);

		LayoutParams titleTextLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		titleTextLayoutParams.gravity = Gravity.CENTER_VERTICAL;
		titleText.setLayoutParams(titleTextLayoutParams);

		titleImage = new ImageView(getContext());
		titleImage.setImageDrawable(drawables.getDrawable("socialize_icon_white.png"));
		titleImage.setPadding(0, 0, 0, 0);

		/**Code for Nexercise project Starts*/
		menubarImage = new ImageView(getContext());
		menubarImage.setBackgroundResource(R.drawable.topbar_socialize_menu_btn_states);
		menubarImage.setPadding(0, 0, 0, 0);

		LayoutParams menubarImageLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		menubarImageLayoutParams.gravity = Gravity.CENTER_VERTICAL;
		menubarImageLayoutParams.setMargins(four, 0, four, 0);
		menubarImage.setLayoutParams(menubarImageLayoutParams);
		menubarImage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (getSlidingMenu() != null) {
					menu.showMenu();
				}
			}
		});
		

		LayoutParams titleImageLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		titleImageLayoutParams.gravity = Gravity.CENTER_VERTICAL;
		titleImageLayoutParams.setMargins(four, 0, four, 0);
		titleImage.setLayoutParams(titleImageLayoutParams);
		titleImage.setVisibility(View.GONE);

		this.addView(menubarImage);
		this.addView(titleImage);
		this.addView(titleText);
		
		/**Code for Nexercise project Ends*/
	}
	
	// So we can mock
	protected LayerDrawable newLayerDrawable(Drawable[] layers) {
		return new LayerDrawable(layers);
	}

	public String getHeaderText() {
		return headerText;
	}

	public void setHeaderTextKey(String headerTextKey) {
		this.headerTextKey = headerTextKey;
	}

	public void setLocalizationService(LocalizationService localizationService) {
		this.localizationService = localizationService;
	}
	
	/**Code for Nexercise project Starts*/
	public void setSlidingMenu(SlidingMenu slidingMenu) {
		this.menu = slidingMenu;
	}

	public SlidingMenu getSlidingMenu() {
		return this.menu;
	}
	/**Code for Nexercise project Ends*/
}
