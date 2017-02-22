package com.xzy.cm.common.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class SHA256 {
    /**
     * 加密
     * @param strSrc
     * @param encName
     * @return
     */
    public static String encrypt(String strSrc, String encName) {
        MessageDigest md = null;
        String strDes = null;
        byte[] bt = strSrc.getBytes();

        try {
            if(encName == null || encName.equals("")) {
                encName = "SHA-256";
            }

            md = MessageDigest.getInstance(encName);
            md.update(bt);
            strDes = bytes2Hex(md.digest());
            return strDes;
        } catch (NoSuchAlgorithmException var6) {
            return null;
        }
    }

    private static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;

        for(int i = 0; i < bts.length; ++i) {
            tmp = Integer.toHexString(bts[i] & 255);
            if(tmp.length() == 1) {
                des = des + "0";
            }

            des = des + tmp;
        }

        return des;
    }

    /**
     * 对比
     * @param a
     * @param b
     * @return
     */
    public static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;

        for(int i = 0; i < a.length && i < b.length; ++i) {
            diff |= a[i] ^ b[i];
        }

        return 0 == diff;
    }

    /**
     * 获取指定长度的安全随机数
     * @param stringLength
     * @return
     */
    public static String getSecureRamdon(int stringLength) {
        StringBuffer result = new StringBuffer();
        byte[] bytes = new byte[stringLength];

        Random ranGen = new SecureRandom();
        ranGen.nextBytes(bytes);
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xff & bytes[i]);
            if (hex.length() == 1)
                result.append('0');
            result.append(hex);
        }

        return result.toString();
    }

}
