package com.oanda.stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Prices reader that work via Open API
 * Could be run into 2 ways:
 * - as an individual thread #start() 
 * - in the local context thread #run()
 */
public class JavaApiStreaming extends Thread {
	
	/**
	 * URL to server
	 */
	public static final String DOMAIN = "https://stream-fxpractice.oanda.com";
	
	
	/**
	 * List of instruments into Open API format
	 */
	public static final String INSTRUMENTS = "EUR_USD,USD_JPY,GBP_USD,USD_CHF,EUR_GBP,EUR_JPY,EUR_CHF,AUD_USD,USD_CAD,NZD_USD";
	
	/**
	 * Max server silent time out duration
	 */
	public static final long DATA_TIMEOUT = 10*1000; // 10s
	
	/**
	 * Notification reconnect warning limit
	 */
	public static final int RECONNECTION_COUNT = 3;
	
	protected CloseableHttpClient httpClient;
	protected TimeOutListener timeout;
	
	protected String access_token;
	protected String account_id;
	protected int pause = 1;
	protected int connectionCounter = 0;
	
	protected List<IPriceListener> listeners ;
	
	/**
	 * @param account_id Account ID
	 * @param access_token Personal Access Token
	 */
	public JavaApiStreaming( String account_id, String access_token) {
		this.account_id = account_id;
		this.access_token = access_token;
		listeners = new ArrayList<IPriceListener>();
	}
	
	@Override
	public void run() {

		// try to reconnect into any case
		while(true) {
		
			try {
				connect();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if ( httpClient != null) {
						httpClient.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				// cancel timeout as data flow stopped
				if ( timeout != null) {
					timeout.cancel();
				}
			}
			
			if ( connectionCounter > RECONNECTION_COUNT) {
				Mailer.send("Disconnected "+connectionCounter+" times");
			}
			
			
			try {
				sleep(1000);
				connectionCounter++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
		
	
	}
	
	protected void connect() throws IOException {
		HttpUriRequest httpGet = new HttpGet(DOMAIN + "/v1/prices?accountId=" + account_id + "&instruments=" + INSTRUMENTS);
        httpGet.setHeader(new BasicHeader("Authorization", "Bearer " + access_token));

        System.out.println("Executing request: " + httpGet.getRequestLine());

        
        httpClient =  HttpClientBuilder.create().build();
        HttpResponse resp = httpClient.execute(httpGet);
        HttpEntity entity = resp.getEntity();

        if (resp.getStatusLine().getStatusCode() == 200 && entity != null) {
            final InputStream stream = entity.getContent();
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            
           
            timeout = new TimeOutListener(stream, DATA_TIMEOUT);
            while ((line = br.readLine()) != null) {

            	Object obj = JSONValue.parse(line);
                JSONObject tick = (JSONObject) obj;

                // unwrap if necessary
                if (tick.containsKey("tick")) {
                	tick = (JSONObject) tick.get("tick");
                }

                if (tick.containsKey("instrument")) {
                	timeout.update();
                    
                    String instrument = tick.get("instrument").toString();
                    String time = tick.get("time").toString();
                    double bid = Double.parseDouble(tick.get("bid").toString());
                    double ask = Double.parseDouble(tick.get("ask").toString());
                    
                    for ( IPriceListener listener : listeners) {
                    	listener.tick(instrument, time, bid, ask);
                    }
                    
                }
                
                if (tick.containsKey("heartbeat")) {
                	timeout.update();
                }
                
                
            }
        } else {
        	
        	// print error message
            String responseString = EntityUtils.toString(entity, "UTF-8");
            System.out.println(responseString);
        }
        
        // exponential rising pause between reconnection if 429 ERROR
        if ( resp.getStatusLine().getStatusCode() == 429 ) {
        	try {
        		System.out.println("pause "+pause+"(s) by 429 ERROR");
				Thread.sleep(pause*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	pause = pause*2;
        	connectionCounter++;
        }
	}
	
	public void addPriceListener(IPriceListener listener) {
		listeners.add(listener);
	}
	
}
