package com.wdc.nintenbro;

import java.net.DatagramSocket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private DatagramSocket mSendingSocket;
	private DatagramSocket mListeningSocket;
	Handler updateConversationHandler;
	private MapView mMapView;
	
	// Port to listen on
	public static final int SERVERPORT = 6000;
	
	// Address to send messages to
	private static final int TARGET_PORT = 5000;
    private static final String TARGET_IP = "192.168.2.103";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize
        mCurrentItem = Item.NULL;
        
        // Start the client thread to open a socket
        new Thread( new ClientThread() ).start();
    
    	// Handler on the UI thread allows the server thread to update the UI with items
    	updateConversationHandler = new Handler();
    
        // Start listening on the server thread
        new Thread( new ServerThread() ).start();
        
        // Start the map update loop
	    mMapView = (MapView) findViewById(R.id.mapview);
	    mMapView.update();

    } // end function onCreate
    
    @Override
	protected void onStop() {
		super.onStop();
		
		// I should close the sockets or something here, maybe on pause?
		
	} // end function onStop

    class ClientThread implements Runnable {
    	
    	@Override
    	public void run() {
    		
    		try {
    			InetAddress servAddr = InetAddress.getByName( TARGET_IP );
    			
    			mSendingSocket = new DatagramSocket( SERVERPORT + 1 ); // I suppose that I could not set a port to pick any available port
    			
    			byte[] buf = new byte[512];
			    DatagramPacket packet = new DatagramPacket(buf, buf.length);
			    
			    byte[] bytes = "Sup from Nintenbro".getBytes();
		        System.arraycopy(bytes, 0, packet.getData(), 0, bytes.length);
		        packet.setLength(bytes.length);
		        
		        packet.setAddress(servAddr);
                packet.setPort(TARGET_PORT);
    			
    			mSendingSocket.send(packet);
    		}
    		// TODO - better error handling
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    		
    	} // end function run
    	
    } // end class ClientThread
    
    class ServerThread implements Runnable {

		public void run() {

			try {
				mListeningSocket = new DatagramSocket(SERVERPORT);
				updateConversationHandler.post( new updateConnectionText( mListeningSocket.getLocalSocketAddress().toString() ) );

				byte[] buf = new byte[512];
				Arrays.fill(buf, (byte)0);
			    DatagramPacket packet = new DatagramPacket(buf, buf.length);
			    
			    while (true) {
			    	updateConversationHandler.post( new updateConnectionText( "Waiting on address " + mListeningSocket.getLocalSocketAddress().toString() ) );
			    	
			    	try
			    	{
			    		Log.v("Nintenbro", "Waiting for data");
			    		mListeningSocket.receive(packet);
			    		Log.v("Nintenbro", "Data received   : " + new String( packet.getData(), 0, packet.getLength() ) );
			    		Log.v("Nintenbro", "Packet was from : " + packet.getAddress().toString() );
				  
			    		// Post to UI thread's handler
			    		updateConversationHandler.post( new updateUIThread( new String( packet.getData(), 0, packet.getLength() ) ) );
					  
			    	}
			    	catch (IOException e)
			    	{
			    		e.printStackTrace();
			    	}
			    	
		    	}
		    
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			
		} // end function run
		
	} // end class ServerThread
    
    class updateUIThread implements Runnable {
		private String msg;

		public updateUIThread( String str ) {
			this.msg = str;
		} // end function updateUIThread

		@Override
		public void run() {
			Toast.makeText(getApplicationContext(), "Client Says: "+ msg, Toast.LENGTH_SHORT).show();
			
			//message << "x:" << p.x << " y:" << p.y;
			Pattern pattern = Pattern.compile( "x:([0-9]?) y:([0-9]?)" );
			Matcher matcher = pattern.matcher( msg );
			
			if ( matcher.matches() )
			{
				int x = Integer.parseInt( matcher.group(1) );
				int y = Integer.parseInt( matcher.group(2) );
				
				// need to scale down by 10? 1000 / MAP_ROWS or 1000 / MAP_COLUMNS
				mMapView.setCarLocation( x, y );
				
				// OFFROAD_DIRT, YELLOW_STAR, ROAD_GRASS, ITEM_BOX
				// mMapView.setTile(tileindex, x, y)
			}
			else if ( msg.equals("receive mushroom") )
			{
				setItem( Item.MUSHROOM );
			}
			else if ( msg.equals("receive redshell") )
			{
				setItem( Item.REDSHELL );
			}
			else if ( msg.equals("receive banana") )
			{
				setItem( Item.BANANA );
			}
			
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
    
    class senMessageThread implements Runnable {
		private String msg;

		public senMessageThread( String str ) {
			this.msg = str;
		}

		@Override
		public void run() {
			byte[] buf = new byte[512];
		    DatagramPacket packet = new DatagramPacket(buf, buf.length);
		    InetAddress servAddr;
		    
			try {
				servAddr = InetAddress.getByName( TARGET_IP );
			
			    byte[] bytes = msg.getBytes();
		        System.arraycopy(bytes, 0, packet.getData(), 0, bytes.length);
		        packet.setLength(bytes.length);
		        
		        packet.setAddress(servAddr);
	            packet.setPort(TARGET_PORT);
				
				try {
					mSendingSocket.send(packet);
				} 
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} 
			catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		} // end function run
		
	}

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
    
    public void sendItemMessage(View view) {
    	
    	Log.v("Nintenbro", "sendItemMessage");
    	
    	if ( mCurrentItem == Item.NULL ) {
    		
    		// No item would mean no message
    		// Set a random item for funsies
    		Random r = new Random();
        	int randomInt = r.nextInt(Item.values().length - 1);
    		setItem(Item.values()[randomInt]);
    		
    	}
    	else {
    		String message;
    		
    		switch (mCurrentItem) {
	    		case BANANA :
	    			message = "Launch banana";
	    			break;
	    		case MUSHROOM :
	    			message = "Launch mushroom";
	    			break;
	    		case REDSHELL :
	    			message = "Launch redshell";
	    			break;
	    		default :
	    			message = "Launch item";
	    			break;
    		}
    		
	        new Thread( new senMessageThread( message ) ).start();

    		setItem(Item.NULL);
    	}
    	
    } // end function sendMessage
    
    public void sendGreetingMessage(View view) {
    	new Thread( new senMessageThread( "Sup from Nintenbro" ) ).start();
    }
    
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
    	ImageButton clickButton = (ImageButton) findViewById(R.id.launchitembutton);
    	clickButton.setImageResource(android.R.drawable.btn_star_big_on);
    } // end function setButtonOn
    
    public void setButtonOff() {
    	ImageButton clickButton = (ImageButton) findViewById(R.id.launchitembutton);
    	clickButton.setImageResource(android.R.drawable.btn_star_big_off);
    } // end function setButtonOff
    
} // end class MainActivity
