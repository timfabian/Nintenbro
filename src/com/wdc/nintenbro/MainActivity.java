package com.wdc.nintenbro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	private Item mCurrentItem;
	private Socket mSocket;
	private ServerSocket mServerSocket;
	Handler updateConversationHandler;
	
	// adb will have the first emulator launcher on port 5554 of the localhost
	// second emulator will be port 5556
	
	// telnet into the server emulator
	// redir add tcp:5000:6000
	
	// Port to open on the emulator's IP
	public static final int SERVERPORT = 6000;
	
	// 10.0.2.2:5000 is the alias for the localhost
	private static final int TARGET_PORT = 5000;
    private static final String TARGET_IP = "10.0.2.2";
    
    private static final boolean mServerModeFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize
        mCurrentItem = Item.NULL;
        
        if ( mServerModeFlag == false ) {
        
	        // Start the client thread to open a socket
	        new Thread( new ClientThread() ).start();
        
        }
        else {
        	
        	updateConversationHandler = new Handler();
        
	        // Start listening on the server thread
	        new Thread( new ServerThread() ).start();
        
        }

    }
    
    @Override
	protected void onStop() {
		super.onStop();
		
		try {
			
			if ( mServerModeFlag == false )
				mSocket.close();
			else
				mServerSocket.close();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

    class ClientThread implements Runnable {
    	
    	@Override
    	public void run() {
    		
    		try {
    			
    			// TODO - IP address
    			InetAddress servAddr = InetAddress.getByName(TARGET_IP);
    			
    			// TODO - port number
    			mSocket = new Socket(servAddr, TARGET_PORT);
    			
    		}
    		// TODO - better error handling
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    		
    	}
    	
    }
    
    class ServerThread implements Runnable {

		public void run() {
			Socket socket = null;
			
			try {
				mServerSocket = new ServerSocket(SERVERPORT);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			
			while ( !Thread.currentThread().isInterrupted() ) {

				try {
					socket = mServerSocket.accept();
					
					Log.v("Nintenbro", "server socket accepted connection");

					InputCommunicationThread commThread = new InputCommunicationThread( socket );
					new Thread( commThread ).start();
				} 
				catch ( Exception e ) {
					e.printStackTrace();
				}
				
			}
			
		}
		
	} // end class ServerThread
    
    class InputCommunicationThread implements Runnable {
		private Socket clientSocket;
		private BufferedReader input;

		public InputCommunicationThread( Socket clientSocket ) {

			this.clientSocket = clientSocket;

			try {
				this.input = new BufferedReader( new InputStreamReader( this.clientSocket.getInputStream() ) );
			} 
			catch ( Exception e ) {
				e.printStackTrace();
			}
			
		}

		public void run() {

			while ( !Thread.currentThread().isInterrupted() ) {

				try {
					String read = input.readLine();
					Log.v("Nintenbro", "read " + read);
					updateConversationHandler.post( new updateUIThread( read ) );
				} 
				catch ( Exception e ) {
					e.printStackTrace();
				}
				
			}
			
		}

	} // end class Communication Thread
    
    class updateUIThread implements Runnable {
		private String msg;

		public updateUIThread( String str ) {
			this.msg = str;
		}

		@Override
		public void run() {
			Log.v("Nintenbro", "Client Says: "+ msg);
		}
		
	} // end class updateUIThread

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
    	
    	Log.v("Nintenbro", "sendMessage");
    	
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
