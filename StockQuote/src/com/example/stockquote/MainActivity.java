package com.example.stockquote;

import java.util.Arrays;

import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends Activity {

	public final static String STOCK_SYMBOL = "com.example.stockquote.STOCK";
	
	private SharedPreferences stockSymbolsEntered;
	
	private TableLayout stockTableScrollView;
	
	private EditText stockSymbolEditText;
	
	private Button enterStockSymbolButton;
	private Button deleteStocksButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		stockSymbolsEntered = getSharedPreferences("stocksList", MODE_PRIVATE);
		
		stockTableScrollView = (TableLayout) findViewById(R.id.stockTableScrollView);
		
		stockSymbolEditText = (EditText) findViewById(R.id.stockSymbolEditText);
		
		enterStockSymbolButton = (Button) findViewById(R.id.enterStockSymbolButton);
		deleteStocksButton = (Button) findViewById(R.id.deleteStocksButton);
		
		setButtonOnClickListners();
		
		updateSavedStockList(null);
		
	}

	private void updateSavedStockList(String newStockSymbol) {
		String[] stocks = stockSymbolsEntered.getAll().keySet().toArray(new String[0]);
		
		Arrays.sort(stocks, String.CASE_INSENSITIVE_ORDER); //Sort Alphabetically, not considering the CaSe
	
		if (newStockSymbol != null) {
			insertStockInScrollView(newStockSymbol, Arrays.binarySearch(stocks, newStockSymbol));
		} else {
			
			for (int i = 0; i< stocks.length; i++) {
				insertStockInScrollView(stocks[i], i);
			}
		}
	}

	private void insertStockInScrollView(String stock, int arrayIndex) {
		// inflater will allow to place the stock_quote_row.xml table inside the scroll view.
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		
		//This is the overall view (the whole column)
		View stockRow = inflater.inflate(R.layout.stock_quote_row, null);
		
		//These are the items inside the column
		TextView newStockTextView = (TextView) stockRow.findViewById(R.id.stockSymbolTextView);
		
		newStockTextView.setText(stock);
		
		Button stockQuoteButton = (Button) stockRow.findViewById(R.id.stockQuoteButton);
		Button quoteFromWebButton = (Button) stockRow.findViewById(R.id.quoteFromWebButton);
		
		/***************************************************************/
		
		stockQuoteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TableRow parentTable = (TableRow) v.getParent();
				
				TextView stockTextView = (TextView) parentTable.findViewById(R.id.stockSymbolTextView);
				
				String stockSymbol = stockTextView.getText().toString();
				
				Intent intent = new Intent(MainActivity.this, StockInfoActivity.class);
				
				intent.putExtra(STOCK_SYMBOL, stockSymbol);				
				
				startActivity(intent);
			}
		});
		
		quoteFromWebButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TableRow parentTable = (TableRow) v.getParent();
				
				TextView stockTextView = (TextView) parentTable.findViewById(R.id.stockSymbolTextView);
				
				String stockSymbol = stockTextView.getText().toString();
				
				String stockUrl = getString(R.string.yahoo_stock_url) + stockSymbol;
				
				Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(stockUrl));
				
				startActivity(webIntent);
			}
		});
		
		/***************************************************************/
		stockTableScrollView.addView(stockRow, arrayIndex);
	}

	@SuppressLint("NewApi")
	private void saveStockSymbol(String newStock) {
		String isTheStockNew = stockSymbolsEntered.getString(newStock, null);
		
		SharedPreferences.Editor preferencesEditor = stockSymbolsEntered.edit();
		preferencesEditor.putString(newStock, newStock);
		preferencesEditor.apply();
		
		if (isTheStockNew == null) {
			updateSavedStockList(newStock);
		}
		
	}
	
	
	/**
	 * Define Listeners for buttons.
	 */
	private void setButtonOnClickListners() {
		
		enterStockSymbolButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (stockSymbolEditText.getText().length() > 0) {
					saveStockSymbol(stockSymbolEditText.getText().toString());
					
					stockSymbolEditText.setText(""); // Clear editText
					
					//Force Keyboard to close
					InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(stockSymbolEditText.getWindowToken(), 0);
				} else {
					// Create dialog box!
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setTitle(R.string.invalid_stock_symbol);
					builder.setPositiveButton(R.string.ok, null);
					builder.setMessage(R.string.missing_stock_symbol);
					
					AlertDialog alertDialog = builder.create();
					
					alertDialog.show();
				}
				
			}
		});
		
		deleteStocksButton.setOnClickListener(new OnClickListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				// Remove from scroll view
				stockTableScrollView.removeAllViews();
				
				// Remove from shared preferences
				SharedPreferences.Editor preferencesEditor = stockSymbolsEntered.edit();
				
				preferencesEditor.clear();
				
				preferencesEditor.apply();
				
			}
		});		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
