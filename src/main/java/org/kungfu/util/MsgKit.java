package org.kungfu.util;

import org.kungfu.core.Constants;

public class MsgKit {

	public static String message(Object flag, String type) {
		return Constants.SUCCESS_MASSAGE.equals(type) ? 
				(flag == null ? Constants.SUCCESS_SAVE  : Constants.SUCCESS_UPDATE) :
				(flag == null ? Constants.ERROR_SAVE  : Constants.ERROR_UPDATE);
	}
}
