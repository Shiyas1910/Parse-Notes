package com.example.mohamedshiyas.parsenoteapp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DeletionListener{

    NotesRecyclerAdapter notesRecyclerAdapter;
    TextView placeHolderText;
    ParseQuery<ParseObject> query;
    List<Notes> notes;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String deviceToken = sharedPreferences.getString("token","");
        Log.e("Device Token", deviceToken);
//        String token = FirebaseInstanceId.getInstance().getToken();
//        Log.e("Token TAG ", token);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "449997255612");
//        installation.put("deviceToken", deviceToken);
        installation.saveInBackground();

        placeHolderText = (TextView) findViewById(R.id.placeHolderText);

        notesRecyclerAdapter = new NotesRecyclerAdapter(Collections.<Notes>emptyList(), this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new NoteItemTouchCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT, this));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(notesRecyclerAdapter);
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> objects, ParseException e) {
//                if (e == null) {
//                    for (ParseObject oneOBJ : objects) {
//                        Log.e("Titlee", oneOBJ.getString("Title"));
//                        Log.e("Descriptionn", oneOBJ.getString("Description"));
//                    }
//                }
//            }
//        });
//        query.getInBackground("xk4GrBNrEU", new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject object, ParseException e) {
//                if (e == null) {
//                    Log.e("Title", object.getString("Title"));
//                    Log.e("Description", object.getString("Description"));
//                }
//                else {
//                    Log.e("Something went wrong", "");
//                }
//            }
//        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(NoteActivity.newInstance(view.getContext(), null));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog = ProgressDialog.show(MainActivity.this,
                "Getting Notes",
                "Just a second!");
        notes = new ArrayList<>();
        query = ParseQuery.getQuery("Notes");
        if (isNetworkAvailable()) {
            queryDatas();
        }
        else {
            query.fromLocalDatastore();
            queryDatas();
        }
    }

    @Override
    public void itemRemoved(int position) {
        Notes notes = notesRecyclerAdapter.getItem(position);
        notesRecyclerAdapter.removeItem(position);
        if (!isNetworkAvailable()) {
            notes.unpinInBackground();
            Log.e("Deleted or not?", "");
            return;
        }
        notes.deleteInBackground();
        Log.e("Deleted or not?2", "");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void queryDatas() {
//        query.findInBackground(new FindCallback<Notes>() {
//            @Override
//            public void done(List<Notes> objects, ParseException e) {
//                if (e == null) {
//                    for (Notes oneOBJ : objects) {
//                        Log.e("Titlee", oneOBJ.getString("Title"));
//                        Log.e("Descriptionn", oneOBJ.getString("Description"));
//                        notes.add(oneOBJ);
//                    }
//                    notesRecyclerAdapter.updateList(notes);
//                }
//            }
//        });
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject oneOBJ : objects) {
//                        Log.e("Titlee", oneOBJ.getString("Title"));
//                        Log.e("Descriptionn", oneOBJ.getString("Description"));
                        notes.add((Notes) oneOBJ);
                        oneOBJ.saveEventually();
                        if (isNetworkAvailable()) {
                            oneOBJ.unpinInBackground();
                        }
                    }
                    notesRecyclerAdapter.updateList(notes);
                    notesRecyclerAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                }
            }
        });
    }
}
