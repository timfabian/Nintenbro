package com.wdc.nintenbro;

public class Map {
	public static final int MAP_ROWS = 200; 
	public static final int MAP_COLUMNS = 200;
	
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
		
		for ( int x = 1; x <= MAP_ROWS; x++ ) {
			
			for ( int y = 1; y <= MAP_COLUMNS; y++ ) {
				
				double dx = x - ( MAP_ROWS / 2 );
		        double dy = y - ( MAP_COLUMNS / 2 );
		        int distance = (int) Math.sqrt(dx*dx + dy*dy);
		        
		        if ( distance >= 60 && distance <= 100 )
		        	tileArray[x-1][y-1] = (char) 1;
		        else
		        	tileArray[x-1][y-1] = (char) 0;
		        
		        //tileArray[20][20] = (char) 2;
		        //tileArray[80][20] = (char) 2;
		        //tileArray[50][80] = (char) 2;
		        
			}
			
		}
		
	}
	
}
