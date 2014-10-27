package com.wdc.nintenbro;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.os.Bundle;

public class MapActivity extends ActionBarActivity {
	private Map mTestMap;
	private MapView mMapView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_map);
	    
	    // Start the map update loop
	    mMapView = (MapView) findViewById(R.id.mapview);
	    mMapView.update();
	}

}
