package com.example.radwan.soleek;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private String mJSONURLString;
    private ListView listView;
    private TextView textView;
    private ArrayList<Country> hCountries;
    private RequestQueue requestQueue;
    private Button hLogout;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        textView=findViewById(R.id.user_mail);
        hLogout=findViewById(R.id.logout_button);
        listView=findViewById(R.id.List_Countries);
        requestQueue= Volley.newRequestQueue(this);
        mJSONURLString=getString(R.string.json_url);
        hCountries=new ArrayList<>();

        firebaseAuth=FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        textView.setText(currentUser.getEmail());

        StartVolley();

        hLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(HomeActivity.this,MainActivity.class));
                firebaseAuth.signOut();
            }
        });

    }


    private void StartVolley(){
        //Volly
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                mJSONURLString,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray jsonArray;
                        try {
                            jsonArray=response.getJSONArray("Response");
                            JSONObject jsonObject;
                            for(int i=0;i<jsonArray.length();i++){
                                // Get current json object
                                jsonObject=jsonArray.getJSONObject(i);
                                // Get the current Country (json object) data
                                String coutryName=jsonObject.getString("Name");
                                // ADD Country name to List view
                                Country newCountry=new Country();
                                newCountry.name=coutryName;
                                Toast.makeText(HomeActivity.this,"country name:"+coutryName,
                                        Toast.LENGTH_SHORT).show();
                                hCountries.add(newCountry);
                                setList();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        Toast.makeText(HomeActivity.this,"ERROR",Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);

    }

    private void setList()
    {
        CountryAdapter adapter=new CountryAdapter(HomeActivity.this,hCountries);
        listView.setAdapter(adapter);
    }

}
