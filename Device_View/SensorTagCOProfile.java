/******************************************************************************
 * Copyright (C) Open Stack, Inc.  All Rights Reserved.
 *
 * This software is unpublished and contains the trade secrets and
 * confidential proprietary information of Open Stack, Inc..
 *
 * No part of this publication may be reproduced in any form whatsoever without
 * written prior approval by Open Stack, Inc..
 *
 * Open Stack, Inc. reserves the right to revise this publication
 * and make changes without obligation to notify any person of such revisions
 * or changes.
 *****************************************************************************/

/*
 * SensorTagCOProfile.java; CO sensor service of bulldi
 */

package openstack.bulldi.safe3x.Device_View;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import openstack.bulldi.common.BluetoothLeService;
import openstack.bulldi.common.Data_store;
import openstack.bulldi.common.GenericBluetoothProfile;
import openstack.bulldi.safe3x.Preference_etc.Language_setting;
import openstack.bulldi.safe3x.R;
import openstack.util.Point3D;

public class SensorTagCOProfile extends GenericBluetoothProfile {

    public static List<Double> myList_co = new ArrayList<Double>();
    public static List<Double> myList_co_check = new ArrayList<Double>();
    public static double co_max;
    public static double co_min;
    public static double co_average;
    public static boolean co_alarm=false;
    Context context;
    //Draw graph
    static Calendar current_time;
    public static DateFormat df;
    public static DateFormat df_1;
    DateFormat df1 = new SimpleDateFormat("dd.MM.yyyy");
    DateFormat df2 = new SimpleDateFormat("HH:mm");
    DateFormat df1_ko = new SimpleDateFormat("yy.MM.d");
    DateFormat df2_ko = new SimpleDateFormat("HH:mm");
    public static String alarm_value;
    public static String time1;
    public static String time2;
    public static String cur_time_1;
    public static String cur_time_2;
    static public Data_store data_co[]={new Data_store(),new Data_store(),new Data_store(),new Data_store(),new Data_store(),new Data_store(),new Data_store(),new Data_store(),new Data_store(),new Data_store(),
            new Data_store(),new Data_store(),new Data_store(),new Data_store(),new Data_store(),new Data_store(),new Data_store(),new Data_store(),new Data_store(),new Data_store(),
            new Data_store(),new Data_store(),new Data_store(),new Data_store()};
    public SensorTagCOProfile(Context con,BluetoothDevice device,BluetoothGattService service,BluetoothLeService controller) {
        super(con,device,service,controller);
        context=con;
        List<BluetoothGattCharacteristic> characteristics = this.mBTService.getCharacteristics();

        for (BluetoothGattCharacteristic c : characteristics) {
            if (c.getUuid().toString().equals(SensorTagGatt.UUID_CO_DATA.toString())) {
                this.dataC = c;
            }
            if (c.getUuid().toString().equals(SensorTagGatt.UUID_CO_CONF.toString())) {
                this.configC = c;
            }
            if (c.getUuid().toString().equals(SensorTagGatt.UUID_CO_PERI.toString())) {
                this.periodC = c;
            }
        }

    }

    public static boolean isCorrectService(BluetoothGattService service) {
        if ((service.getUuid().toString().compareTo(SensorTagGatt.UUID_CO_SERV.toString())) == 0) {//service.getUuid().toString().compareTo(SensorTagGatt.UUID_HUM_DATA.toString())) {
            Log.d("Test", "Match !");
            return true;
        }
        else return false;
    }
    public void didWriteValueForCharacteristic(BluetoothGattCharacteristic c) {

    }
    public void didReadValueForCharacteristic(BluetoothGattCharacteristic c) {

    }
    @Override
    public void didUpdateValueForCharacteristic(BluetoothGattCharacteristic c) {
        byte[] value = c.getValue();
        if (c.equals(this.dataC)){
            Point3D v = Sensor.CO.convert(value);
            if(myList_co.size()==2147483647) myList_co.remove(0);
            myList_co.add(v.x);
            if(myList_co_check.size()==2147483647) myList_co_check.remove(0);
            myList_co_check.add(v.x);
            for(int i=0;i<myList_co_check.size();i++){
                Log.i("CO check","value:"+myList_co_check.get(i));
            }
            //Log.i("Check list size", "CO list size: " + myList_co.size() + " with value: " + v.x);
            co_max = Collections.max(myList_co);
            co_min=Collections.min(myList_co);
            co_average=calculateAverage(myList_co);

            final double co_show = v.x;

            /*int co_hex=(int) v.y;
            String span=Integer.toHexString(co_hex);
            Device_CO.co_text.setText("0x"+span);*/
            Device_CO.co_text.setText(String.format("%.0f ",v.x ));

            final String s = String.format("%.0f ", co_show);



            //int a=(int)v.x;
            //Device_CO.co_text.setText("0x"+Integer.toHexString(a).toString().toUpperCase()+" ");
            if(v.x==0) Device_CO.co_status.setText(context.getString(R.string.sensor_status_zeros));
            else Device_CO.co_status.setText(context.getString(R.string.sensor_status_suspect));



            //Graph
            current_time= Calendar.getInstance();
            df = new SimpleDateFormat("HH:mm a");
            cur_time_1 = df.format(Calendar.getInstance().getTime());
            df_1 = new SimpleDateFormat("d/MM");
            cur_time_2 = df_1.format(Calendar.getInstance().getTime());

            if((current_time.get(Calendar.MINUTE)>=0)&&(current_time.get(Calendar.MINUTE)<=3) ) {
                data_co[current_time.get(Calendar.HOUR_OF_DAY)].set_time(current_time.get(Calendar.YEAR), current_time.get(Calendar.MONTH), current_time.get(Calendar.DAY_OF_MONTH),current_time.get(Calendar.HOUR_OF_DAY),current_time.get(Calendar.MINUTE));
                data_co[current_time.get(Calendar.HOUR_OF_DAY)].set_data(v.x);
                Log.i("Check co graph","value: "+v.x);
            }

            //Alarm check
            int list_size=myList_co_check.size();
            if (v.x>=300)
            {
                //Get 3 last variable in array
                if(list_size<3) co_alarm=false;
                else if((list_size>=3)&&(list_size<6)) {
                    List<Double> array = new ArrayList<Double>();
                    for (int i = 1; i < 4; i++) {
                        array.add(myList_co_check.get(myList_co_check.size() - i));
                    }
                    if (ishiger(300, array) == true) {
                        co_alarm = true;
                        alarm_value=String.format("%.0f", v.x)+"ppm";
                        time1 = df1.format(Calendar.getInstance().getTime());
                        time2 = df2.format(Calendar.getInstance().getTime());

                    }
                    else co_alarm = false;
                }
                else if((list_size>=6) && (list_size<29)){
                    List<Double> array_3 = new ArrayList<Double>();
                    for (int i = 1; i < 4; i++) {
                        array_3.add(myList_co_check.get(myList_co_check.size() - i));
                    }
                    if (ishiger(300, array_3) == true) {
                        co_alarm = true;
                        alarm_value=String.format("%.0f", v.x)+"ppm";
                        time1 = df1.format(Calendar.getInstance().getTime());
                        time2 = df2.format(Calendar.getInstance().getTime());
                    }
                    //else co_alarm = false;
                    else {
                        List<Double> array = new ArrayList<Double>();
                        for (int i = 1; i < 7; i++) {
                            array.add(myList_co_check.get(myList_co_check.size() - i));
                        }
                        if (ishiger(100, array) == true) {
                            co_alarm = true;
                            alarm_value=String.format("%.0f", v.x)+"ppm";
                            time1 = df1.format(Calendar.getInstance().getTime());
                            time2 = df2.format(Calendar.getInstance().getTime());
                        }
                        else co_alarm = false;
                    }
                }
                else{
                    List<Double> array_3 = new ArrayList<Double>();
                    for (int i = 1; i < 4; i++) {
                        array_3.add(myList_co_check.get(myList_co_check.size() - i));
                    }
                    if (ishiger(300, array_3) == true) {
                        co_alarm = true;
                        alarm_value=String.format("%.0f", v.x)+"ppm";
                        time1 = df1.format(Calendar.getInstance().getTime());
                        time2 = df2.format(Calendar.getInstance().getTime());
                    }
                    //else co_alarm = false;
                    else {
                        List<Double> array_6 = new ArrayList<Double>();
                        for (int i = 1; i < 7; i++) {
                            array_6.add(myList_co_check.get(myList_co_check.size() - i));
                        }
                        if (ishiger(100, array_6) == true) {
                            co_alarm = true;
                            alarm_value=String.format("%.0f", v.x)+"ppm";
                            time1 = df1.format(Calendar.getInstance().getTime());
                            time2 = df2.format(Calendar.getInstance().getTime());
                        }
                        else {
                            List<Double> array = new ArrayList<Double>();
                            for (int i=1;i<30;i++)
                            {
                                array.add(myList_co_check.get(myList_co_check.size()-i));
                            }
                            if(ishiger(50, array)==true) {
                                co_alarm=true;
                                alarm_value=String.format("%.0f", v.x)+"ppm";
                                time1 = df1.format(Calendar.getInstance().getTime());
                                time2 = df2.format(Calendar.getInstance().getTime());
                            }
                            else co_alarm=false;
                        }
                    }

                }
            }
            else if((v.x>=100) && (v.x<300)){
                if(co_alarm==true) {
                    //Do nothing
                }
                else{
                    if (list_size < 6) {
                         co_alarm = false;
                    } else if ((list_size >= 6) && (list_size < 29)) {
                            List<Double> array = new ArrayList<Double>();
                            for (int i = 1; i < 7; i++) {
                                array.add(myList_co_check.get(myList_co_check.size() - i));
                            }
                            if (ishiger(100, array) == true) {
                                co_alarm = true;
                                alarm_value = String.format("%.0f", v.x) + "ppm";
                                time1 = df1.format(Calendar.getInstance().getTime());
                                time2 = df2.format(Calendar.getInstance().getTime());
                            } else co_alarm = false;

                    } else {
                            List<Double> array_6 = new ArrayList<Double>();
                            for (int i = 1; i < 7; i++) {
                                array_6.add(myList_co_check.get(myList_co_check.size() - i));
                            }
                            if (ishiger(100, array_6) == true) {
                                co_alarm = true;
                                alarm_value = String.format("%.0f", v.x) + "ppm";
                                time1 = df1.format(Calendar.getInstance().getTime());
                                time2 = df2.format(Calendar.getInstance().getTime());
                            } else {
                                List<Double> array = new ArrayList<Double>();
                                for (int i = 1; i < 30; i++) {
                                    array.add(myList_co_check.get(myList_co_check.size() - i));
                                }
                                if (ishiger(50, array) == true) {
                                    co_alarm = true;
                                    alarm_value = String.format("%.0f", v.x) + "ppm";
                                    time1 = df1.format(Calendar.getInstance().getTime());
                                    time2 = df2.format(Calendar.getInstance().getTime());
                                } else co_alarm = false;
                            }

                    }
                }
            }
            else if((v.x>=50) && (v.x<100)) {
                if (co_alarm == true) {
                    //Do nothing
                } else {
                    if (list_size < 29) co_alarm = false;
                    else {
                        List<Double> array = new ArrayList<Double>();
                        for (int i = 1; i < 30; i++) {
                            array.add(myList_co_check.get(myList_co_check.size() - i));
                        }
                        if (ishiger(50, array) == true) {
                            co_alarm = true;
                            alarm_value = String.format("%.0f", v.x) + "ppm";
                            time1 = df1.format(Calendar.getInstance().getTime());
                            time2 = df2.format(Calendar.getInstance().getTime());
                        } else co_alarm = false;
                    }
                }
            }
            else  {
                if (co_alarm == true) {
                    if(v.x>=10){
                        //Do nothing
                    }else co_alarm=false;
                } else co_alarm = false;
            }
        }
    }
        @Override
        public Map<String,String> getMQTTMap() {
            Point3D v = Sensor.CO.convert(this.dataC.getValue());
        Map<String,String> map = new HashMap<String, String>();
        map.put("co",String.format("%.0f",v.x));
        return map;
    }
    public Point3D get_data()
    {
        Point3D co = Sensor.CO.convert(this.dataC.getValue());
        return co;
    }
    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }
    private double calculateAverage(List <Double> marks) {
        Double sum = 0.0;
        if(!marks.isEmpty()) {
            for (Double mark : marks) {
                sum += mark;
            }
            return sum / marks.size();
        }
        return sum;
    }
    public static boolean ishiger(double limit, List<Double> data){
        for(int k = 0; k < data.size(); k++){
            if (data.get(k) < limit)
                return false;
        }
        return true;
    }
    public static Data_store[] get_co()
    {
        return data_co;
    }
}
