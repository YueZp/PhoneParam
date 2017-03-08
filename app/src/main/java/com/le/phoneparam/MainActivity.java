package com.le.phoneparam;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private android.widget.TextView devices;
    private android.widget.TextView appversion;
    private android.widget.TextView phoneversion;
    private android.widget.TextView sdkversion;
    private android.widget.TextView cputype;
    private android.widget.TextView cpukernel;
    private android.widget.TextView cpufrequency;
    private android.widget.TextView avclevel;
    private android.widget.TextView decodeprofile;
    private android.widget.TextView deviceid;
    private android.widget.Button printlog;


    private String sDevices;
    private String sAppversion;
    private String sPhoneversion;
    private String sSDKversion;
    private String sCPUtype;
    private String sCPUKernel;
    private String sCPUFrequency;
    private String sAvcLevel;
    private String sDecodeprofile;
    private String sDeviceid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        this.deviceid = (TextView) findViewById(R.id.device_id);
        this.decodeprofile = (TextView) findViewById(R.id.decode_profile);
        this.avclevel = (TextView) findViewById(R.id.avc_level);
        this.cpufrequency = (TextView) findViewById(R.id.cpu_frequency);
        this.cpukernel = (TextView) findViewById(R.id.cpu_kernel);
        this.cputype = (TextView) findViewById(R.id.cpu_type);
        this.sdkversion = (TextView) findViewById(R.id.sdk_version);
        this.phoneversion = (TextView) findViewById(R.id.phone_version);
        this.appversion = (TextView) findViewById(R.id.app_version);
        this.devices = (TextView) findViewById(R.id.devices);
        this.printlog = (Button) findViewById(R.id.print_log);
        this.printlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Log: ", sDevices + "\n" + sPhoneversion + "\n" + sSDKversion + "\n" + sCPUtype + "\n" + sCPUKernel + "\n" + sCPUFrequency + "\n" + sAvcLevel + "\n" + sDecodeprofile + "\n" + sDeviceid + "\n");
            }
        });
    }

    private void initData() {

        sDevices = getResources().getString(R.string.device_type) + " " + MUtils.getDeviceName();
        sPhoneversion = getResources().getString(R.string.phone_versions) + " " + MUtils.getOSVersionName();
        sSDKversion = getResources().getString(R.string.sdk_versions) + " " + MUtils.getVersionName(this);
        sCPUtype = getResources().getString(R.string.cpu_type) + " " + MUtils.getCapbility();
        sCPUKernel = getResources().getString(R.string.cpu_kernel) + " " + MUtils.getNumCores();
        sCPUFrequency = getResources().getString(R.string.cpu_frequency) + " " + MUtils.getMaxCpuFrequence();
        sAvcLevel = getResources().getString(R.string.avc_level) + " " + MUtils.getAVCLevel() + "";
        sDecodeprofile = getResources().getString(R.string.decode_profile) + " " + MUtils.getProfileUseNative();
        sDeviceid = getResources().getString(R.string.phone_id) + " " + MUtils.generateDeviceId(this.getApplicationContext());


        devices.setText(sDevices);
        phoneversion.setText(sPhoneversion);
        sdkversion.setText(sSDKversion);
        cputype.setText(sCPUtype);
        cpukernel.setText(sCPUKernel);
        cpufrequency.setText(sCPUFrequency);
        avclevel.setText(sAvcLevel);
        decodeprofile.setText(sDecodeprofile);
        deviceid.setText(sDeviceid);
    }
}
