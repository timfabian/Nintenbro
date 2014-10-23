package com.wdc.nintenbro;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	private Item mCurrentItem;
	private Socket mSocket;
	
	private static final int SERVERPORT = 5000;
    private static final String SERVER_IP = "10.0.2.2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize
        mCurrentItem = Item.NULL;
        
        // Start the client thread to open a socket
        new Thread( new ClientThread() ).start();

    }

    class ClientThread implements Runnable {
    	
    	@Override
    	public void run() {
    		
    		try {
    			
    			// TODO - IP address
    			InetAddress servAddr = InetAddress.getByName(SERVER_IP);
    			
    			// TODO - port number
    			mSocket = new Socket(servAddr, SERVERPORT);
    			
    		}
    		// TODO - better error handling
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    		
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
    
    public void sendMessage(View view) {
    	
    	if ( mCurrentItem == Item.NULL ) {
    		
    		// No item would mean no message
    		// Set a random item for funsies
    		Random r = new Random();
        	int randomInt = r.nextInt(Item.values().length - 1);
    		setItem(Item.values()[randomInt]);
    		setButtonOn();
    		
    	}
    	else {
    		try {
    			
    			// TODO - message protocol?
    			Toast.makeText(getApplicationContext(), "send launch item", Toast.LENGTH_SHORT).show();
	            PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( mSocket.getOutputStream() ) ), true);
	            out.println("Launch item");
	            
	        } 
    		catch (Exception e) {
	            e.printStackTrace();
	        }
    		
    		setItem(Item.NULL);
    		setButtonOff();
    	}
    	
    }
    
    public void setItem (Item item) {
    	ImageView img= (ImageView) findViewById(R.id.imageView1);
    	
    	// Save the current item
    	mCurrentItem = item;
    	
    	switch (item) {
    	
    		case BANANA :
    			img.setVisibility(View.VISIBLE);
    			img.setImageResource(R.drawable.banana);
    			break;
    		case MUSHROOM :
    			img.setVisibility(View.VISIBLE);
    			img.setImageResource(R.drawable.mushroom);
    			break;
    		case REDSHELL :
    			img.setVisibility(View.VISIBLE);
    			img.setImageResource(R.drawable.redshell);
    			break;
    		default :
    			img.setVisibility(View.INVISIBLE);
    			break;
    			
    	}
    	
    }
    
    public void setButtonOn() {
    	ImageButton clickButton = (ImageButton) findViewById(R.id.imageButton1);
    	clickButton.setImageResource(android.R.drawable.btn_star_big_on);
    }
    
    public void setButtonOff() {
    	ImageButton clickButton = (ImageButton) findViewById(R.id.imageButton1);
    	clickButton.setImageResource(android.R.drawable.btn_star_big_off);
    }
    
}
