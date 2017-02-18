package com.example.floginsample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.floginsample.apiTool.ApiTool;
import com.example.floginsample.app.Config;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends Activity {

    private Button btnFbLogin;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFbLogin = (Button) findViewById(R.id.btnFbLogin);

        FacebookSdk.sdkInitialize(MainActivity.this);
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        Config.LOGD("onSuccess");

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        try {
                                            Config.LOGD("onCompleted");
                                            String email = object.getString("email");
                                            String token = loginResult.getAccessToken().getToken();
                                            String id = object.getString("id");

                                            Config.LOGD("email: " + email);
                                            Config.LOGD("token: " + token);
                                            Config.LOGD("id: " + id);
                                            Config.LOGD("response.toString(): " + response.toString());

                                            ApiTool.saveValue(ApiTool.TOKEN, token);//token存到SP
                                            ApiTool.saveValue(ApiTool.EMAIL, email);//email存到SP
                                            ApiTool.saveValue(ApiTool.ID, id);//id存到SP

                                            startActivity(new Intent(MainActivity.this, SecondActivity.class));

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,birthday");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(MainActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


        btnFbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Config.LOGD("on click");
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "user_friends", "email"));
            }
        });

        //如果已登入過,就直接進到SecondActivity
        if (ApiTool.loadVaule(ApiTool.TOKEN) != null) {
            startActivity(new Intent(MainActivity.this, SecondActivity.class));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
