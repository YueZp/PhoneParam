package com.le.phoneparam;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

/**
 * Created by yuezp on 17/3/8.
 */
public class MUtils {
    public static int getNumCores() {
        try {
            File e = new File("/sys/devices/system/cpu/");
            class CpuFilter implements FileFilter {
                CpuFilter() {
                }

                public boolean accept(File pathname) {
                    return Pattern.matches("cpu[0-9]", pathname.getName());
                }
            }

            File[] files = e.listFiles(new CpuFilter());
            return files.length;
        } catch (Exception var2) {
            var2.printStackTrace();
            return 1;
        }
    }

    public static float getCurCpuFrequence() {
        try {
            String[] ex = new String[]{"/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq"};
            ProcessBuilder cmd = new ProcessBuilder(ex);
            Process process = cmd.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            return line != null ? (float) Long.parseLong(line) / 1000000.0F : 0.0F;
        } catch (Exception var5) {
            var5.printStackTrace();
            Log.i("MUtils", "获取cpu当前主频发生错误。");
            return getMaxCpuFrequence();
        }
    }

    public static float getMaxCpuFrequence() {
        long maxFreq = -1L;

        try {
            String str2;
            for (int str1 = 0; str1 < getNumCores(); ++str1) {
                str2 = "/sys/devices/system/cpu/cpu" + str1 + "/cpufreq/cpuinfo_max_freq";
                File fr = new File(str2);
                if (fr.exists()) {
                    byte[] localBufferedReader = new byte[128];
                    FileInputStream strs = new FileInputStream(fr);

                    try {
                        strs.read(localBufferedReader);

                        int freqBound;
                        for (freqBound = 0; localBufferedReader[freqBound] >= 48 && localBufferedReader[freqBound] <= 57 && freqBound < localBufferedReader.length; ++freqBound) {
                            ;
                        }

                        String str = new String(localBufferedReader, 0, freqBound);
                        Integer freqBound1 = Integer.valueOf(Integer.parseInt(str));
                        if ((long) freqBound1.intValue() > maxFreq) {
                            maxFreq = (long) freqBound1.intValue();
                        }
                    } catch (NumberFormatException var16) {
                        ;
                    } finally {
                        strs.close();
                    }
                }
            }

            if (maxFreq == -1L) {
                String var19 = "";
                str2 = "";

                try {
                    FileReader var20 = new FileReader("/proc/cpuinfo");
                    BufferedReader var21 = new BufferedReader(var20);

                    while ((var19 = var21.readLine()) != null) {
                        if (var19.contains("cpu MHz")) {
                            str2 = var19.split(":")[1];
                            if (str2.contains(" ")) {
                                String[] var22 = str2.split(" ");
                                Integer var23 = Integer.valueOf(Integer.parseInt(var22[var22.length - 1]));
                                var23 = Integer.valueOf(var23.intValue() * 1000);
                                if ((long) var23.intValue() > maxFreq) {
                                    maxFreq = (long) var23.intValue();
                                }
                            }
                        }
                    }

                    var21.close();
                } catch (IOException var15) {
                    ;
                }
            }
        } catch (IOException var18) {
            ;
        }

        return (float) maxFreq / 1000000.0F;
    }

    public static String getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuInfo = new String[]{"", ""};

        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            String[] arrayOfString = str2.split("\\s+");

            for (int i = 2; i < arrayOfString.length; ++i) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }

            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] = cpuInfo[1] + arrayOfString[2];
            localBufferedReader.close();
        } catch (IOException var7) {
            ;
        }
        return cpuInfo[0].toString();
    }


    public static String getOSVersionName() {
        return Build.VERSION.RELEASE;
    }


    public static String getDeviceName() {
        String model = Build.MODEL;
        return model != null && model.length() > 0 ? model : "";
    }

    public static String getBrandName() {
        String brand = Build.BRAND;
        return brand != null && brand.length() > 0 ? brand : "";
    }


    public static String getVersionName(Context context) {
        try {
            PackageManager e = context.getPackageManager();
            PackageInfo info = e.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception var3) {
            var3.printStackTrace();
            return "";
        }
    }


    public static int getVersionCode(Context context) {
        try {
            PackageManager e = context.getPackageManager();
            PackageInfo info = e.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception var3) {
            var3.printStackTrace();
            return -1;
        }
    }

    public static String generateDeviceId(Context context) {
        String str = getIMEI(context) + getIMSI(context) + getDeviceName() + getBrandName() + getMacAddress(context);
        return MD5.toMd5(str);
    }

    public static String generate_DeviceId(Context context) {
        String str = getIMEI(context) + getDeviceName() + getBrandName() + getMacAddress(context);
        return MD5.toMd5(str);
    }

    public static String getIMEI(Context context) {
        if (context == null) {
            return "";
        } else {
            try {
                String e = ((TelephonyManager) context.getSystemService("phone")).getDeviceId();
                return null != e && e.length() > 0 ? e.replace(" ", "") : "";
            } catch (Exception var2) {
                var2.printStackTrace();
                return "";
            }
        }

    }

    public static String getIMSI(Context context) {
        if (context == null) {
            return "";
        } else {
            String subscriberId = null;

            try {
                subscriberId = ((TelephonyManager) context.getSystemService("phone")).getSubscriberId();
                if (null != subscriberId && subscriberId.length() > 0) {
                    subscriberId.replace(" ", "");
                    if (TextUtils.isEmpty(subscriberId)) {
                        subscriberId = generate_DeviceId(context);
                    }
                } else {
                    subscriberId = generate_DeviceId(context);
                }

                return subscriberId;
            } catch (Exception var3) {
                var3.printStackTrace();
                return subscriberId;
            }
        }
    }

    public static String getMacAddress(Context context) {
        if (context == null) {
            return "";
        } else {
            try {
                String e = null;
                WifiInfo wifiInfo = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo();
                e = wifiInfo.getMacAddress();
                return e != null && e.length() > 0 ? e : "";
            } catch (Exception var3) {
                var3.printStackTrace();
                return "";
            }
        }
    }

    private static int decodeProfile = -1;
    private static final int HIGH_PROFILE_VALUE = 16;
    private static final int ANDROID_OS_API_LEVEL_HW_LIMIT = 16;

    public static boolean isSupportHWDecodeUseNative() {
        Log.d("lxb", "isSupport");
        int get = getProfileUseNative();
        Log.d("lxb", "get=" + get);
        return get >= 16;
    }

    public static int getProfileUseNative() {
        Log.d("lxb", "decodeProfile=" + decodeProfile);
        if (decodeProfile == -1) {
            decodeProfile = getProfile();
        }

        return decodeProfile;
    }

    public static int getProfile() {
        int maxProfile = 0;
        Log.d("MUtils", "getProfile()->Build.VERSION.SDK_INT:" + String.valueOf(Build.VERSION.SDK_INT));
        if (Build.VERSION.SDK_INT < 16) {
            return maxProfile;
        } else {
            int mediaCodecListCount = MediaCodecList.getCodecCount();

            for (int i = 0; i < mediaCodecListCount; ++i) {
                MediaCodecInfo mediaCodecInfo = MediaCodecList.getCodecInfoAt(i);
                if (!mediaCodecInfo.isEncoder() && !mediaCodecInfo.getName().startsWith("OMX.google") && !mediaCodecInfo.getName().startsWith("OMX.TI.")) {
                    Log.d("MUtils", "getProfile()->name:" + mediaCodecInfo.getName());
                    String[] var4 = mediaCodecInfo.getSupportedTypes();
                    int var5 = var4.length;

                    for (int var6 = 0; var6 < var5; ++var6) {
                        String type = var4[var6];
                        if (type.contains("avc")) {
                            Log.d("MUtils", "getProfile()->type:" + type);
                            MediaCodecInfo.CodecCapabilities codecCapabilities = null;

                            try {
                                codecCapabilities = mediaCodecInfo.getCapabilitiesForType(type);
                            } catch (Exception var17) {
                                var17.printStackTrace();
                                continue;
                            } finally {
                                ;
                            }

                            int[] e = codecCapabilities.colorFormats;
                            int var10 = e.length;

                            int var11;
                            for (var11 = 0; var11 < var10; ++var11) {
                                int codecProfileLevel = e[var11];
                                Log.d("MUtils", "getProfile()->Color Format: " + codecProfileLevel + " " + colorFormatToString(codecProfileLevel));
                            }

                            MediaCodecInfo.CodecProfileLevel[] var19 = codecCapabilities.profileLevels;
                            var10 = var19.length;

                            for (var11 = 0; var11 < var10; ++var11) {
                                MediaCodecInfo.CodecProfileLevel var20 = var19[var11];
                                String level = "unknown type";
                                String sprofile = "unknown type";
                                level = avcLevelToString(var20.level);
                                sprofile = avcProfileToString(var20.profile);
                                Log.d("MUtils", "getProfile()->Codec Profile Level:" + level + " profile:" + sprofile);
                                if (var20.profile > maxProfile) {
                                    maxProfile = var20.profile;
                                }
                            }
                        }
                    }
                }
            }

            Log.d("MUtils", "getProfile()->Max profile:" + maxProfile + " " + avcProfileToString(maxProfile));
            return maxProfile;
        }
    }

    public static int getCapbility() {
        int maxProfile = 0;
        byte tsType = -1;
        Log.d("MUtils", "getCapbility()->Build.VERSION.SDK_INT:" + String.valueOf(Build.VERSION.SDK_INT));
        if(Build.VERSION.SDK_INT < 16) {
            return tsType;
        } else {
            int mediaCodecListCount = MediaCodecList.getCodecCount();

            for(int i = 0; i < mediaCodecListCount; ++i) {
                MediaCodecInfo mediaCodecInfo = MediaCodecList.getCodecInfoAt(i);
                if(!mediaCodecInfo.isEncoder() && !mediaCodecInfo.getName().startsWith("OMX.google") && !mediaCodecInfo.getName().startsWith("OMX.TI.")) {
                    Log.d("MUtils", "getCapbility()->name:" + mediaCodecInfo.getName());
                    String[] var5 = mediaCodecInfo.getSupportedTypes();
                    int var6 = var5.length;

                    for(int var7 = 0; var7 < var6; ++var7) {
                        String type = var5[var7];
                        if(type.contains("avc")) {
                            Log.d("MUtils", "getCapbility()->type:" + type);
                            MediaCodecInfo.CodecCapabilities codecCapabilities = null;

                            try {
                                codecCapabilities = mediaCodecInfo.getCapabilitiesForType(type);
                            } catch (Exception var18) {
                                var18.printStackTrace();
                                continue;
                            } finally {
                                ;
                            }

                            int[] e = codecCapabilities.colorFormats;
                            int var11 = e.length;

                            int var12;
                            for(var12 = 0; var12 < var11; ++var12) {
                                int codecProfileLevel = e[var12];
                                Log.d("MUtils", "getCapbility()->Color Format: " + codecProfileLevel + " " + colorFormatToString(codecProfileLevel));
                            }

                            MediaCodecInfo.CodecProfileLevel[] var20 = codecCapabilities.profileLevels;
                            var11 = var20.length;

                            for(var12 = 0; var12 < var11; ++var12) {
                                MediaCodecInfo.CodecProfileLevel var21 = var20[var12];
                                String level = "unknown type";
                                String sprofile = "unknown type";
                                level = avcLevelToString(var21.level);
                                sprofile = avcProfileToString(var21.profile);
                                Log.d("MUtils", "getCapbility()->Codec Profile Level:" + level + " profile:" + sprofile);
                                if(var21.profile > maxProfile) {
                                    maxProfile = var21.profile;
                                }
                            }
                        }
                    }
                }
            }

            Log.d("MUtils", "getCapbility()->Max profile:" + maxProfile + " " + avcProfileToString(maxProfile));
            if(maxProfile >= 8) {
                tsType = 16;
            }

            return tsType;
        }
    }

    public static int getAVCLevel() {
        int maxAVCLevel = 0;
        if (Build.VERSION.SDK_INT < 16) {
            return maxAVCLevel;
        } else {
            int mediaCodecListCount = MediaCodecList.getCodecCount();

            for (int i = 0; i < mediaCodecListCount; ++i) {
                MediaCodecInfo mediaCodecInfo = MediaCodecList.getCodecInfoAt(i);
                if (!mediaCodecInfo.isEncoder() && !mediaCodecInfo.getName().startsWith("OMX.google") && !mediaCodecInfo.getName().startsWith("OMX.TI.")) {
                    Log.d("MUtils", "getAVCLevel()->name:" + mediaCodecInfo.getName());
                    String[] var4 = mediaCodecInfo.getSupportedTypes();
                    int var5 = var4.length;

                    for (int var6 = 0; var6 < var5; ++var6) {
                        String type = var4[var6];
                        if (type.contains("avc")) {
                            Log.d("MUtils", "getAVCLevel()->type:" + type);
                            MediaCodecInfo.CodecCapabilities codecCapabilities = null;

                            try {
                                codecCapabilities = mediaCodecInfo.getCapabilitiesForType(type);
                            } catch (Exception var17) {
                                var17.printStackTrace();
                                continue;
                            } finally {
                                ;
                            }

                            int[] e = codecCapabilities.colorFormats;
                            int var10 = e.length;

                            int var11;
                            for (var11 = 0; var11 < var10; ++var11) {
                                int codecProfileLevel = e[var11];
                                Log.d("MUtils", "getAVCLevel()->Color Format: " + codecProfileLevel + " " + colorFormatToString(codecProfileLevel));
                            }

                            MediaCodecInfo.CodecProfileLevel[] var19 = codecCapabilities.profileLevels;
                            var10 = var19.length;

                            for (var11 = 0; var11 < var10; ++var11) {
                                MediaCodecInfo.CodecProfileLevel var20 = var19[var11];
                                String level = "unknown type";
                                String sprofile = "unknown type";
                                level = avcLevelToString(var20.level);
                                sprofile = avcProfileToString(var20.profile);
                                Log.d("MUtils", "getAVCLevel()->Codec Profile Level:" + level + " profile:" + sprofile);
                                if (var20.level > maxAVCLevel) {
                                    maxAVCLevel = var20.level;
                                }
                            }
                        }
                    }
                }
            }

            Log.d("MUtils", "getAVCLevel()->Max AVCLevel:" + maxAVCLevel + " " + avcProfileToString(maxAVCLevel));
            return maxAVCLevel;
        }
    }

    private static String colorFormatToString(int colorFormat) {
        String ret = "not found(" + colorFormat + ")";
        switch (colorFormat) {
            case 1:
                ret = "COLOR_FormatMonochrome";
                break;
            case 2:
                ret = "COLOR_Format8bitRGB332";
                break;
            case 3:
                ret = "COLOR_Format12bitRGB444";
                break;
            case 4:
                ret = "COLOR_Format16bitARGB4444";
                break;
            case 5:
                ret = "COLOR_Format16bitARGB1555";
                break;
            case 6:
                ret = "COLOR_Format16bitRGB565";
                break;
            case 7:
                ret = "COLOR_Format16bitBGR565";
                break;
            case 8:
                ret = "COLOR_Format18bitRGB666";
                break;
            case 9:
                ret = "COLOR_Format18bitARGB1665";
                break;
            case 10:
                ret = "COLOR_Format19bitARGB1666";
                break;
            case 11:
                ret = "COLOR_Format24bitRGB888";
                break;
            case 12:
                ret = "COLOR_Format24bitBGR888";
                break;
            case 13:
                ret = "COLOR_Format24bitARGB1887";
                break;
            case 14:
                ret = "COLOR_Format25bitARGB1888";
                break;
            case 15:
                ret = "COLOR_Format32bitBGRA8888";
                break;
            case 16:
                ret = "COLOR_Format32bitARGB8888";
                break;
            case 17:
                ret = "COLOR_FormatYUV411Planar";
                break;
            case 18:
                ret = "COLOR_FormatYUV411PackedPlanar";
                break;
            case 19:
                ret = "COLOR_FormatYUV420Planar";
                break;
            case 20:
                ret = "COLOR_FormatYUV420PackedPlanar";
                break;
            case 21:
                ret = "COLOR_FormatYUV420SemiPlanar";
                break;
            case 22:
                ret = "COLOR_FormatYUV422Planar";
                break;
            case 23:
                ret = "COLOR_FormatYUV422PackedPlanar";
                break;
            case 24:
                ret = "COLOR_FormatYUV422SemiPlanar";
                break;
            case 25:
                ret = "COLOR_FormatYCbYCr";
                break;
            case 26:
                ret = "COLOR_FormatYCrYCb";
                break;
            case 27:
                ret = "COLOR_FormatCbYCrY";
                break;
            case 28:
                ret = "COLOR_FormatCrYCbY";
                break;
            case 29:
                ret = "COLOR_FormatYUV444Interleaved";
                break;
            case 30:
                ret = "COLOR_FormatRawBayer8bit";
                break;
            case 31:
                ret = "COLOR_FormatRawBayer10bit";
                break;
            case 32:
                ret = "COLOR_FormatRawBayer8bitcompressed";
                break;
            case 33:
                ret = "COLOR_FormatL2";
                break;
            case 34:
                ret = "COLOR_FormatL4";
                break;
            case 35:
                ret = "COLOR_FormatL8";
                break;
            case 36:
                ret = "COLOR_FormatL16";
                break;
            case 37:
                ret = "COLOR_FormatL24";
                break;
            case 38:
                ret = "COLOR_FormatL32";
                break;
            case 39:
                ret = "COLOR_FormatYUV420PackedSemiPlanar";
                break;
            case 40:
                ret = "COLOR_FormatYUV422PackedSemiPlanar";
                break;
            case 41:
                ret = "COLOR_Format18BitBGR666";
                break;
            case 42:
                ret = "COLOR_Format24BitARGB6666";
                break;
            case 43:
                ret = "COLOR_Format24BitABGR6666";
                break;
            case 2130706688:
                ret = "COLOR_TI_FormatYUV420PackedSemiPlanar";
                break;
            case 2141391872:
                ret = "COLOR_QCOM_FormatYUV420SemiPlanar";
        }

        return ret;
    }

    private static String avcProfileToString(int profile) {
        String ret = "not found(" + profile + ")";
        switch (profile) {
            case 1:
                ret = "AVCProfileBaseline";
                break;
            case 2:
                ret = "AVCProfileMain";
                break;
            case 4:
                ret = "AVCProfileExtended";
                break;
            case 8:
                ret = "AVCProfileHigh";
                break;
            case 16:
                ret = "AVCProfileHigh10";
                break;
            case 32:
                ret = "AVCProfileHigh422";
                break;
            case 64:
                ret = "AVCProfileHigh444";
        }

        return ret;
    }

    private static String avcLevelToString(int level) {
        String ret = "not found(" + level + ")";
        switch (level) {
            case 1:
                ret = "AVCLevel1";
                break;
            case 2:
                ret = "AVCLevel1b";
                break;
            case 4:
                ret = "AVCLevel11";
                break;
            case 8:
                ret = "AVCLevel12";
                break;
            case 16:
                ret = "AVCLevel13";
                break;
            case 32:
                ret = "AVCLevel2";
                break;
            case 64:
                ret = "AVCLevel21";
                break;
            case 128:
                ret = "AVCLevel22";
                break;
            case 256:
                ret = "AVCLevel3";
                break;
            case 512:
                ret = "AVCLevel31";
                break;
            case 1024:
                ret = "AVCLevel32";
                break;
            case 2048:
                ret = "AVCLevel4";
                break;
            case 4096:
                ret = "AVCLevel41";
                break;
            case 8192:
                ret = "AVCLevel42";
                break;
            case 16384:
                ret = "AVCLevel5";
                break;
            case 32768:
                ret = "AVCLevel51";
        }

        return ret;
    }

}
