package com.example.jingbiaozhen.moodnote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
{

    @BindView(R.id.add_note_btn)
    Button mAddNoteBtn;

    @BindView(R.id.note_recyclerView)
    RecyclerView mNoteRecyclerView;

    private List<NoteBean> mNoteBeans=new ArrayList<>();

    private SQLiteDatabase db;

    private DatabaseOperation dop;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        dop = new DatabaseOperation(this, db);
        initNoteRecyclerView();
        // 数据库操作
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        initData();
    }

    private void initData()
    {

        mNoteBeans.clear();
        // 创建或打开数据库
        dop.createDb();
        Cursor cursor = dop.queryDb();

        while (cursor.moveToNext())
        {
            // String content, String imagepath, String voicepath, String mood,String
            // time, int item_ID
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String imagepath = cursor.getString(cursor.getColumnIndex("imagepath"));
            String voicepath = cursor.getString(cursor.getColumnIndex("voicepath"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            String mood = cursor.getString(cursor.getColumnIndex("mood"));
            NoteBean noteBean = new NoteBean();
            noteBean.id = id;
            noteBean.decs = content;
            if(!TextUtils.isEmpty(imagepath)){
                noteBean.imagePaths = new ArrayList<>(Arrays.asList(imagepath.split("\\*")));
            }else {
                noteBean.imagePaths=null;
            }
            if(!TextUtils.isEmpty(voicepath)){
                noteBean.voicePaths =new ArrayList<>(Arrays.asList(voicepath.split("\\*"))) ;
            }else {
                noteBean.voicePaths=null;
            }
            noteBean.mood = mood;
            noteBean.time = time;
            mNoteBeans.add(noteBean);
        }
        dop.closeDb();

        // 设置Adapter
        NoteAdapter adapter = new NoteAdapter(this, mNoteBeans);
        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent(MainActivity.this,EditNoteActivity.class);
                intent.putExtra("noteItemData",mNoteBeans.get(position));
                startActivity(intent);
            }
        });
        mNoteRecyclerView.setAdapter(adapter);
    }

    private void initNoteRecyclerView()
    {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // 设置布局管理器
        mNoteRecyclerView.setLayoutManager(layoutManager);
        // 设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);

        // 设置分隔线
        mNoteRecyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        // 设置增加或删除条目的动画
        mNoteRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @OnClick(R.id.add_note_btn)
    public void onViewClicked()
    {
       startActivity(new Intent(this,EditNoteActivity.class));

    }
}
