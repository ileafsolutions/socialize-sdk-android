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
package com.socialize.ui.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.nexercise.client.android.NexerciseApplication;
import com.nexercise.client.android.R;
import com.nexercise.client.android.adapters.SlideMenuAdapter;
import com.nexercise.client.android.constants.MenuConstants;
import com.nexercise.client.android.entities.Friend;
import com.nexercise.client.android.entities.NXRMenuItem;
import com.nexercise.client.android.helpers.NxrSocializeMenuHelper;
import com.nexercise.client.android.utils.FriendDialogUtils;
import com.nexercise.client.android.utils.FriendDialogUtils.FriendCallbacks;
import com.socialize.CommentUtils;
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.UserUtils;
import com.socialize.api.SocializeSession;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.ui.SocializeUIActivity;

/**
 * @author Jason Polites
 */
public class ActionDetailActivity extends SocializeUIActivity {

	private ActionDetailView view;
	/**Code for Nexercise project Starts*/	
	NxrSocializeMenuHelper mMenuHelper;
	SlideMenuAdapter mCustomMenuAdapter;
	ListView mListViewSlideMenu;
	List<NXRMenuItem> mCustomMenuList;	
    private static final int DIALOG_ADD_FRIEND= 0;
    private static final int DIALOG_REMOVE_FRIEND= 1;

    private Boolean isFriend;
    
    /**Code for Nexercise project Ends*/
	@Override
	protected void onCreateSafe(Bundle savedInstanceState) {
		Intent intent = getIntent();
		doActivityLoad(intent);
		new GetIsFriendTask().execute(); //Nexercise Custom Slide menu
	}

	@Override
	protected void onNewIntentSafe(Intent intent) {
		Bundle extras = intent.getExtras();
		if(extras != null && view != null) {
			view.reload(extras.getString(Socialize.USER_ID), extras.getString(Socialize.ACTION_ID));
		}
	}

	protected void doActivityLoad(Intent intent) {

		SocializeSession session = getSocialize().getSession();

		if(session == null) {
			finish();
		}
		else {
			User user = session.getUser();

			if(user == null) {
				finish();
			}
			else {
				view = new ActionDetailView(this);
				setContentView(view);
				 /**Code for Nexercise project Starts*/
				initSlideMenu();
				 /**Code for Nexercise project Ends*/
			}
		}	
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == SocializeUIActivity.PROFILE_UPDATE) {
			// Profile has updated... need to reload the view
			view.onProfileUpdate();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			// If we were launched directly, re-launch the main app
			if(isTaskRoot() && view != null) {
				SocializeAction currentAction = view.getCurrentAction();
				if(currentAction != null) {
					CommentUtils.showCommentView(this, currentAction.getEntity());
					finish();
					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	/** Nexercise Custom Slide menu changes starts */
/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(view != null) {
			return view.onCreateOptionsMenu(this, menu);
		}
		return false;
	}*/
	/** Nexercise Custom Slide menu changes ends */
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
	
	/**Nexercise Custom Slide menu changes starts */
	public void initSlideMenu() {
		mMenuHelper = new NxrSocializeMenuHelper(this);
		mCustomMenuList = mMenuHelper.getMenuList();
		if(isFriend != null && !isFriend){
			mCustomMenuList.add(new NXRMenuItem(R.id.custom_menu_add_friend,
					MenuConstants.MENU_ADD_FRIEND, 1,
					R.drawable.icon_custom_add_friend,
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
				            showDialog(DIALOG_ADD_FRIEND);
							if (mMenuHelper.getSlidingMenu().isMenuShowing()) {
								mMenuHelper.getSlidingMenu().showContent();
							}
						}
					}));
		}
		if(isFriend != null && isFriend){
			mCustomMenuList.add(new NXRMenuItem(R.id.custom_menu_remove_friend,
					MenuConstants.MENU_REMOVE_FRIEND, 1,
					R.drawable.icon_custom_remove_friend,
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							showDialog(DIALOG_REMOVE_FRIEND);
							if (mMenuHelper.getSlidingMenu().isMenuShowing()) {
								mMenuHelper.getSlidingMenu().showContent();
							}
						}
					}));
		}
		mCustomMenuAdapter = new SlideMenuAdapter(this, mCustomMenuList,true);
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
			view.setSlidingMenuHelper(mMenuHelper);
		}
	}
	

	/**Nexercise  Custom Slide menu changes ends */
	
/**********************Code for Nexercise project Starts***************************/
	

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
		if (mMenuHelper.getSlidingMenu() != null) {
			mMenuHelper.getSlidingMenu().toggle();
		}
        updateMenu();
        return false;
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        String socializeId= getIntent().getStringExtra(Socialize.USER_ID);
        switch (id) {
            case DIALOG_ADD_FRIEND:
                return FriendDialogUtils.getAddFriendDialog(this, socializeId, new SocializeFriendCallbacks() {
                    public void onPostExecute(Boolean result) {
                        isFriend= result;
                        updateMenu();
                    }
                });
            case DIALOG_REMOVE_FRIEND:
                return FriendDialogUtils.getRemoveFriendDialog(this, getFriend(socializeId).userID, new SocializeFriendCallbacks() {
                    public void onPostExecute(Boolean result) {
                        isFriend= !result;
                        updateMenu();
                    }
                });
            default:
                return super.onCreateDialog(id);
        }
    }

    private Friend getFriend(String socializeId) {
        if (socializeId != null) {
            ArrayList<Friend> list= ((NexerciseApplication)
                    getApplication()).getDataLayerInstance().getExerciseFriendsList();
            for (Friend friend : list) {
                if (friend != null && socializeId.equals(friend.socializeID)) {
                    return friend;
                }
            }
        }
        return null;
    }

    private void updateMenu() {
    		String UserId = null;
    		try {
				UserId = UserUtils.getCurrentUser(this).getId().toString();
			} catch (SocializeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 			if (UserId.equals(getIntent().getStringExtra(Socialize.USER_ID))){
 				mMenuHelper.removefromMenuItem(R.id.custom_menu_add_friend);
 				mMenuHelper.removefromMenuItem(R.id.custom_menu_remove_friend);
			} else {
				if(isFriend != null && !isFriend){
					mMenuHelper.removefromMenuItem(R.id.custom_menu_add_friend);
					mCustomMenuList.add(new NXRMenuItem(R.id.custom_menu_add_friend,
							MenuConstants.MENU_ADD_FRIEND, 1,
							R.drawable.icon_custom_add_friend,
							new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
						            showDialog(DIALOG_ADD_FRIEND);
									if (mMenuHelper.getSlidingMenu().isMenuShowing()) {
										mMenuHelper.getSlidingMenu().showContent();
									}
								}
							}));
				}
				if(isFriend != null && isFriend){
					mMenuHelper.removefromMenuItem(R.id.custom_menu_remove_friend);
					mCustomMenuList.add(new NXRMenuItem(R.id.custom_menu_remove_friend,
							MenuConstants.MENU_REMOVE_FRIEND, 1,
							R.drawable.icon_custom_remove_friend,
							new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									showDialog(DIALOG_REMOVE_FRIEND);
									if (mMenuHelper.getSlidingMenu().isMenuShowing()) {
										mMenuHelper.getSlidingMenu().showContent();
									}
								}
							}));
				}
			}
    }

    private abstract class SocializeFriendCallbacks implements FriendCallbacks {

        @Override
        public void onPreExecute() {
            isFriend= null;
            updateMenu();
        }
    }

    private class GetIsFriendTask extends AsyncTask<Void, Void, Boolean> {

        private String mUserId;

        @Override
        protected final void onPreExecute() {
            super.onPreExecute();
            mUserId= getIntent().getStringExtra(Socialize.USER_ID);
            if (mUserId == null) {
                cancel(true);
            } else {
                isFriend= null;
                updateMenu();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (mUserId != null) {
                ArrayList<Friend> list= ((NexerciseApplication)
                        getApplication()).getDataLayerInstance().getExerciseFriendsList();
                for (Friend friend : list) {
                    if (friend != null && mUserId.equals(friend.socializeID)) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            isFriend= result;
            updateMenu();
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
    /**Code for Nexercise project Ends*/
}
