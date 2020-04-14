package com.example.assignment3chuck;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.assignment3chuck.Quote;
import com.example.assignment3chuck.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<String> mCategories;
    private TextView mQuoteText;
    private Context mContext;


    public CategoryAdapter(Context context,List<String> categories,TextView quoteText) {
        mCategories = categories;
        mContext = context;
        mQuoteText = quoteText;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list,parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.category.setText(mCategories.get(position));
        //click makes a similar api call to the one in main activity.
        holder.category.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View view){
                final RequestQueue requestQueueQuote = Volley.newRequestQueue(view.getContext());
                final String categoriesAPI = "https://api.chucknorris.io/jokes/random?category="+ holder.category.getText();
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        TextView quoteText = view.findViewById(R.id.quote);
                        Quote quote = new Gson().fromJson(response,Quote.class);
                        mQuoteText.setText("\"" + quote.getValue() + "\"");
                        String toastString = "New quote of category: " + quote.getCategories();
                        //to prove proper quote object creation
                        toastString = toastString.substring(0,toastString.indexOf(":")+2)+ toastString.substring(toastString.indexOf("[")+1,toastString.indexOf("]"));
                        Toast.makeText(mContext,toastString , Toast.LENGTH_SHORT).show();
                        requestQueueQuote.stop();
                        Animation expandIn = AnimationUtils.loadAnimation(view.getContext(), R.anim.animation_pop);
                        mQuoteText.startAnimation(expandIn);
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
        });
    }
    //below is basic adapter methods
    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView category;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.category);

        }
    }

}
