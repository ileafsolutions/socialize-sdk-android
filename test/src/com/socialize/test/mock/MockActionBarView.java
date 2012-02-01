/*
 * Copyright (c) 2011 Socialize Inc.
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
package com.socialize.test.mock;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.slider.ActionBarSliderView;

/**
 * @author Jason Polites
 *
 */
public class MockActionBarView extends ActionBarView {

	public MockActionBarView(Context context) {
		super(context);
	}

	@Override
	public View getView(Bundle bundle, Object... entityKeys) {
		return super.getView(bundle, entityKeys);
	}

	@Override
	public void setListeners() {
		super.setListeners();
	}

	@Override
	public ActionBarSliderView getSlider() {
		return super.getSlider();
	}

	@Override
	public String[] getBundleKeys() {
		return super.getBundleKeys();
	}

	@Override
	public View getEditModeView() {
		return super.getEditModeView();
	}
}