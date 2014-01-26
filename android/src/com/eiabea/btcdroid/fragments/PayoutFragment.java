package com.eiabea.btcdroid.fragments;

import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eiabea.btcdroid.R;

public class PayoutFragment extends Fragment {

	private View rootView;
	private TextView txtBillingCycle, txtLastActivity, txtTotalCosts;
	private ProgressBar prgGauge;
	private LinearLayout llTotalCostsHolder;
	private TextView txtMax, txtUsed;

	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {

		this.rootView = inflater.inflate(R.layout.fragment_payout, root, false);

		initUi();

		setListeners();

		fillUp();

		return rootView;

	}

	private void initUi() {
		prgGauge = (ProgressBar) rootView.findViewById(R.id.prg_data_gauge);
	}

	private void setListeners() {
	}

	private void fillUp() {
		setGauge();
	}


	
	private void setGauge(){
		try{
			
//			prgGauge.setMax(fuc.getMax() * 100);
//			prgGauge.setProgress((int) (fuc.getUsed() * 100f));
//			
//			txtUsed.setText(String.format(getString(R.string.txt_progessbar_used_data), fuc.getUsedFormatted(), fuc.getUnit().toLowerCase(Locale.GERMAN)));
//			txtMax.setText(String.format(getString(R.string.txt_progessbar_max_data_with_unit), fuc.getMaxFormatted(), fuc.getUnit().toLowerCase(Locale.GERMAN)));
			
		} catch (NullPointerException e){
			e.printStackTrace();
		}
	}

	public static PayoutFragment newInstance() {

		PayoutFragment fragment = new PayoutFragment();
		Bundle bundle = new Bundle();
//		bundle.putParcelable(MyCostsActivity.PARAM_CCI_DTL, cciDtl);
		fragment.setArguments(bundle);
		return fragment;

	}

}
