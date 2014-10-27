package com.wdc.nintenbro;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;

public class MapView extends TileView {
	private static final String TAG = "MapView";
	
	private Map mMap;
	
	private static final int RED_STAR = 1;
    private static final int YELLOW_STAR = 2;
    private static final int GREEN_STAR = 3;
    
    private long mMoveDelay = 1000;
    private long mLastMove;
    
    private RefreshHandler mRedrawHandler = new RefreshHandler();

    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
        	MapView.this.update();
        	MapView.this.invalidate();
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
        
    } // end class RefreshHandler
    
    public void update() {
	    long now = System.currentTimeMillis();
	
	    if (now - mLastMove > mMoveDelay) {
	        clearTiles();
	        drawMap();
	        mLastMove = now;
	    }
	    mRedrawHandler.sleep(mMoveDelay);
    } // end function update
    
    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMapView(context);
    } // end function MapView

    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initMapView(context);
    } // end function MapView

    private void initMapView(Context context) {
    	
    	Log.v(TAG, "initialize the map view");
    	
    	Map testMap = new Map();
    	testMap.drawTestMap();
    	this.setMap(testMap);
    	
        setFocusable(true);

        Resources r = this.getContext().getResources();
        
        ShapeDrawable test = new ShapeDrawable();
        // brown
        test.getPaint().setColor(0xFF78532E);
        
        ShapeDrawable test2 = new ShapeDrawable();
        // green
        test2.getPaint().setColor(0xFF007C00);

        resetTiles(4);
        //index 0 is blank
        //loadTile(RED_STAR, r.getDrawable(R.drawable.redstar));
        loadTile(RED_STAR, test);
        loadTile(YELLOW_STAR, r.getDrawable(R.drawable.yellowstar));
        //loadTile(GREEN_STAR, r.getDrawable(R.drawable.greenstar));
        loadTile(GREEN_STAR, test2);

    } // end function initMapView
    
    public void setMap(Map map) {
    	Log.v(TAG, "set the mapview's map");
    	mMap = map;
    } // end function setMap
    
    private void drawMap() {
    	int xfactor = (int) Math.ceil( (double) Map.MAP_ROWS / mXTileCount);
    	int yfactor = (int) Math.ceil( (double) Map.MAP_COLUMNS / mYTileCount);
    	
    	for ( int x = 0; x < mXTileCount; x++ ) {

			for ( int y = 0; y < mYTileCount; y++ ) {
				setTile(RED_STAR, x, y);
			}
			
		}
    	
    	// Set tiles defined by the map
		for ( int x = 0; x < Map.MAP_ROWS; x++ ) {

			for ( int y = 0; y < Map.MAP_COLUMNS; y++ ) {
				
				if ( ( ( x / xfactor ) < mXTileCount ) && ( ( y / yfactor ) < mYTileCount ) ) {
					
					if ( mMap.tileArray[x][y] == (char) 1 ) {
						setTile(GREEN_STAR, ( x / xfactor ), ( y / yfactor ));
					}
					else if ( mMap.tileArray[x][y] == (char) 0 ) {
						setTile(RED_STAR, ( x / xfactor ), ( y / yfactor ));
					}
					
				}
				
			}
			
		}
		
    } // end function drawMap
    
}
