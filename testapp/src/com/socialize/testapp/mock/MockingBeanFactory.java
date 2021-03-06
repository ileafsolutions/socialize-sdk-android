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
package com.socialize.testapp.mock;

import com.socialize.android.ioc.BeanCreationListener;
import com.socialize.android.ioc.IBeanFactory;

/**
 * Allows us to mock the internals of a bean factory.
 * @author Jason Polites
 */
public class MockingBeanFactory<T> implements IBeanFactory<T> {

	private T bean;

	public void setBean(T bean) {
		this.bean = bean;
	}

	@Override
	public T getBean() {
		return bean;
	}

	@Override
	public T getBean(Object... args) {
		return bean;
	}

	@Override
	public void getBeanAsync(BeanCreationListener<T> listener) {
		listener.onCreate(bean);
	}

	@Override
	public void getBeanAsync(BeanCreationListener<T> listener, Object... args) {
		listener.onCreate(bean);
	}
}
