package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.lab4.adapter.LVAdapter;
import com.example.lab4.model.Photo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView RVList;
    private ArrayList<Photo> list;
    private SwipeRefreshLayout mSrlLayout;

    //    private LVAdapter mRecyclerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RVList = findViewById(R.id.RVList);
        mSrlLayout = findViewById(R.id.srlLayout);
        mSrlLayout.setOnRefreshListener(this);
        setupData();


    }

    private void setupAdapter() {
        setupData();
    }

    private void setupData() {
        AndroidNetworking.post("https://www.flickr.com/services/rest")
                .addBodyParameter(" api_key", "5b069d87a9e1a49c47b952d46fa8a312")
                .addBodyParameter("user_id", "187037017@N03")
                .addBodyParameter("extras", "views, media, path_alias, url_sq, url_t, url_s, url_q, url_m, url_n, url_z, url_c, url_l, url_o")
                .addBodyParameter("format", "json")
                .addBodyParameter(" method", "flickr.favorites.getList")
                .addBodyParameter(" nojsoncallback", "1")
                .addBodyParameter("per_page", "10")
                .addBodyParameter("page", "0").build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject photos = response.getJSONObject("photos");
                            JSONArray photo = photos.getJSONArray("photo");
                            list = new Gson().fromJson(photo.toString(), new TypeToken<ArrayList<Photo>>() {
                            }.getType());
                            for (int i = 0; i < list.size(); i++) {
                                Log.e("image[" + i + "]", list.get(i).getUrlM());
                            }
                            RVList.setHasFixedSize(true);
                            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
                            RVList.setLayoutManager(staggeredGridLayoutManager);
                            LVAdapter mAdapter = new LVAdapter(list, MainActivity.this);
                            RVList.setAdapter(mAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setupAdapter();
                mSrlLayout.setRefreshing(false);
            }
        }, 2500);
    }
}