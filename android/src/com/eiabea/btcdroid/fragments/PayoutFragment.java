package com.eiabea.btcdroid.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Profile;

public class PayoutFragment extends Fragment {

	private Profile profile;
	private View rootView;
	private TextView txtBillingCycle, txtLastActivity, txtTotalCosts;
	private ProgressBar prgGauge;
	private LinearLayout llTotalCostsHolder;
	private TextView txtMax, txtUsed;

	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {

		this.rootView = inflater.inflate(R.layout.fragment_payout, root, false);

		initUi();

		setListeners();

		return rootView;

	}

	private void initUi() {
		prgGauge = (ProgressBar) rootView.findViewById(R.id.prg_data_gauge);
	}

	private void setListeners() {
	}

	

//	public static PayoutFragment newInstance() {
//
//		PayoutFragment fragment = new PayoutFragment();
//		Bundle bundle = new Bundle();
////		bundle.putParcelable(MyCostsActivity.PARAM_CCI_DTL, cciDtl);
//		fragment.setArguments(bundle);
//		return fragment;
//
//	}
	
	public static PayoutFragment create(int pageNumber) {
		PayoutFragment fragment = new PayoutFragment();
		Bundle b = new Bundle();
		fragment.setArguments(b);
		return fragment;
	}

	public void setProfile(Profile profile) {
		// TODO Auto-generated method stub
		this.profile = profile;
		setGauge(profile);
	}

	private void setGauge(Profile profile) {
		try{
			int max = 1000;
			int offset = 48;
			
			float sendThreshold = Float.valueOf(profile.getSend_threshold());
			float confirmed = Float.valueOf(profile.getConfirmed_reward());
			float unconfirmed = Float.valueOf(profile.getUnconfirmed_reward());
			
			int confirmedProgress = (int) ((confirmed / sendThreshold ) * max);
			int unconfirmedProgress = (int) ((unconfirmed / sendThreshold ) * max);
			int total = unconfirmedProgress + confirmedProgress;
			
			confirmedProgress += offset;
			total += offset;
			
			prgGauge.setMax(max + (2 * offset));
			prgGauge.setProgress(total);
			prgGauge.setSecondaryProgress(confirmedProgress);
//			
//			txtUsed.setText(String.format(getString(R.string.txt_progessbar_used_data), fuc.getUsedFormatted(), fuc.getUnit().toLowerCase(Locale.GERMAN)));
//			txtMax.setText(String.format(getString(R.string.txt_progessbar_max_data_with_unit), fuc.getMaxFormatted(), fuc.getUnit().toLowerCase(Locale.GERMAN)));
			
		} catch (NullPointerException e){
			e.printStackTrace();
		}
	}

}
