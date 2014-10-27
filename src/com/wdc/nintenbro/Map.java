package com.wdc.nintenbro;

import android.util.Log;

public class Map {
	public static final int MAP_ROWS = 100; 
	public static final int MAP_COLUMNS = 100;
	
	private static final int LOW_BOUND = 10;
	private static final int HIGH_BOUND = 20;
	
	char[][] tileArray;
	
	public Map () {
		tileArray = new char[MAP_ROWS][MAP_COLUMNS];
		
		for ( int x = 0; x < MAP_ROWS; x++ ) {
			for ( int y = 0; y < MAP_COLUMNS; y++ ) {
				tileArray[x][y] = 0;
			}
		}
		
	}
	
	public void drawTestMap() {
		
		for ( int x = 0; x < MAP_ROWS; x++ ) {
			
			for ( int y = 0; y < MAP_COLUMNS; y++ ) {
				
				if ( ( x >= LOW_BOUND && x <= HIGH_BOUND ) && ( y >= LOW_BOUND && y <= (MAP_ROWS-LOW_BOUND) ) )
					tileArray[x][y] = (char) 1;
				else if ( ( x >= (MAP_ROWS-HIGH_BOUND) && x <= (MAP_ROWS-LOW_BOUND) ) && ( y >= LOW_BOUND && y <= (MAP_ROWS-LOW_BOUND) ) )
					tileArray[x][y] = (char) 1;
				else if ( ( y >= LOW_BOUND && y <= HIGH_BOUND ) && ( x >= LOW_BOUND && x <= (MAP_ROWS-LOW_BOUND) ) )
					tileArray[x][y] = (char) 1;
				else if ( ( y >= (MAP_COLUMNS-HIGH_BOUND) && y <= (MAP_COLUMNS-LOW_BOUND) ) && ( x >= LOW_BOUND && x <= (MAP_ROWS-LOW_BOUND) ) )
					tileArray[x][y] = (char) 1;
				else
					tileArray[x][y] = (char) 0;
				
			}
			
		}
		
	}
	
}
