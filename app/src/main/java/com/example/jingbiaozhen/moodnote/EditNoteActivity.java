package com.example.jingbiaozhen.moodnote;
/*
 * Created by jingbiaozhen on 2018/5/12.
 * 添加或者编辑笔记的Activity
 * 
 **/

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jingbiaozhen.moodnote.view.CustomPopView;
import com.example.jingbiaozhen.moodnote.view.SpacesItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditNoteActivity extends Activity implements IPopView
{
    private static final int IMAGE_PICKER = 3;

    private static final String TAG = "EditNoteActivity";

    private static final int CODE_SELECT_PATHS = 100;

    @BindView(R.id.save_note_btn)
    Button mSaveNoteBtn;

    @BindView(R.id.add_image_btn)
    Button mAddImageBtn;

    @BindView(R.id.image_recyclerView)
    RecyclerView mImageRecyclerView;

    @BindView(R.id.add_voice_btn)
    Button mAddVoiceBtn;

    @BindView(R.id.voice_recyclerView)
    RecyclerView mVoiceRecyclerView;

    private NoteBean mCurrentNote;

    private NoteBean mLastNote;

    @BindView(R.id.selected_iv)
    ImageView mSelectedIv;

    @BindView(R.id.select_btn)
    Button mSelectBtn;

    @BindView(R.id.note_input_et)
    EditText mNoteInputEt;

    private SQLiteDatabase db;

    private DatabaseOperation dop;

    private List<Integer> mExpressImages = new ArrayList<>();

    private boolean isEditNote;// 区分编辑和新建

    private Integer currentPos;

    private CustomPopView mCustomPopView;

    private AddImagesAdapter mImageAdapter;

    private AddImagesAdapter mVoiceAdapter;

    private List<String> mImagePaths;

    private List<String> mVoicePaths;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        ButterKnife.bind(this);

        initExpress();
        initView();
        initData();
    }

    // 初始化图片或者语音列表
    private void initView()
    {
        // 设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        SpacesItemDecoration imageItemDecoration=new SpacesItemDecoration(6);
        SpacesItemDecoration voiceItemDecoration=new SpacesItemDecoration(6);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        layoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        mImageRecyclerView.setLayoutManager(layoutManager);
        mImageRecyclerView.addItemDecoration(imageItemDecoration);
        mVoiceRecyclerView.setLayoutManager(layoutManager1);
        mVoiceRecyclerView.addItemDecoration(voiceItemDecoration);
        mCustomPopView = new CustomPopView(this, this, mExpressImages);

    }

    /**
     * 初始化数据 如果有MainActivity传过来的数据则是编辑笔记，如果没有回传的数据则是新建
     */
    private void initData()
    {
        Intent intent = getIntent();
        NoteBean noteBean = (NoteBean) intent.getSerializableExtra("noteItemData");
        dop = new DatabaseOperation(this, db);
        if (noteBean != null)
        {// 表明是编辑
            mLastNote = noteBean;
            mCurrentNote = noteBean;
            isEditNote = true;
            Log.d(TAG, "initData: noteBean" + noteBean.toString());
            initNoteData();
        }
        else
        {
            isEditNote = false;
        }
        if (mImagePaths != null && mImagePaths.size() > 0)
        {
            for (String imagePath : mImagePaths)
            {
                if(!TextUtils.isEmpty(imagePath)&&!imagePath.equals("null")){
                    mImageRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        }
        else
        {
            mImagePaths = new ArrayList<>();
            mImageRecyclerView.setVisibility(View.GONE);
        }
        // 设置图片列表适配器
        mImageAdapter = new AddImagesAdapter(EditNoteActivity.this, mImagePaths);
        mImageAdapter.setOnItemClickListener(new AddImagesAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                Intent intent = new Intent(EditNoteActivity.this, ShowPictureActivity.class);
                intent.putExtra("imgPath", mImagePaths.get(position));
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(View view, int position)
            {
                mImagePaths.remove(position);
                updateImage();
            }
        });
        mImageRecyclerView.setAdapter(mImageAdapter);
        if (mVoicePaths != null && mVoicePaths.size() > 0)
        {
            for (String voicePath : mVoicePaths)
            {
                if (!TextUtils.isEmpty(voicePath) && !voicePath.equals("null"))
                {
                    mVoiceRecyclerView.setVisibility(View.VISIBLE);
                }

            }
        }
        else
        {
            mVoicePaths = new ArrayList<>();
            mVoiceRecyclerView.setVisibility(View.GONE);
        }
        // 设置语音列表适配器
        mVoiceAdapter = new AddImagesAdapter(EditNoteActivity.this, mVoicePaths);
        mVoiceAdapter.setOnItemClickListener(new AddImagesAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {

                Intent intent = new Intent(EditNoteActivity.this, ShowRecordActivity.class);
                intent.putExtra("audioPath", mVoicePaths.get(position));
                Log.d(TAG, "onItemClick: audioPath" + mVoicePaths.get(position));
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(View view, int position)
            {
                mVoicePaths.remove(position);
                updateVoice();
            }
        });
        mVoiceRecyclerView.setAdapter(mVoiceAdapter);
    }

    // 恢复之前的数据
    private void initNoteData()
    {
        mSelectedIv.setImageResource(Integer.parseInt(mLastNote.mood));
        mNoteInputEt.setText(mLastNote.decs);
        if (mLastNote.imagePaths != null && mLastNote.imagePaths.size() > 0)
        {

            mImagePaths = mLastNote.imagePaths;
        }
        if (mLastNote.voicePaths != null && mLastNote.voicePaths.size() > 0)
        {

            mVoicePaths = mLastNote.voicePaths;
        }

    }

    // 初始化表情的集合
    private void initExpress()
    {
        mExpressImages.add(R.drawable.express1);
        mExpressImages.add(R.drawable.express2);
        mExpressImages.add(R.drawable.express3);
        mExpressImages.add(R.drawable.express4);
        mExpressImages.add(R.drawable.express5);
        mExpressImages.add(R.drawable.express6);
        mExpressImages.add(R.drawable.express7);
        mExpressImages.add(R.drawable.express8);
        mExpressImages.add(R.drawable.express9);

    }

    @Override
    public void setPostion(int postion)
    {
        currentPos = postion;
        mSelectedIv.setImageResource(mExpressImages.get(postion));
    }

    // 各种点击事件
    @OnClick({R.id.save_note_btn, R.id.select_btn, R.id.add_image_btn, R.id.add_voice_btn})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.save_note_btn:
                if (isEditNote)
                {
                    updateNote();
                }
                else
                {
                    insertNote();
                }

                break;
            case R.id.select_btn:
                mCustomPopView.showAtDropDownCenter(mSelectBtn);
                break;
            case R.id.add_image_btn:
                // 添加图片
                // 调用相册
                /*
                 * Intent intent = new Intent(Intent.ACTION_PICK,
                 * android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                 * startActivityForResult(intent, IMAGE_PICKER);
                 */
                // 自定义批量添加图片
                Intent intent = new Intent(EditNoteActivity.this, SelectImageActivity.class);
                startActivityForResult(intent, CODE_SELECT_PATHS);
                break;
            case R.id.add_voice_btn:
                // 添加语音
                Intent intent1 = new Intent(EditNoteActivity.this, RecordActivity.class);
                startActivityForResult(intent1, 4);
                break;
            default:
                break;
        }
    }

    // 插入笔记
    private void insertNote()
    {
        String content = mNoteInputEt.getText().toString();
        if (TextUtils.isEmpty(content))
        {
            Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // 取得当前时间
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
            String time = formatter.format(curDate);
            int position = currentPos == null ? 0 : currentPos;
            String mood = mExpressImages.get(position) + "";
            StringBuilder imageSb = new StringBuilder();
            String imagepath = null;
            if (mImagePaths != null && mImagePaths.size() > 0)
            {
                int imageCount = mImagePaths.size();
                for (int i = 0; i < imageCount - 1; i++)
                {
                    imageSb.append(mImagePaths.get(i)).append("*");
                }
                imageSb.append(mImagePaths.get(imageCount - 1));
                imagepath = imageSb.toString();
            }
            StringBuilder voiceSb = new StringBuilder();
            String voicepath = null;
            if (mVoicePaths != null && mVoicePaths.size() > 0)
            {
                int voiceCount = mVoicePaths.size();
                for (int i = 0; i < voiceCount - 1; i++)
                {
                    voiceSb.append(mVoicePaths.get(i)).append("*");
                }
                voiceSb.append(mVoicePaths.get(voiceCount - 1));
                voicepath = voiceSb.toString();
            }
            dop.createDb();
            dop.insertDb(content, imagepath, voicepath, mood, time);
            dop.closeDb();
            finish();
        }
    }

    // 更新笔记
    private void updateNote()
    {
        String content = mNoteInputEt.getText().toString();
        if (TextUtils.isEmpty(content))
        {
            Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // 取得当前时间
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
            String time = formatter.format(curDate);
            String mood = "";
            if (currentPos != null)
            {
                mood = mExpressImages.get(currentPos) + "";
            }
            else
            {
                mood = mLastNote.mood;
            }
            StringBuilder imageSb = new StringBuilder();
            if (mImagePaths != null && mImagePaths.size() > 0)
            {
                int imageCount = mImagePaths.size();
                for (int i = 0; i < imageCount - 1; i++)
                {
                    imageSb.append(mImagePaths.get(i)).append("*");
                }
                imageSb.append(mImagePaths.get(imageCount - 1));
            }

            String imagepath = imageSb.toString();
            StringBuilder voiceSb = new StringBuilder();
            if (mVoicePaths != null && mVoicePaths.size() > 0)
            {
                int voiceCount = mVoicePaths.size();
                for (int i = 0; i < voiceCount - 1; i++)
                {
                    voiceSb.append(mVoicePaths.get(i)).append("*");
                }
                voiceSb.append(mVoicePaths.get(voiceCount - 1));
            }

            String voicepath = voiceSb.toString();
            dop.createDb();
            dop.updateDb(content, imagepath, voicepath, mood, time, mLastNote.id);
            dop.closeDb();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == 4)
        {
            Bundle extras = data.getExtras();
            String path = extras.getString("audio");
            Log.d(TAG, "onActivityResult: audio" + path);
            mVoicePaths.add(path);
            updateVoice();
        }
        else if (data != null && requestCode == IMAGE_PICKER)
        {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            if (c != null)
            {
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePathColumns[0]);
                String imagePath = c.getString(columnIndex);
                mImagePaths.add(imagePath);
                updateImage();
            }

        }
        else if (data != null && resultCode == CODE_SELECT_PATHS)
        {
            List<String> imagePaths = data.getStringArrayListExtra("selectedImagePaths");
            if (imagePaths != null)
            {
                mImagePaths.clear();
                mImagePaths.addAll(imagePaths);
                updateImage();
            }
        }

    }

    private void updateVoice()
    {

        if (mVoicePaths != null && mVoicePaths.size() > 0)
        {

            mVoiceRecyclerView.setVisibility(View.VISIBLE);
            mVoiceAdapter.notifyDataSetChanged();
        }
        else
        {
            mVoiceRecyclerView.setVisibility(View.GONE);
        }
    }

    private void updateImage()
    {
        if (mImagePaths != null && mImagePaths.size() > 0)
        {
            mImageRecyclerView.setVisibility(View.VISIBLE);
            mImageAdapter.notifyDataSetChanged();
        }
        else
        {
            mImageRecyclerView.setVisibility(View.GONE);
        }
    }

}
