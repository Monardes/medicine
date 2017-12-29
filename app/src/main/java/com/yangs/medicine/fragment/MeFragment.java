package com.yangs.medicine.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yangs.medicine.R;
import com.yangs.medicine.Splash;
import com.yangs.medicine.activity.APPlication;
import com.yangs.medicine.activity.InfoEditActivity;
import com.yangs.medicine.activity.LoginActivity;
import com.yangs.medicine.activity.MeActivity;
import com.yangs.medicine.adapter.MeAdapter;
import com.yangs.medicine.adapter.TitleBuilder;
import com.yangs.medicine.model.MeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangs on 2017/9/23 0023.
 * 我的
 */

public class MeFragment extends LazyLoadFragment implements MeAdapter.OnItemClickListener, View.OnClickListener {
    private SimpleDraweeView sv_head;
    private ImageView iv_edit;
    private TextView tv_name;
    private TextView tv_level;
    private Button bt_logout;
    private RecyclerView mrecyclerView;
    private MeAdapter meAdapter;
    private List<MeList> list;
    private View mLay;
    private Handler handler;

    @Override
    protected int setContentView() {
        return R.layout.mefrag_layout;
    }

    @Override
    protected void lazyLoad() {
        if (isInit) {
            if (!isLoad) {
                initData();
                initView();
            }
        }

    }

    private void initData() {
        list = new ArrayList<>();
        MeList meList = new MeList();
        meList.setIcon(R.drawable.icon_shoucang);
        meList.setName("我的收藏");
        list.add(meList);
        meList = new MeList();
        meList.setIcon(R.drawable.icon_renwu);
        meList.setName("我的任务");
        list.add(meList);
        meList = new MeList();
        meList.setIcon(R.drawable.icon_xiaoxi);
        meList.setName("消息");
        list.add(meList);
        meList = new MeList();
        meList.setIcon(R.drawable.icon_yijian);
        meList.setName("反馈意见");
        list.add(meList);
        meList = new MeList();
        meList.setIcon(R.drawable.icon_guanyuwomen);
        meList.setName("关于我们");
        list.add(meList);
    }

    private void initView() {
        handler = new Handler();
        mLay = getContentView();
        sv_head = (SimpleDraweeView) mLay.findViewById(R.id.me_sv);
        iv_edit = (ImageView) mLay.findViewById(R.id.me_iv_edit);
        tv_name = (TextView) mLay.findViewById(R.id.me_tv_name);
        tv_level = (TextView) mLay.findViewById(R.id.me_tv_level);
        bt_logout = (Button) mLay.findViewById(R.id.me_bt_logout);
        mrecyclerView = (RecyclerView) mLay.findViewById(R.id.me_rv);
        meAdapter = new MeAdapter(getActivity(), list);
        meAdapter.setOnItemClickListener(this);
        bt_logout.setOnClickListener(this);
        iv_edit.setOnClickListener(this);
        mrecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mrecyclerView.setAdapter(meAdapter);
        tv_name.setText(APPlication.user);
        if ("".equals(APPlication.subject)) {
            tv_level.setText("未设置");
        } else {
            tv_level.setText(APPlication.grade);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Bundle bundle = new Bundle();
        Intent intent;
        switch (position) {
            case 0:
                bundle.putString("name", "我的收藏");
                intent = new Intent(getActivity(), MeActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case 1:
                bundle.putString("name", "我的任务");
                intent = new Intent(getActivity(), MeActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case 3:
                PackageManager packageManager = getContext().getPackageManager();
                try {
                    packageManager.getPackageInfo("com.tencent.mobileqq", 0);
                    String url = "mqqwpa://im/chat?chat_type=wpa&uin=908379757";
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (PackageManager.NameNotFoundException e) {
                    APPlication.showToast("安装QQ后才能反馈意见哦", 0);
                }
                break;
            default:
                APPlication.showToast(list.get(position).getName(), 0);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.me_bt_logout:
                new AlertDialog.Builder(getContext()).setTitle("提示")
                        .setMessage("是否要退出登录?").setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        final ProgressDialog progressDialog = new ProgressDialog(getContext());
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("请稍后...");
                        progressDialog.show();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                APPlication.save.edit().putBoolean("login_status", false)
                                        .putString("grade", "").putString("subject", "").apply();
                                startActivity(new Intent(getContext(), LoginActivity.class));
                                getActivity().finish();
                            }
                        }, 1500);
                    }
                }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
                break;
            case R.id.me_iv_edit:
                startActivityForResult(new Intent(getContext(), InfoEditActivity.class), 1);
                break;
        }
    }
}
