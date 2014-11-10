package sv.cmu.edu.ips.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Editable;
import android.widget.EditText;

public class UserInputManager {
	
	public void getLabel(Context context, String message, final Callback callback) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);

		alert.setTitle("Sound Recognizer");
		alert.setMessage(message);

		// Set an EditText view to get user input
		final EditText input = new EditText(context);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Editable value = input.getText();
				
				Message msg = new Message();
				msg.obj = value.toString();
				
				callback.handleMessage(msg);
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

		alert.show();
	}

}
