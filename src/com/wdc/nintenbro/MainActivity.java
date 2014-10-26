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
import android.widget.TextView;
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
    
    private static final boolean mServerModeFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize
        mCurrentItem = Item.NULL;
        
        if ( mServerModeFlag == false ) {
        	
        	Log.v("Nintenbro", "client flag");
        
	        // Start the client thread to open a socket
	        new Thread( new ClientThread() ).start();
        
        }
        else {
        	
        	Log.v("Nintenbro", "server flag");
        	
        	// Handler on the UI thread allows the server thread to update the UI with items
        	updateConversationHandler = new Handler();
        
	        // Start listening on the server thread
	        new Thread( new ServerThread() ).start();
        
        }

    } // end function onCreate
    
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
		
	} // end function onStop

    class ClientThread implements Runnable {
    	
    	@Override
    	public void run() {
    		
    		try {
    			
    			// TODO - IP address
    			InetAddress servAddr = InetAddress.getByName(TARGET_IP);
    			
    			// TODO - port number
    			mSocket = new Socket(servAddr, TARGET_PORT);
    			
    			updateConversationHandler.post( new updateConnectionText( "Connected" ) );
    			
    		}
    		// TODO - better error handling
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    		
    	} // end function run
    	
    } // end class ClientThread
    
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

					// Launch input communication thread to handle the message received
					InputCommunicationThread commThread = new InputCommunicationThread( socket );
					new Thread( commThread ).start();
				} 
				catch ( Exception e ) {
					e.printStackTrace();
				}
				
			}
			
		} // end function run
		
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
			
		} // end function InputCommunicationThread

		public void run() {
			
			updateConversationHandler.post( new updateConnectionText( "Connected" ) );

			while ( !Thread.currentThread().isInterrupted() ) {

				try {
					String read = input.readLine();
					Log.v("Nintenbro", "read " + read);
					
					// Post to UI thread's handler
					updateConversationHandler.post( new updateUIThread( read ) );
					
				} 
				catch ( Exception e ) {
					e.printStackTrace();
				}
				
			}
			
		} // end function run

	} // end class Communication Thread
    
    class updateUIThread implements Runnable {
		private String msg;

		public updateUIThread( String str ) {
			this.msg = str;
		} // end function updateUIThread

		@Override
		public void run() {
			Toast.makeText(getApplicationContext(), "Client Says: "+ msg, Toast.LENGTH_SHORT).show();
			
			if ( msg.equals("receive mushroom") )
				setItem( Item.MUSHROOM );
			else if ( msg.equals("receive redshell") )
				setItem( Item.REDSHELL );
			else if ( msg.equals("receive banana") )
				setItem( Item.BANANA );
			
		} // end function run
		
	} // end class updateUIThread
    
    class updateConnectionText implements Runnable {
    	private String msg;
    	
    	public updateConnectionText( String str ) {
			this.msg = str;
		} // end function updateConnectionText
    	
    	@Override
		public void run() {
    		TextView connectionText= (TextView) findViewById(R.id.textView1);
    		connectionText.setText(msg);
		} // end function run
    	
    } // end class updateConnectionText

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    } // end function onCreateOptionsMenu

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
    } // end function onOptionsItemSelected
    
    public void sendMessage(View view) {
    	
    	Log.v("Nintenbro", "sendMessage");
    	
    	if ( mCurrentItem == Item.NULL ) {
    		
    		// No item would mean no message
    		// Set a random item for funsies
    		Random r = new Random();
        	int randomInt = r.nextInt(Item.values().length - 1);
    		setItem(Item.values()[randomInt]);
    		
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
    	}
    	
    } // end function sendMessage
    
    public void setItem (Item item) {
    	ImageView img= (ImageView) findViewById(R.id.imageView1);
    	
    	// Save the current item
    	mCurrentItem = item;
    	
    	switch (item) {
    	
    		case BANANA :
    			img.setVisibility(View.VISIBLE);
    			img.setImageResource(R.drawable.banana);
    			setButtonOn();
    			break;
    		case MUSHROOM :
    			img.setVisibility(View.VISIBLE);
    			img.setImageResource(R.drawable.mushroom);
    			setButtonOn();
    			break;
    		case REDSHELL :
    			img.setVisibility(View.VISIBLE);
    			img.setImageResource(R.drawable.redshell);
    			setButtonOn();
    			break;
    		default :
    			img.setVisibility(View.INVISIBLE);
    			setButtonOff();
    			break;
    			
    	}
    	
    } // end function setItem
    
    public void setButtonOn() {
    	ImageButton clickButton = (ImageButton) findViewById(R.id.imageButton1);
    	clickButton.setImageResource(android.R.drawable.btn_star_big_on);
    } // end function setButtonOn
    
    public void setButtonOff() {
    	ImageButton clickButton = (ImageButton) findViewById(R.id.imageButton1);
    	clickButton.setImageResource(android.R.drawable.btn_star_big_off);
    } // end function setButtonOff
    
} // end class MainActivity
