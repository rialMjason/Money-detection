package com.moneydetection;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class NotificationListener extends NotificationListenerService {
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        if (packageName.equals("com.tngdigital.ewallet")) {
                // Parse notification and extract transaction details
                String notificationText = "";
                if (sbn.getNotification().extras != null) {
                    CharSequence cs = sbn.getNotification().extras.getCharSequence("android.text");
                    if (cs != null) {
                        notificationText = cs.toString();
                    }
                }

                // Example: parse amount and merchant from notification text
                // This is a simple regex, adjust as needed for actual TNG notification format
                String amount = "";
                String merchant = "";
                java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("RM([0-9]+\\.[0-9]{2})").matcher(notificationText);
                if (matcher.find()) {
                    amount = matcher.group(1);
                }
                // Example merchant extraction (adjust as needed)
                int merchantIdx = notificationText.indexOf("at ");
                if (merchantIdx != -1) {
                    merchant = notificationText.substring(merchantIdx + 3).split("\\s")[0];
                }

                Log.d("TNGNotification", "Detected TNG notification: Amount=" + amount + ", Merchant=" + merchant + ", Text=" + notificationText);
                    // Send broadcast to MainActivity to prompt for category
                    android.content.Intent intent = new android.content.Intent("com.moneydetection.TNG_TRANSACTION");
                    intent.putExtra("amount", amount);
                    intent.putExtra("merchant", merchant);
                    intent.putExtra("text", notificationText);
                    sendBroadcast(intent);
        }
    }
}
