package com.example.jingbiaozhen.moodnote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jingbiaozhen.moodnote.view.CircleCrop;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener

{

    private static final int IMAGE_REQUEST_CODE = 1;

    @BindView(R.id.add_note_btn)
    FloatingActionButton mAddNoteBtn;

    @BindView(R.id.note_recyclerView)
    DeleteRecyclerView mNoteRecyclerView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    private ImageView mAvatarIv;

    private EditText mSignatureEt;

    private List<NoteBean> mNoteBeans = new ArrayList<>();

    private SQLiteDatabase db;

    private DatabaseOperation dop;

    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
        initNav();
        // 数据库操作
        dop = new DatabaseOperation(this, db);
        initNoteRecyclerView();
    }

    private void initNav()
    {
        LinearLayout menuHeadLayout = (LinearLayout) mNavigationView.inflateHeaderView(R.layout.menu_head);
        mAvatarIv = menuHeadLayout.findViewById(R.id.avatar_iv);
        mSignatureEt = menuHeadLayout.findViewById(R.id.signature_et);
        mAvatarIv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
            default:
        }
        return true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        initData();
        String signature = getSharedPreferences("signature", MODE_PRIVATE).getString("signature", "");
        String avatarPath = getSharedPreferences("signature", MODE_PRIVATE).getString("avatar", "");
        if (!TextUtils.isEmpty(signature))
        {

            mSignatureEt.setText(signature);
        }
        if (!TextUtils.isEmpty(avatarPath))
        {
            Glide.with(this).load(avatarPath).transform(new CircleCrop(this)).into(mAvatarIv);
        }
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
            if (!TextUtils.isEmpty(imagepath))
            {
                noteBean.imagePaths = new ArrayList<>(Arrays.asList(imagepath.split("\\*")));
            }
            else
            {
                noteBean.imagePaths = null;
            }
            if (!TextUtils.isEmpty(voicepath))
            {
                noteBean.voicePaths = new ArrayList<>(Arrays.asList(voicepath.split("\\*")));
            }
            else
            {
                noteBean.voicePaths = null;
            }
            noteBean.mood = mood;
            noteBean.time = time;
            mNoteBeans.add(noteBean);
        }
        dop.closeDb();

        // 设置Adapter
        adapter = new MyAdapter(this, mNoteBeans);
        mNoteRecyclerView.setAdapter(adapter);
        mNoteRecyclerView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                intent.putExtra("noteItemData", mNoteBeans.get(position));
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(int position)
            {
                removeItemFromDB(mNoteBeans.get(position));
                mNoteBeans.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 从数据库中删除这条数据
     */
    private void removeItemFromDB(NoteBean noteBean)
    {
        if (noteBean != null)
        {
            // 创建或打开数据库
            dop.createDb();
            dop.deleteDb(noteBean.id);
            dop.closeDb();
        }

    }

    // 初始化笔记列表
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
        startActivity(new Intent(this, EditNoteActivity.class));

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        SharedPreferences.Editor editor = getSharedPreferences("signature", MODE_PRIVATE).edit();
        if (mSignatureEt != null)
        {
            String signature = mSignatureEt.getText().toString();
            if (!TextUtils.isEmpty(signature))
            {
                editor.putString("signature", signature);
            }
        }
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == IMAGE_REQUEST_CODE)
        {
            Uri selectedImage = data.getData(); // 获取系统返回的照片的Uri
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);// 从系统表中查询指定Uri对应的照片
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String path = cursor.getString(columnIndex); // 获取照片路径
            Glide.with(this).load(path).transform(new CircleCrop(this)).into(mAvatarIv);
            SharedPreferences.Editor editor = getSharedPreferences("signature", MODE_PRIVATE).edit();
            if (mSignatureEt != null)
            {
                String signature = mSignatureEt.getText().toString();
                if (!TextUtils.isEmpty(signature))
                {
                    editor.putString("avatar", path);
                }
            }
            editor.apply();
            cursor.close();
        }
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.nav_camera:
                Toast.makeText(this, "您能提出您的建议嘛？", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_gallery:
                Toast.makeText(this, "这是一个本地项目", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_slideshow:
                Toast.makeText(this, "敬请期待", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_manage:
                Toast.makeText(this, "期待和您完善项目", Toast.LENGTH_SHORT).show();
                break;
            default:

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
