
package com.bj4.yhh.everyday.utils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.util.Log;

public class Utils {
    @SuppressWarnings("deprecation")
    public static String parseOnInternet(String url) {
        URL u;
        InputStream is = null;
        DataInputStream dis;
        String s;
        StringBuilder sb = new StringBuilder();
        try {
            u = new URL(url);
            is = u.openStream();
            dis = new DataInputStream(new BufferedInputStream(is));
            while ((s = dis.readLine()) != null) {
                sb.append(s);
            }
        } catch (Exception e) {
        } finally {
            try {
                is.close();
            } catch (Exception ioe) {
            }
        }
        return sb.toString();
    }
}
