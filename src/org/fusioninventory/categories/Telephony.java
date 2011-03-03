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

		this.content.put("SIM_COUNTRY", mTM.getSimCountryIso());
		this.content.put("SIM_OPERATOR_CODE", mTM.getSimOperator());
		this.content.put("SIM_OPERATOR_NAME", mTM.getSimOperatorName());

	}
}
