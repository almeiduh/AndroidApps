package com.example.stockquote;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

public class StockInfoActivity extends Activity {
	
	private static final String TAG = "STOCKQUOTE"; // This is to identify the messages in logcat
	
	// TextViews
	private TextView companyNameTextView;
	private TextView yearHighTextView;
	private TextView yearLowTextView;
	private TextView dayHighTextView;
	private TextView dayLowTextView;
	private TextView lastPriceTextView;
	private TextView changeTextView;
	private TextView dailyPriceRangeTextView;

	//	XML Tags
	static final String KEY_QUOTE = "quote";
	static final String KEY_NAME = "Name";
	static final String KEY_YEAR_HIGH = "YearHigh";
	static final String KEY_YEAR_LOW = "YearLow";
	static final String KEY_DAYS_HIGH = "DaysHigh";
	static final String KEY_DAYS_LOW = "DaysLow";
	static final String KEY_LAST_TRADE_PRICE_ONLY = "LastTradePriceOnly";
	static final String KEY_CHANGE = "Change";
	static final String KEY_DAYS_RANGE = "DaysRange";

	// XML Retrieved Data
	private String companyName = "";
	private String yearHigh = "";
	private String yearLow = "";
	private String dayHigh = "";
	private String dayLow = "";
	private String lastPrice = "";
	private String change = "";
	private String dailyPriceRange = "";
	
	String yahooUrlFirst = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quote%20where%20symbol%20in%20(%22";
	String yahooUrlLast = "%22)&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stock_info);
		
		Intent intentWichCalledMe = getIntent();
		
		String stockSymbol = intentWichCalledMe.getStringExtra(MainActivity.STOCK_SYMBOL);
		
		// Initialize textViews
		companyNameTextView = (TextView) findViewById(R.id.companyNameTextView);
		yearHighTextView = (TextView) findViewById(R.id.yearHighTextView);
		yearLowTextView = (TextView) findViewById(R.id.yearLowTextView);
		dayHighTextView = (TextView) findViewById(R.id.dayHighTextView);
		dayLowTextView = (TextView) findViewById(R.id.dayLowTextView);
		lastPriceTextView = (TextView) findViewById(R.id.lastPriceTextView);
		changeTextView = (TextView) findViewById(R.id.changeTextView);
		dailyPriceRangeTextView = (TextView) findViewById(R.id.dailyPriceRangeTextView);

		
		Log.d(TAG, "Before URL Creation of: " + stockSymbol);
		
		final String yqlUrl = yahooUrlFirst + stockSymbol + yahooUrlLast;
		
		new MyAsyncTask().execute(yqlUrl);
	}

	private class MyAsyncTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {

			try {
				Log.d(TAG, "Establishing Connection...");

				URL url = new URL(params[0]); // Create URL

				URLConnection connection = url.openConnection(); // Open connection to URL

				// Provides several ways to check if connection was successfully established
				HttpURLConnection httpConnection = (HttpURLConnection) connection; 

				//Check if connection was established
				int responseCode = httpConnection.getResponseCode();

				//If connection was established
				if (responseCode == HttpURLConnection.HTTP_OK) {
					
					Log.d(TAG, "Connection Established!");
					
					// Get Input Stream from HTTP connection.
					InputStream inputStream = httpConnection.getInputStream();

					// Get an instance of a document builder factory
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

					// Get a documentBuilder from the Factory
					DocumentBuilder db = dbf.newDocumentBuilder();

					// Get a Document Object Module using the document Builder parse(...) method
					Document dom = db.parse(inputStream);

					// Define root element which is going to be inside the DOM
					Element docElement = dom.getDocumentElement();

					// Get the node "quote"
					NodeList nl = docElement.getElementsByTagName(KEY_QUOTE);

					Log.d(TAG, "Found " + nl.getLength() + " quote");
					
					// If the node was found
					if (nl != null && nl.getLength() > 0) {

						// Cicle through all of "quote" tags
						for (int i = 0; i< nl.getLength(); i++) {
							StockInfo stockInfo = getStockInformation(docElement);

							companyName = stockInfo.getName();
							yearHigh = stockInfo.getYearHigh();
							yearLow = stockInfo.getYearLow();
							dayHigh = stockInfo.getDaysHigh();
							dayLow = stockInfo.getDaysLow();
							lastPrice = stockInfo.getLastTradePriceOnly();
							change = stockInfo.getChange();
							dailyPriceRange = stockInfo.getDaysRange();
						}

					}
				}
			} catch (MalformedURLException e) {
				Log.d(TAG, "Malformed URL");
			} catch (IOException e) {
				Log.d(TAG, "IO Exception");
			} catch (ParserConfigurationException e) {
				Log.d(TAG, "Parser Configuration Exception");
			} catch (SAXException e) {
				Log.d(TAG, "SAX Exception");
			}

			return null;
		}
		
		// To update GUI. This is executed after execute().
		protected void onPostExecute(String result) {
			Log.d(TAG, "Updating View");

			setTitle(companyName);
			companyNameTextView.setText(companyName);
			yearHighTextView.append(yearHigh);
			yearLowTextView.append(yearLow);
			dayHighTextView.append(dayHigh);
			dayLowTextView.append(dayLow);
			lastPriceTextView.append(lastPrice);
			changeTextView.append(change);
			dailyPriceRangeTextView.append(dailyPriceRange);
		}
		
		private StockInfo getStockInformation(Element docElement) {
			
			String companyName = getText(docElement, KEY_NAME);
			String yearHigh = getText(docElement, KEY_YEAR_HIGH);
			String yearLow = getText(docElement, KEY_YEAR_LOW);
			String dayHigh = getText(docElement, KEY_DAYS_HIGH);
			String dayLow = getText(docElement, KEY_DAYS_LOW);
			String lastPrice = getText(docElement, KEY_LAST_TRADE_PRICE_ONLY);
			String change = getText(docElement, KEY_CHANGE);
			String dailyPriceRange = getText(docElement, KEY_DAYS_RANGE);
			
			StockInfo theStock = new StockInfo(dayLow, dayHigh, yearLow, yearHigh, companyName, lastPrice, change, dailyPriceRange);
			
			return theStock;
		}


		private String getText(Element docElement, String key) {
			
			String tagValueToReturn = null;
			Log.d(TAG, "Getting information of '" + key + "' from XML");
			NodeList nl = docElement.getElementsByTagName(key);
			
			if (nl != null && nl.getLength() > 0) {
				try {
					Element element = (Element) nl.item(0);
					
					tagValueToReturn = element.getFirstChild().getNodeValue();
					Log.d(TAG, key + " = " + tagValueToReturn);
				} catch (DOMException e) {
					Log.w(TAG, "Invalid Value from XML");
					tagValueToReturn = "Invalid Value";
				} catch (NullPointerException e) {
					Log.w(TAG, "Invalid Value from XML");
					tagValueToReturn = "Invalid Value";
				}
			}
			
			return tagValueToReturn;
		}
		
	}

}
