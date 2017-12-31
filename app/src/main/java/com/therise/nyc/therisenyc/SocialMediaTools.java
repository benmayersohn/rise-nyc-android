package com.therise.nyc.therisenyc;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * SocialMediaTools: methods for doing FB/Insta/Twitter/Meetup stuff
 */

class SocialMediaTools {
    static String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { // newer versions of fb app
                return "fb://facewebmodal/f?href=" + GeneralStatic.FB_URL;
            } else { //older versions of fb app
                return "fb://page/" + GeneralStatic.FB_NAME;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return GeneralStatic.FB_URL; //normal web url
        }
    }

    static String getInstaURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        String url;
        try {
            packageManager.getPackageInfo("com.instagram.android", 0);
            url = "http://instagram.com/_u/" + GeneralStatic.INSTA_HANDLE;
        } catch(PackageManager.NameNotFoundException e) {
            url = "http://instagram.com/" + GeneralStatic.INSTA_HANDLE;
        }

        return url;
    }

    static String getTwitterURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        String url;
        try {
            packageManager.getPackageInfo("com.twitter.android", 0);
            url = "twitter://user?user_id=" + GeneralStatic.TWITTER_ID;
        } catch(PackageManager.NameNotFoundException e) {
            url = "https://twitter.com/" + GeneralStatic.TWITTER_HANDLE;
        }

        return url;
    }
}
