/*
 * Copyright (c) [2012] - [2017] Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.security.oauth;

import static com.google.common.base.Strings.isNullOrEmpty;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.eclipse.che.api.auth.shared.dto.OAuthToken;
import org.eclipse.che.commons.annotation.Nullable;
import org.eclipse.che.commons.json.JsonHelper;
import org.eclipse.che.commons.json.JsonParseException;
import org.eclipse.che.commons.lang.IoUtil;
import org.eclipse.che.security.oauth.shared.User;
import org.everrest.core.impl.provider.json.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** OAuth authentication for google account. */
@Singleton
public class GoogleOAuthAuthenticator extends OAuthAuthenticator {

  private static final Logger LOG = LoggerFactory.getLogger(GoogleOAuthAuthenticator.class);

  @Inject
  public GoogleOAuthAuthenticator(
      @Nullable @Named("oauth.google.clientid") String clientId,
      @Nullable @Named("oauth.google.clientsecret") String clientSecret,
      @Nullable @Named("oauth.google.redirecturis") String[] redirectUris,
      @Nullable @Named("oauth.google.authuri") String authUri,
      @Nullable @Named("oauth.google.tokenuri") String tokenUri)
      throws IOException {
    if (!isNullOrEmpty(clientId)
        && !isNullOrEmpty(clientSecret)
        && redirectUris != null
        && redirectUris.length != 0) {

      configure(
          new GoogleAuthorizationCodeFlow.Builder(
                  new NetHttpTransport(),
                  new JacksonFactory(),
                  new GoogleClientSecrets()
                      .setWeb(
                          new GoogleClientSecrets.Details()
                              .setClientId(clientId)
                              .setClientSecret(clientSecret)
                              .setRedirectUris(Arrays.asList(redirectUris))
                              .setAuthUri(authUri)
                              .setTokenUri(tokenUri)),
                  Arrays.asList(
                      "https://www.googleapis.com/auth/userinfo.email",
                      "https://www.googleapis.com/auth/userinfo.profile"))
              .setDataStoreFactory(new MemoryDataStoreFactory())
              .setApprovalPrompt("auto")
              .setAccessType("online")
              .build(),
          Arrays.asList(redirectUris));
    }
  }

  @Override
  public User getUser(OAuthToken accessToken) throws OAuthAuthenticationException {
    return getJson(
        "https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken.getToken(),
        GoogleUser.class);
  }

  @Override
  public final String getOAuthProvider() {
    return "google";
  }

  @Override
  public OAuthToken getToken(String userId) throws IOException {
    final OAuthToken token = super.getToken(userId);
    if (!(token == null || token.getToken() == null || token.getToken().isEmpty())) {
      // Need to check if token which stored is valid for requests, then if it is valid - we return
      // it to caller
      URL tokenInfoUrl =
          new URL(
              "https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=" + token.getToken());
      try {
        JsonValue jsonValue = doRequest(tokenInfoUrl);
        if (jsonValue == null) {
          return null;
        }
        JsonValue scope = jsonValue.getElement("scope");
        if (scope != null) token.setScope(scope.getStringValue());
      } catch (JsonParseException e) {
        LOG.error(e.getLocalizedMessage(), e);
      }
      return token;
    }
    return null;
  }

  private JsonValue doRequest(URL tokenInfoUrl) throws IOException, JsonParseException {
    HttpURLConnection http = null;
    try {
      http = (HttpURLConnection) tokenInfoUrl.openConnection();
      int responseCode = http.getResponseCode();
      if (responseCode != HttpURLConnection.HTTP_OK) {
        LOG.warn(
            "Can not receive google token by path: {}. Response status: {}. Error message: {}",
            tokenInfoUrl.toString(),
            responseCode,
            IoUtil.readStream(http.getErrorStream()));
        return null;
      }

      JsonValue result;
      try (InputStream input = http.getInputStream()) {
        result = JsonHelper.parseJson(input);
      }
      return result;
    } finally {
      if (http != null) {
        http.disconnect();
      }
    }
  }
}
