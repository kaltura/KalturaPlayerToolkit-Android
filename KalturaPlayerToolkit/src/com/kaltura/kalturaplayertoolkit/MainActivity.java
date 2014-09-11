package com.kaltura.kalturaplayertoolkit;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.kaltura.playersdk.PlayerViewController;


public class MainActivity extends Activity {
	public static String TAG = "mainActivity";
	public static final String PROP_IFRAME_URL = "iframeUrl";
	
	private PlayerViewController mPlayerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        mPlayerView = (PlayerViewController) findViewById( R.id.player );
        mPlayerView.setActivity(MainActivity.this);
        
        Intent intent = getIntent();
        
        ///////////////////////////////////////////////////////////////////////////
        //for tests
     //  intent.putExtra(PROP_IFRAME_URL, "http://10.0.20.117/html5.kaltura/mwEmbed/mwEmbedFrame.php/p/524241/sp/52424100/uiconf_id/25906371/wid/_524241/entry_id/0_8zzalxul?iframeembed=true&#038;playerId=kaltura_player_1404535475&#038;entry_id=0_8zzalxul");
        /////////////////////////////////////////////
        
	     // check if this intent is started via browser
	     if ( Intent.ACTION_VIEW.equals( intent.getAction() ) ) {
	       Uri uri = intent.getData();
	       String[] params = uri.toString().split(":=");
	       if ( params!=null && params.length > 1 ) {
		       String iframeUrl = params[1];
		       intent.putExtra(PROP_IFRAME_URL, iframeUrl);
	       } else {
	    	   Log.w(TAG, "didn't load iframe, invalid iframeUrl parameter was passed" ); 
	       }

	     }
	     
	     if ( intent.getStringExtra(PROP_IFRAME_URL)!= null ) {
	    	 showPlayerView();
	     } else {
	    	  Button goBtn = (Button) findViewById(R.id.submitBtn);
	          final EditText urlText = (EditText) findViewById(R.id.iframeUrl);

	          mPlayerView.setVisibility(RelativeLayout.GONE);
	  		
	          if ( goBtn != null) {
	              goBtn.setOnClickListener( new OnClickListener() {
	              	
	  				@Override
	  				public void onClick(View v) {
	  					//TODO validate url
	  					getIntent().putExtra(PROP_IFRAME_URL, urlText.getText().toString());
	  	            	showPlayerView();
	  				}
	              	
	              });
	          } 
	     }

    }
    
    private void showPlayerView() {
		mPlayerView.setVisibility(RelativeLayout.VISIBLE);
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        mPlayerView.addComponents( getIntent().getStringExtra(PROP_IFRAME_URL), this);
        mPlayerView.setPlayerViewDimensions( size.x, size.y, 0, 0 );
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if ( mPlayerView.getVisibility() == RelativeLayout.VISIBLE ) {
        	Timer swapTimer = new Timer();
        	swapTimer.schedule(new TimerTask() {
	    			            @Override
					            public void run() {
					                runOnUiThread(new Runnable() {
					                    public void run() {
					                    	 Point size = new Point();
					    			        getWindowManager().getDefaultDisplay().getSize(size);
					    			        mPlayerView.setPlayerViewDimensions( size.x, size.y, 0, 0 );
					                    }
					                });
					            }
				        }, 100 );    
        			        	
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
