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
package com.socialize.test.ui.twitter;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthTokenListener;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.http.HttpParameters;
import android.content.Context;
import android.webkit.WebViewClient;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.auth.twitter.OAuthRequestListener;
import com.socialize.auth.twitter.TwitterAuthListener;
import com.socialize.auth.twitter.TwitterAuthWebView;
import com.socialize.auth.twitter.TwitterOAuthProvider;
import com.socialize.auth.twitter.TwitterWebViewClient;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason Polites
 *
 */
public class TwitterAuthWebViewTest extends SocializeUnitTest {

	@UsesMocks ({CommonsHttpOAuthConsumer.class, TwitterOAuthProvider.class, TwitterWebViewClient.class, OAuthRequestListener.class, TwitterAuthListener.class })
	public void testAuthenticate() throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
		
		final String consumerKey = "foo";
		final String consumerSecret = "bar";
		final String requestToken = "foobar_token";
		
		final CommonsHttpOAuthConsumer commonsHttpOAuthConsumer = AndroidMock.createMock(CommonsHttpOAuthConsumer.class, consumerKey, consumerSecret);
		final TwitterOAuthProvider twitterOAuthProvider = AndroidMock.createMock(TwitterOAuthProvider.class);
		final TwitterWebViewClient twitterWebViewClient = AndroidMock.createMock(TwitterWebViewClient.class);
		final TwitterAuthListener listener = AndroidMock.createMock(TwitterAuthListener.class);
		final OAuthRequestListener oAuthRequestListener = AndroidMock.createMock(OAuthRequestListener.class, listener, twitterOAuthProvider, commonsHttpOAuthConsumer);
		
		// Expect
		twitterWebViewClient.setOauthRequestListener(oAuthRequestListener);
		AndroidMock.expect(twitterOAuthProvider.retrieveRequestToken(commonsHttpOAuthConsumer, TwitterOAuthProvider.OAUTH_CALLBACK_URL)).andReturn(requestToken);
		
		AndroidMock.replay(twitterWebViewClient, twitterOAuthProvider);
		
		TwitterAuthWebView webView = new TwitterAuthWebView(getContext()) {

			@Override
			public TwitterWebViewClient newTwitterWebViewClient() {
				return twitterWebViewClient;
			}

			@Override
			public TwitterOAuthProvider newTwitterOAuthProvider() {
				return twitterOAuthProvider;
			}

			@Override
			public CommonsHttpOAuthConsumer newCommonsHttpOAuthConsumer(String consumerKey, String consumerSecret) {
				return commonsHttpOAuthConsumer;
			}

			@Override
			protected OAuthRequestListener newOAuthRequestListener(TwitterAuthListener listener, TwitterOAuthProvider provider, CommonsHttpOAuthConsumer consumer) {
				return oAuthRequestListener;
			}

			@Override
			public void setWebViewClient(WebViewClient client) {
				addResult(0, client);
			}

			@Override
			public void loadUrl(String url) {
				addResult(1, url);
			}
		};
		
		webView.init();
		webView.authenticate(consumerKey, consumerSecret, listener);
		
		AndroidMock.verify(twitterWebViewClient, twitterOAuthProvider);
		
		assertSame(twitterWebViewClient, getResult(0));
		assertEquals(requestToken, getResult(1));
	}
	
	@UsesMocks ({
		CommonsHttpOAuthConsumer.class, 
		TwitterOAuthProvider.class,
		TwitterAuthListener.class,
		OAuthTokenListener.class})
	public void testOAuthRequestListener() throws Exception {
		
		final String consumerKey = "foo";
		final String consumerSecret = "bar";
		final String verifier = "foobar_verifier";
		final String cancelToken = "foobar_cancelToken";
		final String token = "foobar_token";
		
		final CommonsHttpOAuthConsumer consumer = AndroidMock.createMock(CommonsHttpOAuthConsumer.class, consumerKey, consumerSecret);
		final TwitterOAuthProvider provider = AndroidMock.createMock(TwitterOAuthProvider.class);
		final TwitterAuthListener listener = AndroidMock.createMock(TwitterAuthListener.class);
		final OAuthTokenListener tokenListener = AndroidMock.createMock(OAuthTokenListener.class);
		
		
		listener.onCancel();
		provider.retrieveAccessToken(consumer, verifier, tokenListener);
		
		AndroidMock.replay(listener, provider);
		
		PublicTwitterAuthWebView webView = new PublicTwitterAuthWebView(getContext()) {

			@Override
			public OAuthTokenListener newOAuthTokenListener(TwitterAuthListener listener) {
				addResult(0, listener);
				return tokenListener;
			}
		};
		
		OAuthRequestListener oAuthRequestListener = webView.newOAuthRequestListener(listener, provider, consumer);
		
		oAuthRequestListener.onCancel(cancelToken);
		oAuthRequestListener.onRequestToken(token, verifier);
		
		AndroidMock.verify(listener, provider);
		
		assertSame(listener, getResult(0));
	}
	
	@UsesMocks({HttpParameters.class, TwitterAuthListener.class})
	public void testOAuthTokenListener() {
		final TwitterAuthListener listener = AndroidMock.createMock(TwitterAuthListener.class);
		final HttpParameters parameters = AndroidMock.createMock(HttpParameters.class);
		
		String token = "foobar_token";
		String secret = "foobar_secret";
		String screenName = "foobar_screenName";
		String userId = "foobar_userId";
		
		AndroidMock.expect(parameters.getFirst(OAuth.OAUTH_TOKEN)).andReturn(token);
		AndroidMock.expect(parameters.getFirst(OAuth.OAUTH_TOKEN_SECRET)).andReturn(secret);
		AndroidMock.expect(parameters.getFirst("user_id")).andReturn(userId);
		AndroidMock.expect(parameters.getFirst("screen_name")).andReturn(screenName);
		
		listener.onAuthSuccess(token, secret, screenName, userId);
		
		AndroidMock.replay(parameters, listener);
		
		PublicTwitterAuthWebView webView = new PublicTwitterAuthWebView(getContext());
		OAuthTokenListener tokenListener = webView.newOAuthTokenListener(listener);
		
		tokenListener.onResponse(parameters);
		
		AndroidMock.verify(parameters, listener);
		
	}
	
	class PublicTwitterAuthWebView extends TwitterAuthWebView {

		public PublicTwitterAuthWebView(Context context) {
			super(context);
		}

		@Override
		public OAuthRequestListener newOAuthRequestListener(TwitterAuthListener listener, TwitterOAuthProvider provider, CommonsHttpOAuthConsumer consumer) {
			return super.newOAuthRequestListener(listener, provider, consumer);
		}

		@Override
		public OAuthTokenListener newOAuthTokenListener(TwitterAuthListener listener) {
			return super.newOAuthTokenListener(listener);
		}

		@Override
		public TwitterWebViewClient newTwitterWebViewClient() {
			return super.newTwitterWebViewClient();
		}

		@Override
		public TwitterOAuthProvider newTwitterOAuthProvider() {
			return super.newTwitterOAuthProvider();
		}

		@Override
		public CommonsHttpOAuthConsumer newCommonsHttpOAuthConsumer(String consumerKey, String consumerSecret) {
			return super.newCommonsHttpOAuthConsumer(consumerKey, consumerSecret);
		}
	}
	
}