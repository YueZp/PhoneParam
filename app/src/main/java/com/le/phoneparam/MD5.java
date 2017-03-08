package com.le.phoneparam;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by yuezp on 17/3/8.
 */

public class MD5 {
    public MD5() {
    }

    public static String toMd5(String md5Str) {
        String result = "";

        try {
            MessageDigest var4 = MessageDigest.getInstance("MD5");
            var4.reset();
            var4.update(md5Str.getBytes("utf-8"));
            result = toHexString(var4.digest());
        } catch (NoSuchAlgorithmException var3) {
            throw new RuntimeException(var3);
        } catch (UnsupportedEncodingException var41) {
            var41.printStackTrace();
        }

        return result;
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        byte[] var5 = bytes;
        int var4 = bytes.length;

        for(int var3 = 0; var3 < var4; ++var3) {
            int b = var5[var3];
            if(b < 0) {
                b += 256;
            }

            if(b < 16) {
                hexString.append("0");
            }

            hexString.append(Integer.toHexString(b));
        }

        return hexString.toString();
    }
}
