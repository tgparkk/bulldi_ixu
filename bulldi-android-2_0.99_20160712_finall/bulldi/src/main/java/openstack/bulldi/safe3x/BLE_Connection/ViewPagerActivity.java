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
 * ViewPagerActivity.java: viewpager control of connection page
 */

package openstack.bulldi.safe3x.BLE_Connection;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
// import android.util.Log;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import openstack.bulldi.safe3x.R;

public class ViewPagerActivity extends FragmentActivity {
  // Constants
   private static final String TAG = "ViewPagerActivity";

  // GUI
  protected static ViewPagerActivity mThis = null;
  protected SectionsPagerAdapter mSectionsPagerAdapter;
  private ViewPager mViewPager;
  protected int mResourceFragmentPager;
  protected int mResourceIdPager;
  private int mCurrentTab = 0;
  protected Menu optionsMenu;
  private MenuItem refreshItem;
  protected boolean mBusy;
  protected ViewPagerActivity() {
    // Log.d(TAG, "construct");
    mThis = this;
    mBusy = false;
    refreshItem = null;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // Log.d(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    setContentView(mResourceFragmentPager);
    Typeface font_title_light=Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
    Typeface font_title_regular=Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");

    // Set up the action bar

    final ActionBar actionBar = getActionBar();
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    actionBar.setDisplayShowTitleEnabled(false);
    actionBar.setDisplayShowHomeEnabled(false);
    actionBar.setDisplayHomeAsUpEnabled(false);

    // Set up the ViewPager with the sections adapter.
    mViewPager = (ViewPager) findViewById(mResourceIdPager);
    mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
      @Override
      public void onPageSelected(int n) {
         Log.d(TAG, "onPageSelected: " + n);
        actionBar.setSelectedNavigationItem(n);
      }
    });
    // Create the adapter that will return a fragment for each section
    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

    // Set up the ViewPager with the sections adapter.
    mViewPager.setAdapter(mSectionsPagerAdapter);
    mViewPager.setOffscreenPageLimit(10);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    // Log.d(TAG, "onDestroy");
    mSectionsPagerAdapter = null;
  }

  @Override
  public void onBackPressed() {
    if (mCurrentTab != 0)
      getActionBar().setSelectedNavigationItem(0);
    else
      super.onBackPressed();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Log.d(TAG, "onOptionsItemSelected");
    // Handle presses on the action bar items
    switch (item.getItemId()) {
    // Respond to the action bar's Up/Home button
    case android.R.id.home:
      onBackPressed();
      return true;
    default:
      return super.onOptionsItemSelected(item);
    }
  }

  protected void showBusyIndicator(final boolean busy) {
  	if (optionsMenu != null) {
  		refreshItem = optionsMenu.findItem(R.id.opt_progress);
  		if (refreshItem != null) {
  			if (busy) {
  				refreshItem.setActionView(R.layout.frame_progress);
  			} else {
  				refreshItem.setActionView(null);
  			}
    		refreshItem.setVisible(busy);
  		} else {
    		// Log.e(TAG,"Refresh item not expanded");
          Log.d("ViewPager","Refresh item not expanded");
        }
  	} else {
  		 Log.e("ViewPager","Options not expanded");
  	}
  	mBusy = busy;
  }
  
  protected void refreshBusyIndicator() {
  	if (refreshItem == null) {
  		runOnUiThread(new Runnable() {

  			@Override
  			public void run() {
  				showBusyIndicator(mBusy);
  			}
  		});
  	}
  }


  public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragmentList;
    private List<String> mTitles;
    //private List<TextView> mTitles;

    public SectionsPagerAdapter(FragmentManager fm) {
      super(fm);
      mFragmentList = new ArrayList<Fragment>();
      mTitles = new ArrayList<String>();
      //mTitles = new ArrayList<TextView>();
    }

    public void addSection(Fragment fragment, String title) {
    //public void addSection(Fragment fragment, TextView title) {
      final ActionBar actionBar = getActionBar();
      actionBar.setStackedBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.White)));
      actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

      mFragmentList.add(fragment);

      mTitles.add(title);

      actionBar.addTab(actionBar.newTab().setText(title).setTabListener(tabListener));
      notifyDataSetChanged();
      // Log.d(TAG, "Tab: " + title);
    }

    @Override
    public Fragment getItem(int position) {
      return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
      return mTitles.size();
    }
  }

  // Create a tab listener that is called when the user changes tabs.
  ActionBar.TabListener tabListener = new ActionBar.TabListener() {

    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
      int n = tab.getPosition();
      // Log.d(TAG, "onTabSelected: " + n);
      mCurrentTab = n;
      mViewPager.setCurrentItem(n);

      //Change title font

    }

    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
      // int n = tab.getPosition();
      // Log.d(TAG, "onTabUnselected: " + n);
    }

    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
      // int n = tab.getPosition();
      // Log.d(TAG, "onTabReselected: " + n);
    }
  };


}
