package com.fisnikz.snapdrive.api.users.control;

import com.fisnikz.snapdrive.api.users.entity.SignInWithGoogleResponse;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * @author Fisnik Zejnullahu
 */
@ApplicationScoped
public class GoogleAuthService {

    @Inject
    UsersService usersService;

    @Inject
    @ConfigProperty(name = "gcm.client.id")
    String clientId;

    @Inject
    @ConfigProperty(name = "gcm.client.secret")
    String clientSecret;

    @Inject
    @ConfigProperty(name = "gcm.client.redirect.uri")
    String redirectUri;

    private String accessToken;
    private String refreshToken;
    private String idToken;

    public SignInWithGoogleResponse signInWithGoogle(String authorizationCode) {
        try {
            getTokensWithAuthorizationCode(authorizationCode);
            GoogleIdToken.Payload payload = verifyTokenAndGetPayload(this.idToken);
            return new SignInWithGoogleResponse(payload.getEmail(), payload.getSubject());

        } catch (TokenResponseException ex) {
            throw new WebApplicationException(401);
        } catch (IOException e) {
            e.printStackTrace();
            throw new WebApplicationException(500);
        }

    }

    public void refreshAccessToken() throws IOException {
        TokenResponse response =
                new GoogleRefreshTokenRequest(new NetHttpTransport(), new GsonFactory(),
                        this.refreshToken, this.clientId, this.clientSecret).execute();

        this.accessToken = response.getAccessToken();
    }

    private void getTokensWithAuthorizationCode(String code) throws IOException {
        System.out.println("clientId = " + clientId);
        System.out.println("clientSecret = " + clientSecret);
        System.out.println("redirectUri = " + redirectUri);


        GoogleTokenResponse response =
                new GoogleAuthorizationCodeTokenRequest(new NetHttpTransport(), new GsonFactory(),
                        clientId, clientSecret, code, redirectUri)
                        .execute();

        this.accessToken = response.getAccessToken();
        this.refreshToken = response.getRefreshToken();
        this.idToken = response.getIdToken();
        System.out.println("Access token: " + response.getAccessToken());
        System.out.println("ID token: " + response.getIdToken());
        System.out.println("Refresh token: " + response.getRefreshToken());
    }

    private GoogleIdToken.Payload verifyTokenAndGetPayload(String idTokenString) {
        try {

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new ApacheHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(clientId))
                    .build();
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                return idToken.getPayload();
            }
            return null;
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            throw new WebApplicationException(500);
        }
    }

    public String getAccessToken() {
        return this.accessToken;
    }
}
