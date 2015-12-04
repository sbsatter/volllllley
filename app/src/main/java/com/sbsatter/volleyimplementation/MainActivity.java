package com.sbsatter.volleyimplementation;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.til1)TextInputLayout textInputLayout;
    @Bind(R.id.urlET)EditText urlEt;
    @Bind(R.id.getButton)Button getButton;
    @Bind(R.id.relativeLayout)RelativeLayout relativeLayout;
    @Bind(R.id.fetchedData) TextView fetchedData;
    RequestQueue request;
    private final String TAG="URL DATA FETCH";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @OnClick(R.id.getButton)
    public void submit(){
//        getJSONData();
        Log.i("TAG","BUTTON PRESSED");
        fetchedData.setText("FETCHING...");
        if(false){
            return;
        }
//        RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup
//                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.addRule(RelativeLayout.BELOW,getButton.getId() );
//        relativeLayout.addView(fetchedData,params);
        if(request==null){
            Cache cache= new DiskBasedCache(getCacheDir(),1024*1024);
            Network network= new BasicNetwork(new HurlStack());
            request= new RequestQueue(cache,network);
            request.start();
        }else{
            request.cancelAll(TAG);
        }
//
//
//
////        String url= urlEt.getText().toString();
////        if(url.substring(0,4).equals("www.")){
////            url="http://"+url;
////        }
////        else if(! (url.length()>7 &&(url.substring(0,11).equals("http://")))){
////            url="http://"+url;
////        }
//        RequestQueue requestQueue= Volley.newRequestQueue(this);
        String url="http://dss.brac.net/bracstandingdata/Service.asmx/GetAllDivision";
        StringRequest stringRequest= new StringRequest(Request.Method.GET, url, new Response
                .Listener<String>() {
            @Override
            public void onResponse(String response) {
//                fetchedData.setText(response);
//                urlEt.setText("");
                Log.i("TAG",response.substring(0,100));
                String responseString="not yet initialized";
                XmlPullParserFactory factory = null;
                try {
                    factory = XmlPullParserFactory.newInstance();

                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();

                    xpp.setInput(new StringReader(response));
                    int eventType = xpp.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_DOCUMENT) {
                            System.out.println("Start document");
                        } else if (eventType == XmlPullParser.START_TAG) {
                            System.out.println("Start tag " + xpp.getName());
                        } else if (eventType == XmlPullParser.END_TAG) {
                            System.out.println("End tag " + xpp.getName());
                        } else if (eventType == XmlPullParser.TEXT) {
                            responseString = xpp.getText();
                            System.out.println("Text " + xpp.getText());
                        }
                        eventType = xpp.next();
                    }
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("End document");
                fetchedData.setText(responseString);
                try {
                    JSONArray jsonArray= new JSONArray(responseString);
                    String district=jsonArray.getJSONObject(1).get("DivisionName").toString();
                    fetchedData.setText("You are in "+district);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAG", "ERROR:"+error.getMessage());
            }
        });
        stringRequest.setTag(TAG);
        request.add(stringRequest);
    }

    private void getJSONData() {
        String url="http://dss.brac.net/bracstandingdata/Service.asmx/GetAllDistrict";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, new
                Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Toast.makeText(MainActivity.this, "Found a response!", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAG", error.getMessage());
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
