package ca.rldesigns.android.casa;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class AbstractMapActivity extends FragmentActivity {
	protected static final String TAG_ERROR_DIALOG_FRAGMENT = "errorDialog";

	protected boolean readyToGo() {
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		if (status == ConnectionResult.SUCCESS) {
			if (getVersionFromPackageManager(this) >= 2) {
				return (true);
			} else {
				Toast.makeText(this, R.string.no_maps, Toast.LENGTH_LONG).show();
				finish();
			}
		} else if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
			ErrorDialogFragment.newInstance(status).show(getSupportFragmentManager(), TAG_ERROR_DIALOG_FRAGMENT);
		} else {
			Toast.makeText(this, R.string.no_maps, Toast.LENGTH_LONG).show();
			finish();
		}

		return (false);
	}

	public static class ErrorDialogFragment extends DialogFragment {
		static final String ARG_STATUS = "status";

		static ErrorDialogFragment newInstance(int status) {
			Bundle args = new Bundle();

			args.putInt(ARG_STATUS, status);

			ErrorDialogFragment result = new ErrorDialogFragment();

			result.setArguments(args);

			return (result);
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Bundle args = getArguments();

			return GooglePlayServicesUtil.getErrorDialog(args.getInt(ARG_STATUS), getActivity(), 0);
		}

		@Override
		public void onDismiss(DialogInterface dlg) {
			if (getActivity() != null) {
				getActivity().finish();
			}
		}
	}

	private static int getVersionFromPackageManager(Context context) {
		PackageManager packageManager = context.getPackageManager();
		FeatureInfo[] featureInfos = packageManager.getSystemAvailableFeatures();
		if (featureInfos != null && featureInfos.length > 0) {
			for (FeatureInfo featureInfo : featureInfos) {
				// Null feature name means this feature is the open
				// gl es version feature.
				if (featureInfo.name == null) {
					if (featureInfo.reqGlEsVersion != FeatureInfo.GL_ES_VERSION_UNDEFINED) {
						return getMajorVersion(featureInfo.reqGlEsVersion);
					} else {
						return 1; // Lack of property means OpenGL ES version 1
					}
				}
			}
		}
		return 1;
	}

	/** @see FeatureInfo#getGlEsVersion() */
	private static int getMajorVersion(int glEsVersion) {
		return ((glEsVersion & 0xffff0000) >> 16);
	}
}