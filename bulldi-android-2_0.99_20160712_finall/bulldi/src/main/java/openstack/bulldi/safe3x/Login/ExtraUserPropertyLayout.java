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
 * ExtraUserPropertyLayout.java: kakaotalk api
 */

package openstack.bulldi.safe3x.Login;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.util.HashMap;
import java.util.Map;

import openstack.bulldi.safe3x.R;


public class ExtraUserPropertyLayout extends FrameLayout {
    // property key
    private  static final String NAME_KEY = "name";
    private  static final String AGE_KEY = "age";
    private  static final String GENDER_KEY = "gender";

    private EditText name;
    private EditText age;
    private KakaoDialogSpinner gender;

    public ExtraUserPropertyLayout(Context context) {
        super(context);
        initView();
    }

    public ExtraUserPropertyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ExtraUserPropertyLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        final View view = inflate(getContext(), R.layout.layout_usermgmt_extra_user_property, this);
        name = (EditText) view.findViewById(R.id.name);
        age = (EditText) view.findViewById(R.id.age);
        gender = (KakaoDialogSpinner) view.findViewById(R.id.gender);
    }

    public HashMap<String, String> getProperties(){
        final String nickNameValue = name.getText().toString();
        final String ageValue = age.getText().toString();
        final String genderValue = String.valueOf(gender.getSelectedItem());

        HashMap<String, String> properties = new HashMap<String, String>();
        if(nickNameValue != null)
            properties.put(NAME_KEY, nickNameValue);
        if(ageValue != null)
            properties.put(AGE_KEY, ageValue);
        if(genderValue != null)
            properties.put(GENDER_KEY, genderValue);

        return properties;
    }

    void showProperties(final Map<String, String> properties) {
        final String nameValue = properties.get(NAME_KEY);
        if (nameValue != null)
            name.setText(nameValue);

        final String ageValue = properties.get(AGE_KEY);
        if (ageValue != null)
            age.setText(ageValue);

        final String genderValue = properties.get(GENDER_KEY);
        if (genderValue != null) {
            if (genderValue.equalsIgnoreCase(getContext().getString(R.string.female))) {
                gender.setSelection(0);
            } else {
                gender.setSelection(1);
            }
        }
    }
}
