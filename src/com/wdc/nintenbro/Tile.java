package com.wdc.nintenbro;

public class Tile {
	char type;
    char defaultType;
    
    public Tile () {
    	type = 0;
    	defaultType = 0;
    }
    
	public char GetType() {
		return type;
	}
	
	public void SetType(char newType) {
		type = newType;
	}
	
	public void SetDefaultType(char newDefaultType) {
		defaultType = newDefaultType;
	}
	
	public void ResetToDefault() {
		type = defaultType; 
	}
	
}