package com.gin.xjh.usebmob;

import android.Manifest;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobInstallationManager;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.InstallationListener;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.SaveListener;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private EditText mName, mFeedback, mQuery;
    private Button Submit, Query_all, Query, PushAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isPermissionAllGranted(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 3);
        Bmob.initialize(this, "b952ad3a45cb8b6141b2ada8518e19fc");
        BmobInstallationManager.getInstance().initialize(new InstallationListener<BmobInstallation>() {
            @Override
            public void done(BmobInstallation bmobInstallation, BmobException e) {
                if (e != null) {
                    Toast.makeText(MainActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 启动推送服务
        BmobPush.startWork(this);
        initView();
        initEvent();
    }

    private void initView() {
        mName = findViewById(R.id.name);
        mFeedback = findViewById(R.id.feedback);
        Submit = findViewById(R.id.Submit);
        Query_all = findViewById(R.id.Query_all);
        Query = findViewById(R.id.Query);
        mQuery = findViewById(R.id.query_et);
        PushAll = findViewById(R.id.PushAll);
    }

    private void initEvent() {
        Submit.setOnClickListener(this);
        Query_all.setOnClickListener(this);
        Query.setOnClickListener(this);
        PushAll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Submit:
                String name = mName.getText().toString();
                String feedback = mFeedback.getText().toString();
                if (name.equals("") || feedback.equals("")) {
                    Toast.makeText(this, "不能传入空字符", Toast.LENGTH_SHORT).show();
                    return;
                }
                Feedback feedbackObj = new Feedback();
                feedbackObj.setName(name);
                feedbackObj.setFeedback(feedback);
                feedbackObj.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            Toast.makeText(MainActivity.this, "Submit Success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Submit Failure", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.Query_all:
                BmobQuery<Feedback> queryall = new BmobQuery<Feedback>();
                queryall.findObjects(new FindListener<Feedback>() {
                    @Override
                    public void done(List<Feedback> list, BmobException e) {
                        if (e == null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Query");
                            String str = "";
                            for (Feedback feedback : list) {
                                str += feedback.getName() + ":" + feedback.getFeedback() + "\n";
                            }
                            builder.setMessage(str);
                            builder.create().show();
                        } else {
                            Toast.makeText(MainActivity.this, "Query Failure", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.Query:
                String str = mQuery.getText().toString();
                if (str.equals("")) {
                    Toast.makeText(this, "不能传入空字符", Toast.LENGTH_SHORT).show();
                }
                BmobQuery<Feedback> query = new BmobQuery<Feedback>();
                query.addWhereEqualTo("name", str);
                query.findObjects(new FindListener<Feedback>() {
                    @Override
                    public void done(List<Feedback> list, BmobException e) {
                        if (e == null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Query");
                            String str = "";
                            for (Feedback feedback : list) {
                                str += feedback.getName() + ":" + feedback.getFeedback() + "\n";
                            }
                            builder.setMessage(str);
                            builder.create().show();
                        } else {
                            Toast.makeText(MainActivity.this, "Query Failure", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.PushAll:
                BmobPushManager push = new BmobPushManager();
                push.pushMessageAll("Test", new PushListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            //Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
    }
}