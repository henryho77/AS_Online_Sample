package com.example.floginsample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.floginsample.apiTool.ApiTool;
import com.example.floginsample.apiTool.apiModel.TaggableFriend;
import com.example.floginsample.apiTool.apiModel.TaggableFriendsResult;
import com.example.floginsample.app.Config;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SecondActivity extends Activity {

    private int exitAppFlagCount = 0;
    private Button btnFbLogout;
    private Button btnGetFriends;
    private ListView listView;
    private ArrayList<String> friendList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        btnFbLogout = (Button) findViewById(R.id.btnFbLogout);
        btnGetFriends = (Button) findViewById(R.id.btnGetFriends);
        listView = (ListView) findViewById(R.id.list);

        btnFbLogout.setOnClickListener(onClickListener);
        btnGetFriends.setOnClickListener(onClickListener);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (exitAppFlagCount == 0) {
            Config.TOAST(SecondActivity.this, "再按一次返回鍵退出");
            exitAppFlagCount ++;
        } else if (exitAppFlagCount == 1) {
            finishAffinity();
            //完全退出app就用killProcess
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnFbLogout:
                    LoginManager.getInstance().logOut();
                    ApiTool.clearValue();
                    finish();
                    break;
                case R.id.btnGetFriends:
//                    String userId = ApiTool.loadVaule(ApiTool.ID) + "/friends";
                    String userId = ApiTool.loadVaule(ApiTool.ID) + "/taggable_friends";

                    GraphRequest request = new GraphRequest(
                            AccessToken.getCurrentAccessToken(),
                            userId,
                            null,
                            HttpMethod.GET,
                            new GraphRequest.Callback() {
                                public void onCompleted(GraphResponse response) {
                                    /* handle the result */
                                    Config.LOGD("response.toString(): " + response.toString());//{Response:  responseCode: 200, graphObject: {"data":[{"id":"AaLcpf9gmyE...
                                    Config.LOGD("response.getJSONObject().toString(): " + response.getJSONObject().toString());//{"data":[{"id":"AaLcpf9gmyE...


//                                    String strTaggableFriendsResult = null;//將result(型別為JsonObject)轉成字串(內容是UserResult)
//                                    try {
//                                        strTaggableFriendsResult = response.getJSONObject().getJSONArray("data").toString();
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                    Config.LOGD("strTaggableFriendsResult: " + strTaggableFriendsResult);
//                                    Type type = new TypeToken<TaggableFriendsResult>(){}.getType();//建立TaggableFriendsResult的Type
//                                    TaggableFriendsResult mResult =  new Gson().fromJson(strTaggableFriendsResult, type);//將Json格式的字串(內容是TaggableFriendsResult)藉由給定的TaggableFriendsResult的Type來轉回TaggableFriendsResult
//                                    ArrayList<TaggableFriend> friends = mResult.data;
//
//                                    for (TaggableFriend friend : friends) {
//                                        Config.LOGD(friend.name);
//                                    }

                                    try {
                                        Config.LOGD("response.getJSONObject().getJSONArray(\"data\").toString(): " + response.getJSONObject().getJSONArray("data").toString());
                                        JSONArray jsonArrayResponse = response.getJSONObject().getJSONArray("data");

                                        for(int i = 0; i<jsonArrayResponse.length(); i++) {

                                            JSONObject jsonObject = jsonArrayResponse.getJSONObject(i);

                                            String name  =  jsonObject.getString("name");
                                            friendList.add(name);
                                            Config.LOGD(name);
                                        }

                                        listView.setAdapter(new ArrayAdapter<String>(SecondActivity.this, android.R.layout.simple_list_item_1, friendList));

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }
                            });

                    request.executeAsync();
                    break;
            }
        }
    };
}
