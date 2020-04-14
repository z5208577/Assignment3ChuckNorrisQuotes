package com.example.assignment3chuck;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public TextView quoteText;
    public Button newQuote;
    public List<String> mCategories;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        quoteText = findViewById(R.id.quote);
        newQuote=findViewById(R.id.newQuote);
        //Implementation of the first core requirement: "a button to trigger the API call"
        newQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //I prefer Volley as the parsing is manual (retorift request and response handling uses automatic parsing)
                // and thus I can understand it better.
                final RequestQueue requestQueueQuote = Volley.newRequestQueue(v.getContext());
                final String quoteAPI = "https://api.chucknorris.io/jokes/random";
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Quote quote = new Gson().fromJson(response,Quote.class);
                        quoteText.setText("\"" + quote.getValue() + "\"");
                        requestQueueQuote.stop();

                        Animation expandIn = AnimationUtils.loadAnimation(v.getContext(), R.anim.animation_pop);
                        quoteText.startAnimation(expandIn);
                    }
                };

                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse (VolleyError error) {
                        System.out.println(error.toString());
                    }
                };
                StringRequest stringRequestQuote = new StringRequest(Request.Method.GET, quoteAPI, responseListener, errorListener);
                requestQueueQuote.add(stringRequestQuote);
            }
        });
        //Additional functionality; random quote by category. Categories are displayed in a clickable recyclerview
        getCategories(this.findViewById(android.R.id.content));

    }
    //
    public void getCategories(View view){
        final RequestQueue requestQueueQuote = Volley.newRequestQueue(view.getContext());
        final String categoriesAPI = "https://api.chucknorris.io/jokes/categories";
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Type listType = new TypeToken<List<String>>() {}.getType();
                mCategories = new Gson().fromJson(response, listType);
                fillRecyclerView();
                requestQueueQuote.stop();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error) {
                System.out.println(error.toString());
            }
        };
        StringRequest stringRequestQuote = new StringRequest(Request.Method.GET, categoriesAPI, responseListener, errorListener);
        requestQueueQuote.add(stringRequestQuote);
    }

    //recycler view uses a grid layout.
    public void fillRecyclerView(){
        RecyclerView mRecyclerView = findViewById(R.id.categoryList);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this,3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //the quote textbox is passed in as it will need to change depending on the category clicked.
        RecyclerView.Adapter mAdapter = new CategoryAdapter(this, mCategories,quoteText);
        mRecyclerView.setAdapter(mAdapter);
    }
}
