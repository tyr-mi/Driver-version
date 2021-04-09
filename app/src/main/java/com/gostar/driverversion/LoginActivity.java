package com.gostar.driverversion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText usernameEt;
    private TextInputEditText passwordEt;

    private CardView loginBtn;

    private Realm realm;

    private GetDataService service;
    private Call<Retro> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
//        realm.beginTransaction();
//        realm.deleteAll();
//        realm.commitTransaction();
        RealmResults<UserDbClass> result = realm.where(UserDbClass.class).findAll();
        if (result.size() > 0) {
            Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(mainIntent);
        }
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareNetworkReq();

        usernameEt = findViewById(R.id.login_username_edit_text);
        passwordEt = findViewById(R.id.login_password_edit_text);

        loginBtn = findViewById(R.id.login_button_card_view);



        loginBtn.setOnClickListener(v -> {
            if (!usernameEt.getText().toString().trim().equals(""))
            {
                if (!passwordEt.getText().toString().trim().equals(""))
                {

                    CheckLogin check_login=new CheckLogin(1,usernameEt.getText().toString().trim(),passwordEt.getText().toString().trim());
                    call = service.checkLogin(check_login);

                    call.enqueue(new Callback<Retro>()
                    {
                        @Override
                        public void onResponse(Call<Retro> call, Response<Retro> response)
                        {
                            Retro responseStr = response.body();
                            if (responseStr.getStatus() == 1 )
                            {
                                realm.beginTransaction();
                                realm.deleteAll();
                                UserDbClass user = realm.createObject(UserDbClass.class);
                                user.setName(responseStr.getStatusMessage());
                                user.setUsername(usernameEt.getText().toString());
                                user.setPassword(passwordEt.getText().toString());
                                realm.commitTransaction();
                                Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(mainIntent);
                            }
                        }

                        @Override
                        public void onFailure(Call<Retro> call, Throwable t)
                        {
                            t.printStackTrace();
                            t.getLocalizedMessage();
                        }

                    });

                }
                else {
                    Toast.makeText(getApplicationContext(),"گذرواژه خالی است." , Toast.LENGTH_LONG).show();

                }
            } else {
                Toast.makeText(getApplicationContext(),"نام کاربری خالی است." , Toast.LENGTH_LONG).show();
            }

        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    private void prepareNetworkReq() {
        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
    }
}