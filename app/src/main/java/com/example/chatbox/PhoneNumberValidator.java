package com.example.chatbox;

import android.util.Log;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
public class PhoneNumberValidator {
    public static boolean isValidPhoneNumber(String phoneNumber, String defaultRegion) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        try {
            com.google.i18n.phonenumbers.Phonenumber.PhoneNumber parsedNumber = phoneUtil.parse(phoneNumber, defaultRegion);
            return phoneUtil.isValidNumber(parsedNumber);
        } catch (NumberParseException e) {
            Log.e("PhoneNumberValidator", "Error parsing phone number", e);
            return false;
        }
    }
}
