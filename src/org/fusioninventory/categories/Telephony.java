package org.fusioninventory.categories;

import android.content.Context;
import android.telephony.TelephonyManager;

public class Telephony extends Category {

	public Telephony(Context ctx) {
		super(ctx);
		this.type = "TELEPHONY";
		final TelephonyManager mTM = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		
		/*
		 * Starting Telephony Informations pull
		 */

		this.content.put("DEVICEID",mTM.getDeviceId());
		this.content.put("SOFTWAREVERSION",mTM.getDeviceSoftwareVersion());
		this.content.put("SIMCOUNTRY", mTM.getSimCountryIso());
		this.content.put("SIMOPERATORCODE", mTM.getSimOperator());
		this.content.put("SIMOPERATORNAME", mTM.getSimOperatorName());

	}
}
