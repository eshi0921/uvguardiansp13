package com.example.navidration;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;

public class FountainPrompt extends DialogFragment {
	
	public interface FountainPromptListener {
		public void onDialogPositiveClick(FountainPrompt dialog);
		public void onDialogNegativeClick(FountainPrompt dialog);
	}
	
	String rating = "Y";
	FountainPromptListener mListener;
	
	static FountainPrompt newInstance(String title, String positive, int fid) {
		FountainPrompt p = new FountainPrompt();
		
		Bundle args = new Bundle();
		args.putString("positive", positive);
		args.putString("title", title);
		args.putInt("fid", fid);
		p.setArguments(args);
		
		return p;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		View view = inflater.inflate(R.layout.fountain_modify, null);
		
		if (getArguments().getString("title").equals(getString(R.string.rate_fountain)))
			view.findViewById(R.id.additional).setVisibility(View.GONE);

		builder.setView(view)
			   .setTitle(getArguments().getString("title"))
			   .setPositiveButton(getArguments().getString("positive"), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mListener.onDialogPositiveClick(FountainPrompt.this);
					}
			   })
			   .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {				
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mListener.onDialogNegativeClick(FountainPrompt.this);
					}
			   });
		
		RadioGroup rg = (RadioGroup) view.findViewById(R.id.radio_group);
		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(RadioGroup rg, int checkedId) {
				rating = (checkedId == 0) ? "Y" : "N";
			}
		});
		return builder.create();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (FountainPromptListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement FountainPromptListener");
		}
	}
	
	public int getFid() {
		return getArguments().getInt("fid");
	}
}
