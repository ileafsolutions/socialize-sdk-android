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
package com.socialize.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.nexercise.client.android.NexerciseApplication;
import com.nexercise.client.android.R;
import com.nexercise.client.android.entities.Friend;
import com.nexercise.client.android.utils.FriendDialogUtils;
import com.nexercise.client.android.utils.FriendDialogUtils.FriendCallbacks;
import com.socialize.Socialize;
import com.socialize.UserUtils;
import com.socialize.error.SocializeException;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.dialog.DialogRegister;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Jason Polites
 *
 */
public abstract class SocializeUIActivity extends Activity implements DialogRegister {
	
	public static final int PROFILE_UPDATE = 1347;
	
	/**Code for Nexercise project Starts*/
    private static final int DIALOG_ADD_FRIEND= 0;
    private static final int DIALOG_REMOVE_FRIEND= 1;
	
	private Set<Dialog> dialogs;
	
    private MenuItem menuAddFriend;
    private MenuItem menuRemoveFriend;
    private Boolean isFriend;
    
    /**Code for Nexercise project Ends*/
	
	@Override
	public final void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			dialogs = new LinkedHashSet<Dialog>();
			onCreateSafe(savedInstanceState);
			new GetIsFriendTask().execute(); // Code for Nexercise project

		}
		catch (Throwable e) {
			SocializeLogger.e("", e);
			finish();
		}
	}
	
	protected void onNewIntent(Intent intent) {
		try {
			super.onNewIntent(intent);
			onNewIntentSafe(intent);
		}
		catch (Throwable e) {
			SocializeLogger.e("", e);
			finish();
		}
	}
	
	@Override
	public void register(Dialog dialog) {
		dialogs.add(dialog);
	}
	
	@Override
	public Collection<Dialog> getDialogs() {
		return dialogs;
	}

	@Override
	protected void onDestroy() {
		if(dialogs != null) {
			for (Dialog dialog : dialogs) {
				dialog.dismiss();
			}
			dialogs.clear();
		}
		super.onDestroy();
	}

	protected void onNewIntentSafe(Intent intent) {}
	
	protected abstract void onCreateSafe(Bundle savedInstanceState);
/**********************Code for Nexercise project Starts***************************/
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
        menuAddFriend= menu.add("Add friend");
        menuAddFriend.setIcon(R.drawable.icon_add_friend);
        
        menuRemoveFriend= menu.add("Remove friend");
        menuRemoveFriend.setIcon(R.drawable.icon_remove_friend);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean result= super.onPrepareOptionsMenu(menu);
        if(menuAddFriend == null){
        	menuAddFriend= menu.add("Add friend");
        	menuAddFriend.setIcon(R.drawable.icon_add_friend);
    	}
        if(menuRemoveFriend == null){
        	menuRemoveFriend= menu.add("Remove friend");
        	menuRemoveFriend.setIcon(R.drawable.icon_remove_friend);
        }
        updateMenu();
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == menuAddFriend) {
            showDialog(DIALOG_ADD_FRIEND);
            return true;
        } else if (item == menuRemoveFriend) {
            showDialog(DIALOG_REMOVE_FRIEND);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
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
			    if (menuAddFriend != null) {
			        menuAddFriend.setVisible(false);
			    }
			    if (menuRemoveFriend != null) {
			        menuRemoveFriend.setVisible(false);
			    }
			} else {
			    if (menuAddFriend != null) {
			        menuAddFriend.setVisible(isFriend != null && !isFriend);
			    }
			    if (menuRemoveFriend != null) {
			        menuRemoveFriend.setVisible(isFriend != null && isFriend);
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
    
    /**Code for Nexercise project Ends*/
}
