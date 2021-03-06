package com.example.crazytipcalc;

import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

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
	
	SeekBar changeTipSeekBar;
	
	private int[] checklistValues = new int[12];
	
	CheckBox friendlyBox;
	CheckBox specialsBox;
	CheckBox opinionBox;
	
	RadioGroup availableRadioGroup;
	
	RadioButton availableBadRadio;
	RadioButton availableOkRadio;
	RadioButton availableGoodRadio;
	
	Spinner problemsSpinner;
	
	Button startChronometerButton;
	Button pauseChronometerButton;
	Button resetChronometerButton;
	
	Chronometer timeWaitingChronometer;
	
	long secondsYouWaited = 0;
	
	TextView timeWaitingTextView;
	
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
		
		changeTipSeekBar = (SeekBar) findViewById(R.id.changeTipSeekBar);
		
		changeTipSeekBar.setOnSeekBarChangeListener(seekBarChangeListner);
		
		billBeforeTipEt.addTextChangedListener(billBeforeTipListner);
		
		friendlyBox = (CheckBox) findViewById(R.id.friendlyCheckBox);
		specialsBox = (CheckBox) findViewById(R.id.specialsCheckBox);
		opinionBox = (CheckBox) findViewById(R.id.optionCheckBox);
		
		setUpIntroCheckBoxes();
		
		availableRadioGroup = (RadioGroup) findViewById(R.id.availabilityRadioGroup);
		
		availableBadRadio = (RadioButton) findViewById(R.id.availableBadRadioButton);
		availableOkRadio = (RadioButton) findViewById(R.id.availableOkRadioButton);
		availableGoodRadio = (RadioButton) findViewById(R.id.availableGoodRadioButton);
		
		addChangeListnersToRadios();
		
		problemsSpinner = (Spinner) findViewById(R.id.problemsSpinner);
		
		addItemSelectedListner();
		
		startChronometerButton = (Button) findViewById(R.id.startButton);
		pauseChronometerButton = (Button) findViewById(R.id.pauseButton);
		resetChronometerButton = (Button) findViewById(R.id.resetButton);
		
		setButtonOnClickListners();
		
		timeWaitingChronometer = (Chronometer) findViewById(R.id.timeWaitingChronometer);
		
		timeWaitingTextView = (TextView) findViewById(R.id.timeWaitingTextView);
		
	}

	/**
	 * Seek Tip Bar change Listner.
	 */
	private OnSeekBarChangeListener seekBarChangeListner = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			tipAmount = (changeTipSeekBar.getProgress()) * 0.01;
			tipAmountEt.setText(String.format("%.02f", tipAmount));
			
			updateTipAndFinalBill();
		}
	};
	
	
	/**
	 * Listner for Bill TextBox.
	 */
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

	
	/**
	 * Add onChecked Listeners for all checkBox Buttons.
	 */
	private void setUpIntroCheckBoxes() {
		friendlyBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checklistValues[0] = (friendlyBox.isChecked())?4:0;
				
				setTipFromWaitressCheckList();
				
				updateTipAndFinalBill();
			}
		});
		
		specialsBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checklistValues[1] = (specialsBox.isChecked())?1:0;
				
				setTipFromWaitressCheckList();
				
				updateTipAndFinalBill();
			}
		});
		
		opinionBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checklistValues[2] = (opinionBox.isChecked())?2:0;
				
				setTipFromWaitressCheckList();
				
				updateTipAndFinalBill();
			}
		});
	}
	
	
	/**
	 * Calculate tip from waitress, using the checkListValues array.
	 */
	private void setTipFromWaitressCheckList() {
		int checkListTotal = 0;
		
		for(int item: checklistValues) {
			checkListTotal += item;
		}
		
		tipAmountEt.setText(String.format("%.02f", checkListTotal * 0.01));
	}
	
	/**
	 * Update tipAmount private variable and set editText final bill;
	 */
	protected void updateTipAndFinalBill() {
		double tipAmount = Double.parseDouble(tipAmountEt.getText().toString());
		
		double finalBill = billBeforeTip + (billBeforeTip * tipAmount);
		
		finalBillEt.setText(String.valueOf(finalBill));
	}


	/**
	 * Create Change listners for radio buttons
	 */
	private void addChangeListnersToRadios() {

		availableRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				checklistValues[3] = (availableBadRadio.isChecked())?-1:0;
				checklistValues[4] = (availableOkRadio.isChecked())?2:0;
				checklistValues[5] = (availableGoodRadio.isChecked())?4:0;
				
				setTipFromWaitressCheckList();
				
				updateTipAndFinalBill();
			}
		});
		
		
		
	}
	
	
	/**
	 * Add listner to Spinner 
	 */
	private void addItemSelectedListner() {
		problemsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				checklistValues[6] = (problemsSpinner.getSelectedItem().equals("Bad"))?-1:0;
				checklistValues[7] = (problemsSpinner.getSelectedItem().equals("OK"))?3:0;
				checklistValues[8] = (problemsSpinner.getSelectedItem().equals("Good"))?6:0;
				
				setTipFromWaitressCheckList();
				
				updateTipAndFinalBill();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
	}
	
	/**
	 * Set button listners.
	 */
	private void setButtonOnClickListners() {
		startChronometerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int stoppedMilliseconds = 0;
				
				String chronoText = timeWaitingChronometer.getText().toString();
				String array[] = chronoText.split(":");
				
				if (array.length == 2) {
					// MM:SS
					stoppedMilliseconds = Integer.parseInt(array[0])*60*1000 + Integer.parseInt(array[1])*1000;
					
				} else if (array.length == 3) {
					// H:MM:SS
					stoppedMilliseconds = Integer.parseInt(array[0])*60*60*1000 + Integer.parseInt(array[1])*60*1000 + Integer.parseInt(array[2])*1000;
				}
				
				timeWaitingChronometer.setBase(SystemClock.elapsedRealtime() - stoppedMilliseconds);
				
				secondsYouWaited = Long.parseLong(array[1]);
				
				updateTipBasedOnTimeYouWaited();
				
				timeWaitingChronometer.start();
			}
		});
		
		pauseChronometerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				timeWaitingChronometer.stop();
			}
		});
		
		resetChronometerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				timeWaitingChronometer.setBase(SystemClock.elapsedRealtime());
				
				secondsYouWaited = 0;
			}
		});
	}
	
	private void updateTipBasedOnTimeYouWaited() {
		
		checklistValues[9] = (secondsYouWaited > 10)?-2:2;
		
		setTipFromWaitressCheckList();
		
		updateTipAndFinalBill();
	}
	
	
	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.crazy_tip_calc, menu);
		return true;
	}


	/**
	 * This method is called whenever anything changes (app closes; screen rotates, etc...)
	 */
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putDouble(BILL_WITHOUT_TIP, billBeforeTip);
		outState.putDouble(CURRENT_TIP, tipAmount);
		outState.putDouble(TOTAL_BILL, finalBill);
	}
	
}
