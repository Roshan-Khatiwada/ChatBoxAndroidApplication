package com.example.chatbox;

import android.util.Log;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

/**
 * Utility class to validate phone numbers using Google's libphonenumber library.
 */
public class PhoneNumberValidator {

    /**
     * Validates the given phone number.
     *
     * @param phoneNumber   The phone number to validate.
     * @param defaultRegion The default region for parsing the phone number.
     * @return True if the phone number is valid, false otherwise.
     */
    public static boolean isValidPhoneNumber(String phoneNumber, String defaultRegion) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        try {
            com.google.i18n.phonenumbers.Phonenumber.PhoneNumber parsedNumber = phoneUtil.parse(phoneNumber, defaultRegion);
            return phoneUtil.isValidNumber(parsedNumber);
        } catch (NumberParseException e) {
            // Log the error if parsing fails
            Log.e("PhoneNumberValidator", "Error parsing phone number", e);
            return false;
        }
    }
}
