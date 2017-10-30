package com.yangs.medicine.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yangs.medicine.R;
import com.yangs.medicine.adapter.TimuDialogAdapter;
import com.yangs.medicine.db.QuestionUtil;
import com.yangs.medicine.model.ChooseList;
import com.yangs.medicine.model.Question;
import com.yangs.medicine.model.TimuList;
import com.yangs.medicine.question.AskQuesFragment;
import com.yangs.medicine.question.BlankQuesFragment;
import com.yangs.medicine.question.CheckQuesFragment;
import com.yangs.medicine.question.ChooseQuesFragment;
import com.yangs.medicine.question.ExplainQuesFragment;
import com.yangs.medicine.util.FitStatusBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangs on 2017/9/24 0024.
 */

public class QuestionActivity extends BaseActivity implements View.OnClickListener, TimuDialogAdapter.TimuOnClickListener {
    private Button bt_back;
    private Button bt_ok;
    private TextView head_title;
    private LinearLayout ll_chat;
    private ImageView iv_love;
    private ImageView iv_share;
    private ImageView iv_chat;
    private ImageView iv_timu;
    private ViewPager viewPager;
    private List<Fragment> frag_list;
    private Dialog timuDialog;
    private AlertDialog disscussDialog;
    private DialogOnClickListener timuListener;
    public static List<TimuList> timuLists;

    private int choose_count;
    private int blank_count;
    private int check_count;
    private int ask_count;
    private int explain_count;

    private int cursor_count = 0;           //游标
    private TimuDialogAdapter timuDialogAdapter;
    private RecyclerView timuDialog_rv;
    private ProgressDialog progressDialog;
    private ProgressBar dis_pb;
    private int get_question_code;
    private String SP;
    private String Cha;
    private String Subject;
    private int postDiscussCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questionactivity_layout);
        FitStatusBar.addStatusBarView(this);
        initView();
        init();
    }

    private void initView() {
        Bundle bundle = getIntent().getExtras();
        SP = bundle.getString("SP");
        Cha = bundle.getString("Cha");
        Subject = bundle.getString("Name");
        head_title = (TextView) findViewById(R.id.questionactivity_head_title);
        bt_back = (Button) findViewById(R.id.questionactivity_head_back);
        bt_ok = (Button) findViewById(R.id.questionactivity_head_ok);
        ll_chat = (LinearLayout) findViewById(R.id.questionactivity_ll);
        iv_love = (ImageView) findViewById(R.id.questionactivity_iv_love);
        iv_share = (ImageView) findViewById(R.id.questionactivity_iv_share);
        iv_chat = (ImageView) findViewById(R.id.questionactivity_iv_talk);
        iv_timu = (ImageView) findViewById(R.id.questionactivity_iv_timu);
        viewPager = (ViewPager) findViewById(R.id.questionactivity_vp);
        bt_back.setOnClickListener(this);
        bt_ok.setOnClickListener(this);
        ll_chat.setOnClickListener(this);
        iv_love.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        iv_chat.setOnClickListener(this);
        iv_timu.setOnClickListener(this);
        frag_list = new ArrayList<>();
        timuLists = new ArrayList<>();
        head_title.setText(Subject);
    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("获取题目数据中...");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                get_question_code = APPlication.questionSource.getQuestion(SP, Cha);
                choose_count = QuestionUtil.getQuestionCount("选择题");
                blank_count = QuestionUtil.getQuestionCount("填空题");
                check_count = QuestionUtil.getQuestionCount("判断题");
                explain_count = QuestionUtil.getQuestionCount("名词解释题");
                ask_count = QuestionUtil.getQuestionCount("问答题");
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (progressDialog.isShowing())
                        progressDialog.cancel();
                    switch (get_question_code) {
                        case 0:
                            initData();
                            viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
                                @Override
                                public Fragment getItem(int position) {
                                    return frag_list.get(position);
                                }

                                @Override
                                public int getCount() {
                                    return frag_list.size();
                                }
                            });
                            break;
                        case -1:
                            APPlication.showToast("解析题目数据时发生了错误,请反馈!", 1);
                            break;
                        case -2:
                            APPlication.showToast("连接服务器失败!", 0);
                            break;
                    }
                    break;
                case 1:
                    if (dis_pb != null)
                        dis_pb.setVisibility(View.INVISIBLE);
                    switch (postDiscussCode) {
                        case 0:
                            APPlication.showToast("提交成功!", 0);
                            if (disscussDialog != null)
                                disscussDialog.cancel();
                            Fragment fragment = frag_list.get(viewPager.getCurrentItem());
                            if (fragment instanceof ChooseQuesFragment) {
                                ((ChooseQuesFragment) fragment).checkOK();
                            }
                            break;
                        case -1:
                            APPlication.showToast("服务器出错,评论失败!", 0);
                            break;
                        case -2:
                            APPlication.showToast("评论失败,网络出错!", 0);
                            break;
                    }
                    break;
            }
            return false;
        }
    });

    private void initData() {
        TimuList timuList = new TimuList();
        timuList.setType("生理学 第一章");
        timuLists.add(timuList);
        if (choose_count != 0) {
            timuList = new TimuList();
            timuList.setType("选择题");
            timuLists.add(timuList);
            for (int i = 0; i < choose_count; i++) {
                timuList = new TimuList();
                timuList.setIndex(i + cursor_count + 1);
                timuList.setStatus("未做");
                timuList.setFragIndex(frag_list.size());
                ChooseQuesFragment chooseQuesFragment = new ChooseQuesFragment();
                chooseQuesFragment.setOnResultListener(new ChooseQuesFragment.OnResultListener() {
                    @Override
                    public void onResult(int dialogIndex, int status) {
                        if (status == 1)
                            timuLists.get(dialogIndex).setStatus("对");
                        else if (status == 0)
                            timuLists.get(dialogIndex).setStatus("错");
                        if (timuDialog_rv != null && timuDialogAdapter != null)
                            timuDialogAdapter.notifyDataSetChanged();
                    }
                });
                Bundle bundle = new Bundle();
                bundle.putSerializable("dialogIndex", timuLists.size());
                bundle.putSerializable("index", i + cursor_count);
                chooseQuesFragment.setArguments(bundle);
                frag_list.add(chooseQuesFragment);
                timuLists.add(timuList);
            }
            cursor_count += choose_count;
        }
        if (blank_count != 0) {
            timuList = new TimuList();
            timuList.setType("填空题");
            timuLists.add(timuList);
            Boolean flag = false;
            for (int i = 1; i <= blank_count; i++) {
                if (i % 5 == 0) {
                    flag = true;
                }
                timuList = new TimuList();
                timuList.setIndex(i + cursor_count);
                timuList.setStatus("未做");
                timuList.setFragIndex(frag_list.size());
                if ((blank_count - i == 0) && !flag) {
                    BlankQuesFragment blankQuesFragment = new BlankQuesFragment();
                    blankQuesFragment.setOnResultListener(new BlankQuesFragment.OnResultListener() {
                        @Override
                        public void onResult(int dialogIndex, int status) {
                            if (status == 1)
                                timuLists.get(dialogIndex).setStatus("对");
                            else if (status == 0)
                                timuLists.get(dialogIndex).setStatus("未做");
                            if (timuDialog_rv != null && timuDialogAdapter != null)
                                timuDialogAdapter.notifyDataSetChanged();
                        }
                    });
                    Bundle bundle = new Bundle();
                    int start = (i / 5) * 5 + cursor_count;
                    int end = i - 1 + cursor_count;
                    bundle.putSerializable("start", start);
                    bundle.putSerializable("end", end);
                    bundle.putSerializable("dialogIndex", timuLists.size() - start + end - 2);
                    blankQuesFragment.setArguments(bundle);
                    frag_list.add(blankQuesFragment);
                }
                if (flag) {
                    BlankQuesFragment blankQuesFragment = new BlankQuesFragment();
                    blankQuesFragment.setOnResultListener(new BlankQuesFragment.OnResultListener() {
                        @Override
                        public void onResult(int dialogIndex, int status) {
                            if (status == 1)
                                timuLists.get(dialogIndex).setStatus("对");
                            else if (status == 0)
                                timuLists.get(dialogIndex).setStatus("未做");
                            if (timuDialog_rv != null && timuDialogAdapter != null)
                                timuDialogAdapter.notifyDataSetChanged();
                        }
                    });
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("start", i - 5 + cursor_count);
                    bundle.putSerializable("end", i - 1 + cursor_count);
                    bundle.putSerializable("dialogIndex", timuLists.size() - 4);
                    blankQuesFragment.setArguments(bundle);
                    frag_list.add(blankQuesFragment);
                    flag = false;
                }
                timuLists.add(timuList);
            }
            cursor_count += blank_count;
        }
        if (check_count != 0) {
            timuList = new TimuList();
            timuList.setType("判断题");
            timuLists.add(timuList);
            for (int i = 0; i < check_count; i++) {
                timuList = new TimuList();
                timuList.setIndex(i + cursor_count + 1);
                timuList.setStatus("未做");
                timuList.setFragIndex(frag_list.size());
                CheckQuesFragment checkQuesFragment = new CheckQuesFragment();
                checkQuesFragment.setOnResultListener(new CheckQuesFragment.OnResultListener() {
                    @Override
                    public void onResult(int dialogIndex, int status) {
                        if (status == 1)
                            timuLists.get(dialogIndex).setStatus("对");
                        else if (status == 0)
                            timuLists.get(dialogIndex).setStatus("错");
                        if (timuDialog_rv != null && timuDialogAdapter != null)
                            timuDialogAdapter.notifyDataSetChanged();
                    }
                });
                Bundle bundle = new Bundle();
                bundle.putSerializable("index", i + cursor_count);
                bundle.putSerializable("dialogIndex", timuLists.size());
                checkQuesFragment.setArguments(bundle);
                frag_list.add(checkQuesFragment);
                timuLists.add(timuList);
            }
            cursor_count += check_count;
        }
        if (explain_count != 0) {
            timuList = new TimuList();
            timuList.setType("名词解释题");
            timuLists.add(timuList);
            Boolean flag = false;
            for (int i = 1; i <= explain_count; i++) {
                if (i % 5 == 0) {
                    flag = true;
                }
                timuList = new TimuList();
                timuList.setIndex(i + cursor_count);
                timuList.setStatus("未做");
                timuList.setFragIndex(frag_list.size());
                if ((explain_count - i == 0) && !flag) {
                    ExplainQuesFragment explainQuesFragment = new ExplainQuesFragment();
                    explainQuesFragment.setOnResultListener(new ExplainQuesFragment.OnResultListener() {
                        @Override
                        public void onResult(int dialogIndex, int status) {
                            if (status == 1)
                                timuLists.get(dialogIndex).setStatus("对");
                            else if (status == 0)
                                timuLists.get(dialogIndex).setStatus("未做");
                            if (timuDialog_rv != null && timuDialogAdapter != null)
                                timuDialogAdapter.notifyDataSetChanged();
                        }
                    });
                    Bundle bundle = new Bundle();
                    int start = (i / 5) * 5 + cursor_count;
                    int end = i - 1 + cursor_count;
                    int pice = i % 5;
                    bundle.putSerializable("start", start);
                    bundle.putSerializable("end", end);
                    bundle.putSerializable("dialogIndex", timuLists.size() - pice + 1);
                    explainQuesFragment.setArguments(bundle);
                    frag_list.add(explainQuesFragment);
                }
                if (flag) {
                    ExplainQuesFragment explainQuesFragment = new ExplainQuesFragment();
                    explainQuesFragment.setOnResultListener(new ExplainQuesFragment.OnResultListener() {
                        @Override
                        public void onResult(int dialogIndex, int status) {
                            if (status == 1)
                                timuLists.get(dialogIndex).setStatus("对");
                            else if (status == 0)
                                timuLists.get(dialogIndex).setStatus("未做");
                            if (timuDialog_rv != null && timuDialogAdapter != null)
                                timuDialogAdapter.notifyDataSetChanged();
                        }
                    });
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("start", i - 5 + cursor_count);
                    bundle.putSerializable("end", i - 1 + cursor_count);
                    bundle.putSerializable("dialogIndex", timuLists.size() - 4);
                    explainQuesFragment.setArguments(bundle);
                    frag_list.add(explainQuesFragment);
                    flag = false;
                }
                timuLists.add(timuList);
            }
            cursor_count += explain_count;
        }
        if (ask_count != 0) {
            timuList = new TimuList();
            timuList.setType("问答题");
            timuLists.add(timuList);
            for (int i = 0; i < ask_count; i++) {
                timuList = new TimuList();
                timuList.setIndex(i + cursor_count + 1);
                timuList.setStatus("未做");
                timuList.setFragIndex(frag_list.size());
                AskQuesFragment askQuesFragment = new AskQuesFragment();
                askQuesFragment.setOnResultListener(new AskQuesFragment.OnResultListener() {
                    @Override
                    public void onResult(int dialogIndex, int status) {
                        if (status == 1)
                            timuLists.get(dialogIndex).setStatus("对");
                        else if (status == 0)
                            timuLists.get(dialogIndex).setStatus("未做");
                        if (timuDialog_rv != null && timuDialogAdapter != null)
                            timuDialogAdapter.notifyDataSetChanged();
                    }
                });
                Bundle bundle = new Bundle();
                bundle.putSerializable("index", i + cursor_count);
                bundle.putSerializable("dialogIndex", timuLists.size());
                askQuesFragment.setArguments(bundle);
                frag_list.add(askQuesFragment);
                timuLists.add(timuList);
            }
            cursor_count += ask_count;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.questionactivity_menu, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.questionactivity_head_back:
                finish();
                break;
            case R.id.questionactivity_head_ok:
                APPlication.showToast("未完成!", 0);
                break;
            case R.id.questionactivity_ll:
                if (disscussDialog == null) {
                    View view = LayoutInflater.from(QuestionActivity.this).inflate(R.layout.discussdialog_layout, null);
                    ImageView iv_close = (ImageView) view.findViewById(R.id.discussdialog_iv_close);
                    final EditText et_content = (EditText) view.findViewById(R.id.discussdialog_et_content);
                    dis_pb = (ProgressBar) view.findViewById(R.id.discussdialog_pb);
                    Button bt_sub = (Button) view.findViewById(R.id.discussdialog_bt_sub);
                    disscussDialog = new AlertDialog.Builder(QuestionActivity.this).setCancelable(false)
                            .setView(view).create();
                    iv_close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (disscussDialog != null)
                                disscussDialog.cancel();
                        }
                    });
                    bt_sub.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dis_pb != null)
                                dis_pb.setVisibility(View.VISIBLE);
                            final String s = et_content.getText().toString().trim();
                            if ("".equals(s)) {
                                APPlication.showToast("请输入评论内容", 0);
                                return;
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Question question = QuestionUtil.getQuestionByID(viewPager.getCurrentItem() + 1);
                                    postDiscussCode = APPlication.questionSource
                                            .postDiscuss(s, question.getRealID());
                                    handler.sendEmptyMessage(1);
                                }
                            }).start();
                        }
                    });
                }
                disscussDialog.show();
                break;
            case R.id.questionactivity_iv_love:
                APPlication.showToast("收藏", 0);
                break;
            case R.id.questionactivity_iv_share:
                APPlication.showToast("分享", 0);
                break;
            case R.id.questionactivity_iv_talk:
                Question question = QuestionUtil.getQuestionByID(viewPager.getCurrentItem() + 1);
                Bundle bundle = new Bundle();
                bundle.putString("realIndex", question.getRealID());
                Intent intent = new Intent(QuestionActivity.this, DiscussActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
                break;
            case R.id.questionactivity_iv_timu:
                if (timuDialog == null) {
                    timuListener = new DialogOnClickListener();
                    timuDialog = new Dialog(QuestionActivity.this, R.style.my_dialog);
                    LinearLayout timuDialog_ll = (LinearLayout) LayoutInflater.from(QuestionActivity.this)
                            .inflate(R.layout.timudialog_layout, null);
                    timuDialog_ll.findViewById(R.id.timudialog_iv_love).setOnClickListener(timuListener);
                    timuDialog_ll.findViewById(R.id.timudialog_iv_share).setOnClickListener(timuListener);
                    timuDialog_ll.findViewById(R.id.timudialog_iv_talk).setOnClickListener(timuListener);
                    timuDialog_ll.findViewById(R.id.timudialog_iv_timu).setOnClickListener(timuListener);
                    timuDialog_rv = (RecyclerView) timuDialog_ll.findViewById(R.id.timudialog_rv);
                    timuDialogAdapter = new TimuDialogAdapter(timuLists, this);
                    timuDialogAdapter.setTimuOnClickListener(this);
                    timuDialog_rv.setAdapter(timuDialogAdapter);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 8);
                    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            if (!"".equals(timuLists.get(position).getType()))
                                return 8;
                            else
                                return 1;
                        }
                    });
                    timuDialog_rv.setLayoutManager(gridLayoutManager);
                    timuDialog.setContentView(timuDialog_ll);
                    Window dialogWindow = timuDialog.getWindow();
                    dialogWindow.setGravity(Gravity.BOTTOM);
                    dialogWindow.setWindowAnimations(R.style.dialogstyle);
                    WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                    lp.x = 0;
                    lp.y = 0;
                    lp.width = getResources().getDisplayMetrics().widthPixels;
                    timuDialog_ll.measure(0, 0);
                    lp.height = timuDialog_ll.getMeasuredHeight();
                    lp.alpha = 8f;
                    dialogWindow.setAttributes(lp);
                }
                timuDialog.show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (data != null) {
                    String isChanged = data.getExtras().getString("isChanged");
                    if ("true".equals(isChanged)) {
                        Fragment fragment = frag_list.get(viewPager.getCurrentItem());
                        if (fragment instanceof ChooseQuesFragment) {
                            ((ChooseQuesFragment) fragment).checkOK();
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void timuOnClick(TimuList timuList) {
        viewPager.setCurrentItem(timuList.getFragIndex(), false);
        if (timuDialog != null) {
            timuDialog.cancel();
        }
    }

    public class DialogOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.timudialog_iv_love:
                    if (timuDialog != null)
                        timuDialog.cancel();
                    break;
                case R.id.timudialog_iv_share:
                    break;
                case R.id.timudialog_iv_talk:
                    break;
                case R.id.timudialog_iv_timu:
                    break;
            }
        }
    }
}
