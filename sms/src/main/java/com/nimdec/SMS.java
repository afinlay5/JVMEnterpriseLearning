package com.nimdec;

import static java.lang.System.out;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public final class SMS {

    // adf5152 google account

    // Find your Account Sid and Token at console.twilio.com
    public static final String ACCOUNT_SID = "AC2a50e10ee8ca94234f76d783cf9d7afa";
    public static final String AUTH_TOKEN = "07533db7366e8fcf04f9ae77d89789f6";

    private static final PhoneNumber fromNumber = new PhoneNumber("+19543200764");
//    private static final PhoneNumber fromNumber = new PhoneNumber("+18777804236");


//    private static final PhoneNumber toNumber = new PhoneNumber("+18446974140");
//    private static final PhoneNumber toNumber = new PhoneNumber("+13059301122");
    private static final PhoneNumber toNumber = new PhoneNumber("+19546091569");
//    private static final PhoneNumber toNumber = new PhoneNumber("+19142698239");

    private static final String textMessageBody = "Hello, Sarah, from Adrian's PC ;)";

    public static void main(String[] args) {

        try {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            // to then from
            var message = Message.creator(toNumber, fromNumber, textMessageBody)
                    .create();

            out.println(message.getSid());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}