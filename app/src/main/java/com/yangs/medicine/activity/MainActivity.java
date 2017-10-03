package com.yangs.medicine.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yangs.medicine.R;
import com.yangs.medicine.fragment.BookFragment;
import com.yangs.medicine.fragment.ErrorFragment;
import com.yangs.medicine.fragment.MeFragment;
import com.yangs.medicine.fragment.TopicFragment;
import com.yangs.medicine.util.StatusBar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FragmentManager fm;
    private LinearLayout tab_ly_1;
    private LinearLayout tab_ly_2;
    private LinearLayout tab_ly_3;
    private LinearLayout tab_ly_4;
    private TextView tab_tv_1;
    private TextView tab_tv_2;
    private TextView tab_tv_3;
    private TextView tab_tv_4;
    private ImageView tab_iv_1;
    private ImageView tab_iv_2;
    private ImageView tab_iv_3;
    private ImageView tab_iv_4;
    private TopicFragment topicFragment;
    private ErrorFragment errorFragment;
    private BookFragment bookFragment;
    private MeFragment meFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity_layout);
        init();
        switchFragment(1);
    }

    private void init() {
        fm = getSupportFragmentManager();
        tab_ly_1 = (LinearLayout) findViewById(R.id.tab_ly_1);
        tab_ly_2 = (LinearLayout) findViewById(R.id.tab_ly_2);
        tab_ly_3 = (LinearLayout) findViewById(R.id.tab_ly_3);
        tab_ly_4 = (LinearLayout) findViewById(R.id.tab_ly_4);
        tab_tv_1 = (TextView) tab_ly_1.findViewById(R.id.tab_tv_1);
        tab_tv_2 = (TextView) tab_ly_2.findViewById(R.id.tab_tv_2);
        tab_tv_3 = (TextView) tab_ly_3.findViewById(R.id.tab_tv_3);
        tab_tv_4 = (TextView) tab_ly_4.findViewById(R.id.tab_tv_4);
        tab_iv_1 = (ImageView) tab_ly_1.findViewById(R.id.tab_iv_1);
        tab_iv_2 = (ImageView) tab_ly_2.findViewById(R.id.tab_iv_2);
        tab_iv_3 = (ImageView) tab_ly_3.findViewById(R.id.tab_iv_3);
        tab_iv_4 = (ImageView) tab_ly_4.findViewById(R.id.tab_iv_4);
        tab_ly_1.setOnClickListener(this);
        tab_ly_2.setOnClickListener(this);
        tab_ly_3.setOnClickListener(this);
        tab_ly_4.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab_ly_1:
                switchFragment(1);
                break;
            case R.id.tab_ly_2:
                switchFragment(2);
                break;
            case R.id.tab_ly_3:
                switchFragment(3);
                break;
            case R.id.tab_ly_4:
                switchFragment(4);
                break;
        }

    }

    private void switchFragment(int i) {
        FragmentTransaction transaction = fm.beginTransaction();
        if (topicFragment != null)
            transaction.hide(topicFragment);
        if (errorFragment != null)
            transaction.hide(errorFragment);
        if (bookFragment != null)
            transaction.hide(bookFragment);
        if (meFragment != null)
            transaction.hide(meFragment);
        tab_tv_1.setTextColor(getResources().getColor(R.color.gray));
        tab_tv_2.setTextColor(getResources().getColor(R.color.gray));
        tab_tv_3.setTextColor(getResources().getColor(R.color.gray));
        tab_tv_4.setTextColor(getResources().getColor(R.color.gray));
        tab_iv_1.setBackgroundResource(R.drawable.tabbar_icon_tiji_default);
        tab_iv_2.setBackgroundResource(R.drawable.tabbar_icon_cuotiji_default);
        tab_iv_3.setBackgroundResource(R.drawable.tabbar_icon_jiaocai_default);
        tab_iv_4.setBackgroundResource(R.drawable.tabbar_icon_wode_default);
        switch (i) {
            case 1:
                if (topicFragment == null) {
                    topicFragment = new TopicFragment();
                    transaction.add(R.id.frame, topicFragment);
                } else {
                    transaction.show(topicFragment);
                }
                tab_tv_1.setTextColor(getResources().getColor(R.color.blue));
                tab_iv_1.setBackgroundResource(R.drawable.tabbar_icon_tiji_selected);
                break;
            case 2:
                if (errorFragment == null) {
                    errorFragment = new ErrorFragment();
                    transaction.add(R.id.frame, errorFragment);
                } else {
                    transaction.show(errorFragment);
                }
                tab_tv_2.setTextColor(getResources().getColor(R.color.blue));
                tab_iv_2.setBackgroundResource(R.drawable.tabbar_icon_cuotiji_selected);
                break;
            case 3:
                if (bookFragment == null) {
                    bookFragment = new BookFragment();
                    transaction.add(R.id.frame, bookFragment);
                } else {
                    transaction.show(bookFragment);
                }
                tab_tv_3.setTextColor(getResources().getColor(R.color.blue));
                tab_iv_3.setBackgroundResource(R.drawable.tabbar_icon_jiaocai_selected);
                break;
            case 4:
                if (meFragment == null) {
                    meFragment = new MeFragment();
                    transaction.add(R.id.frame, meFragment);
                } else {
                    transaction.show(meFragment);
                }
                tab_tv_4.setTextColor(getResources().getColor(R.color.blue));
                tab_iv_4.setBackgroundResource(R.drawable.tabbar_icon_wode_selected);
                break;
        }
        transaction.commitAllowingStateLoss();
    }
}
