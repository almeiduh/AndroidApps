package com.example.crazytipcalc;

import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.widget.EditText;

public class CrazyTipCalc extends Activity {

	
	// static variables to save values when application is closed and re-opened.
	private static final String TOTAL_BILL = "TOTAL_BILL";
	private static final String CURRENT_TIP = "FINAL_TIP";
	private static final String BILL_WITHOUT_TIP = "BILL_WITHOUT_TIP";
	
	private double billBeforeTip;
	private double tipAmount;
	private double finalBill;
	
	EditText billBeforeTipEt;
	EditText tipAmountEt;
	EditText finalBillEt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crazy_tip_calc);
		
		
		if (savedInstanceState == null) {
			// This means I'm starting the app the 1st time
			billBeforeTip = 0.0;
			tipAmount = 0.15;
			finalBill = 0.0;
		} else {
			// I'm restoring the application
			billBeforeTip = savedInstanceState.getDouble(BILL_WITHOUT_TIP);
			tipAmount = savedInstanceState.getDouble(CURRENT_TIP);
			finalBill = savedInstanceState.getDouble(TOTAL_BILL);
		}

		billBeforeTipEt = (EditText) findViewById(R.id.BillEditText);
		tipAmountEt = (EditText) findViewById(R.id.tipEditText);
		finalBillEt = (EditText) findViewById(R.id.finalBillEditText);
		
		billBeforeTipEt.addTextChangedListener(billBeforeTipListner);
		
		
	}

	
	private TextWatcher billBeforeTipListner = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			try {
				// When text changes, Store the new value in billBeforeTip variable
				billBeforeTip = Double.parseDouble(s.toString());
				
			} catch(NumberFormatException e) {
				// If content in invalid, then set variable to 0.0
				billBeforeTip = 0.0;
			}
			
			updateTipAndFinalBill();
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.crazy_tip_calc, menu);
		return true;
	}

	protected void updateTipAndFinalBill() {
		double tipAmount = Double.parseDouble(tipAmountEt.getText().toString());
		
		double finalBill = billBeforeTip + (billBeforeTip * tipAmount);
		
		finalBillEt.setText(String.valueOf(finalBill));
		
		
		
	}

	
	protected void onSaveInstanceState(Bundle outState) {
		// This method is called whenever anything changes (app closes; screen rotates, etc...)
		super.onSaveInstanceState(outState);
		
		outState.putDouble(BILL_WITHOUT_TIP, billBeforeTip);
		outState.putDouble(CURRENT_TIP, tipAmount);
		outState.putDouble(TOTAL_BILL, finalBill);
	}
	
}
