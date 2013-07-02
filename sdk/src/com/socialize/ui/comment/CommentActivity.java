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
package com.socialize.ui.comment;

import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.nexercise.client.android.R;
import com.nexercise.client.android.adapters.SlideMenuAdapter;
import com.nexercise.client.android.constants.MenuConstants;
import com.nexercise.client.android.entities.NXRMenuItem;
import com.nexercise.client.android.helpers.NxrSocializeMenuHelper;
import com.nexercise.client.android.utils.Funcs;
import com.socialize.Socialize;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.SocializeUIActivity;
import com.socialize.ui.slider.ActionBarSliderView;
import com.socialize.ui.slider.ActionBarSliderView.DisplayState;
import com.socialize.util.DefaultAppUtils;

/**
 * @author Jason Polites
 */
public class CommentActivity extends SocializeUIActivity {
	
	private CommentView view;
	/** Nexercise Custom Slide menu changes starts */
	NxrSocializeMenuHelper mMenuHelper;
	SlideMenuAdapter mCustomMenuAdapter;
	ListView mListViewSlideMenu;
	List<NXRMenuItem> mCustomMenuList;
	/** Nexercise Custom Slide menu changes ends */
	
	@Override
	protected void onCreateSafe(Bundle savedInstanceState) {

		Bundle extras = getIntent().getExtras();
		
		if(extras == null || !extras.containsKey(Socialize.ENTITY_OBJECT)) {
			SocializeLogger.w("No entity found for Comment Activity. Aborting");
			finish();
		}
		else {
			Object entity = extras.get(Socialize.ENTITY_OBJECT);
			if(entity == null) {
				SocializeLogger.w("No entity found for Comment Activity. Aborting");
				finish();
			}
			else {
				view = new CommentView(this);
				setContentView(view);
				/** Nexercise Custom Slide menu changes starts */
				initSlideMenu();
				/** Nexercise Custom Slide menu changes ends */
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			if(view != null) {
				ActionBarSliderView slider = view.getCommentEntryViewSlider();
				if(slider != null && slider.getDisplayState().equals(DisplayState.MAXIMIZE)) {
					slider.close();
					return true;
				}
			}
			
			if(isTaskRoot()) {
				if (DefaultAppUtils.launchMainApp(this)) {
					finish();
					return true;
				}
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == PROFILE_UPDATE) {
			// Profile has updated... need to reload the view
			view.onProfileUpdate();
		}
	}
	/** Nexercise Custom Slide menu changes starts */
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(view != null) {
			return view.onCreateOptionsMenu(this, menu);
		}
		return false;
	}*/
	/** Nexercise Custom Slide menu changes Ends */
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if(view != null) {
			view.onCreateContextMenu(this, menu, v, menuInfo);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(view != null) {
			return view.onContextItemSelected(this, item);
		}		
		return super.onContextItemSelected(item);
	}
	/** Nexercise Custom Slide menu changes starts */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
		if (mMenuHelper.getSlidingMenu() != null) {
			mMenuHelper.getSlidingMenu().toggle();
		}
        return false;
    }
	public void initSlideMenu() {
		mMenuHelper = new NxrSocializeMenuHelper(this);
		mCustomMenuList = mMenuHelper.getMenuList();
		mMenuHelper.removefromMenuItem(R.id.custom_menu_refresh);
		mCustomMenuList.add(new NXRMenuItem(R.id.custom_menu_refresh,
				MenuConstants.MENU_REFRESH, 2,
				R.drawable.ic_custom_menu_refresh, new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (mMenuHelper.getSlidingMenu().isMenuShowing()) {
							mMenuHelper.getSlidingMenu().showContent();
						}
						if (Funcs.isInternetReachable(CommentActivity.this)) {
							if(view != null){
								view.reload();
							}
						} else {
							Toast.makeText(getApplicationContext(),
									"No connection available",
									Toast.LENGTH_SHORT).show();
						}
					}
				}));
		mCustomMenuAdapter = new SlideMenuAdapter(this, mCustomMenuList);
		Collections.sort(mCustomMenuList);
		mListViewSlideMenu = (ListView) findViewById(R.id.list_view_menu);
		mListViewSlideMenu.setAdapter(mCustomMenuAdapter);
		mCustomMenuAdapter.notifyDataSetChanged();
		mMenuHelper.getSlidingMenu().setOnOpenListener(new OnOpenListener() {
			
			@Override
			public void onOpen() {
				// TODO Auto-generated method stub
			}
		});
		if(view != null){
			view.setSlidingMenu(mMenuHelper.getSlidingMenu());
		}
	}
	
	@Override
	public void onBackPressed() {
		if (mMenuHelper.getSlidingMenu().isMenuShowing()) {
			mMenuHelper.getSlidingMenu().showContent();
		} else {
			super.onBackPressed();
		}
	}
	/** Nexercise Custom Slide menu changes  ends */
}
