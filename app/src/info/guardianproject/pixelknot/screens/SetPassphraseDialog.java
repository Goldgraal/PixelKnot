package info.guardianproject.pixelknot.screens;

import com.actionbarsherlock.app.SherlockFragment;

import info.guardianproject.pixelknot.Constants;
import info.guardianproject.pixelknot.R;
import info.guardianproject.pixelknot.utils.PassphraseDialogListener;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public abstract class SetPassphraseDialog {
	static int num_tries = 0;
	
	@SuppressLint("InflateParams") 
	public static AlertDialog getDialog(final SherlockFragment a, final String passphrase) {
		View passphrase_dialog = a.getActivity().getLayoutInflater().inflate(R.layout.set_passphrase_dialog, null);
		ImageButton generate_random_passphrase = (ImageButton) passphrase_dialog.findViewById(R.id.generate_random_passphrase);
		ImageButton clear_passphrase = (ImageButton) passphrase_dialog.findViewById(R.id.clear_passphrase);
		
		final TextView passphrase_monitor = (TextView) passphrase_dialog.findViewById(R.id.passphrase_monitor);
		final EditText passphrase_holder = (EditText) passphrase_dialog.findViewById(R.id.passphrase_holder);
		
		if(passphrase != null) {
			passphrase_holder.setText(passphrase);
		}
		
		final String passphrase_length_string = a.getString(R.string.password_monitor);
		passphrase_monitor.setText(String.format(passphrase_length_string, passphrase == null ? 0 : passphrase.length()));
		
		generate_random_passphrase.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((PassphraseDialogListener) a).onRandomPassphraseRequested();
			}
		});
		
		clear_passphrase.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				passphrase_holder.setText("");
			}
		});
		
		TextWatcher monitor_passphrase_length = new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				passphrase_monitor.setText(String.format(passphrase_length_string, passphrase_holder.getText().length()));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
		};
		
		passphrase_holder.addTextChangedListener(monitor_passphrase_length);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(a.getActivity());
		
		builder.setView(passphrase_dialog);
		builder.setPositiveButton(a.getActivity().getString(R.string.set), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(passphrase_holder.getText().length() < Constants.PASSPHRASE_MIN_LENGTH) {
					num_tries++;
					
					if(num_tries > 4) {
						Toast.makeText(a.getActivity(), a.getResources().getString(R.string.password_generating), Toast.LENGTH_LONG).show();
						((PassphraseDialogListener) a).onRandomPassphraseRequested();
					} else {
						Toast.makeText(a.getActivity(), a.getResources().getString(R.string.password_too_short), Toast.LENGTH_SHORT).show();
					}
					
					return;
				}
				
				((PassphraseDialogListener) a).onPassphraseSuccessfullySet(passphrase_holder.getText().toString());
			}
		});
		
		return builder.create();
	}

}