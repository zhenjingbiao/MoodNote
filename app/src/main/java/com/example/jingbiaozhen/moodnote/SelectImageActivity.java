package com.example.jingbiaozhen.moodnote;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 * Created by jingbiaozhen on 2018/5/15.
 **/

public class SelectImageActivity extends Activity
{
    private static final int CODE_SELECT_PATHS = 100;

    @BindView(R.id.back_btn)
    Button mBackBtn;

    @BindView(R.id.complete_select_tv)
    TextView mCompleteSelectTv;

    @BindView(R.id.images_recyclerView)
    RecyclerView mImagesRecyclerView;

    private List<String> mImageUrls;

    private int mSelectedCount;

    private ArrayList<String> mSelectImageUrls = new ArrayList<>();

    private SelectImageAdapter mSelectImageAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);
        ButterKnife.bind(this);
        initImages();
        initView();

    }

    private void initView()
    {
        int spanCount = 3; // 3 columns
        GridLayoutManager manager = new GridLayoutManager(this, spanCount);
        mImagesRecyclerView.setLayoutManager(manager);
        int spacing = 15; // 50px
        mImagesRecyclerView.addItemDecoration(new ImageItemDecoration(spanCount, spacing, false));
        mSelectImageAdapter = new SelectImageAdapter(this, mImageUrls);
        mImagesRecyclerView.setAdapter(mSelectImageAdapter);
        mSelectImageAdapter.setImageSelectListener(new SelectImageAdapter.OnImageSelectListener()
        {
            @Override
            public void onImageCheck(String path, boolean isCheck, CheckBox checkBox)
            {
                if (mSelectedCount > 8)
                {
                    checkBox.setChecked(false);
                    Toast.makeText(SelectImageActivity.this, "最多只能选择9张图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (isCheck)
                {
                    mSelectImageUrls.add(path);
                    mSelectedCount++;
                }
                else
                {
                    if (mSelectImageUrls.contains(path))
                    {
                        mSelectImageUrls.remove(path);
                        mSelectedCount--;
                    }
                }
                mCompleteSelectTv.setText(String.format("完成(%d/9)", mSelectedCount));

            }

            @Override
            public void onImageClick(View view, String path)
            {
                // TODO 展示大图
                Intent intent = new Intent(SelectImageActivity.this, ShowPictureActivity.class);
                intent.putExtra("imgPath", path);
                startActivity(intent);
            }
        });
    }

    private void initImages()
    {
        mImageUrls = new ArrayList<>();

        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        Cursor imageCursor = this.getApplicationContext().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy + " DESC");
        for (int i = 0; i < imageCursor.getCount(); i++)
        {
            imageCursor.moveToPosition(i);
            int dataColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
            mImageUrls.add(imageCursor.getString(dataColumnIndex));
            Log.i("imageUrl", mImageUrls.get(i));
        }
        imageCursor.close();
    }

    @OnClick({R.id.back_btn, R.id.complete_select_tv})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.back_btn:
                finish();
                break;
            case R.id.complete_select_tv:
                Intent intent = new Intent();
                intent.putStringArrayListExtra("selectedImagePaths", mSelectImageUrls);
                setResult(CODE_SELECT_PATHS, intent);
                finish();
                break;
            default:
                break;
        }
    }
}
