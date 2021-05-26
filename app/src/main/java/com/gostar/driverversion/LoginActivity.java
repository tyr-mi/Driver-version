package com.gostar.driverversion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;


import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private Call<Retro> call;
    private CardView loginBtn;
    /* access modifiers changed from: private */
    public AppCompatEditText passwordEt;
    /* access modifiers changed from: private */
    public Realm realm;
    private GetDataService service;
    /* access modifiers changed from: private */
    public AppCompatEditText usernameEt;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        if (realm.where(UserDbClass.class).findAll().size() > 0) {
            startActivity(new Intent(getApplicationContext(), MainActivityRefactor.class));
        }
        setContentView((int) R.layout.activity_login);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        prepareNetworkReq();
        this.usernameEt = findViewById(R.id.login_username_edit_text);
        this.passwordEt = findViewById(R.id.login_password_edit_text);
        this.loginBtn = findViewById(R.id.login_button_card_view);
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            public void onComplete(Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w("FCM : ", "Fetching FCM registration token failed", task.getException());
                    return;
                }
                String token = task.getResult();
                LoginActivity.this.startService(new Intent(LoginActivity.this, FirebaseService.class));
                Log.d("FCM : ", token);
                Toast.makeText(LoginActivity.this, token, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
        this.loginBtn.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                LoginActivity.this.lambda$onResume$0$LoginActivity(view);
            }
        });
    }

    public /* synthetic */ void lambda$onResume$0$LoginActivity(View v) {
        if (this.usernameEt.getText().toString().trim().equals("")) {
            Toast.makeText(getApplicationContext(), "نام کاربری خالی است.", Toast.LENGTH_LONG).show();
        } else if (!this.passwordEt.getText().toString().trim().equals("")) {
            Call<Retro> checkLogin = this.service.checkLogin(new CheckLogin(1, this.usernameEt.getText().toString().trim(), this.passwordEt.getText().toString().trim()));
            this.call = checkLogin;
            checkLogin.enqueue(new Callback<Retro>() {
                public void onResponse(Call<Retro> call, Response<Retro> response) {
                    Retro responseStr = response.body();
                    if (response == null) {
                        Toast.makeText(LoginActivity.this.getApplicationContext(), "در ورود شما مشکلی پیش آمده است ...", Toast.LENGTH_LONG).show();
                    }
                    if (responseStr.getStatus().intValue() == 1) {
                        LoginActivity.this.realm.beginTransaction();
                        LoginActivity.this.realm.deleteAll();
                        UserDbClass user = (UserDbClass) LoginActivity.this.realm.createObject(UserDbClass.class);
                        user.setName(responseStr.getStatusMessage());
                        user.setUsername(LoginActivity.this.usernameEt.getText().toString());
                        user.setPassword(LoginActivity.this.passwordEt.getText().toString());
                        LoginActivity.this.realm.commitTransaction();
                        LoginActivity.this.startActivity(new Intent(LoginActivity.this.getApplicationContext(), MainActivityRefactor.class));
                    }
                }

                public void onFailure(Call<Retro> call, Throwable t) {
                    t.printStackTrace();
                    t.getLocalizedMessage();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "گذرواژه خالی است.", Toast.LENGTH_LONG).show();
        }
    }

    /* access modifiers changed from: protected */

    private void prepareNetworkReq() {
        this.service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
    }
}
