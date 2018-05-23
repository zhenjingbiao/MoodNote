package com.example.jingbiaozhen.moodnote;
/*
 * Created by jingbiaozhen on 2018/5/15.
 **/

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class SelectImageAdapter extends RecyclerView.Adapter<SelectImageAdapter.ViewHolder>
{

    private Context mContext;

    private List<String> mPaths;

    private OnImageSelectListener mImageSelectListener;

    public SelectImageAdapter(Context context, List<String> paths)
    {
        mContext = context;
        mPaths = paths;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = View.inflate(mContext, R.layout.item_select_image, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        if (mPaths != null)
        {
            final String path = mPaths.get(position);
            if (!TextUtils.isEmpty(path))
            {
                /*Bitmap bm = BitmapFactory.decodeFile(path);
                holder.mImageView.setImageBitmap(bm);*/

                Glide.with(mContext).load(path).into(holder.mImageView);

                holder.mImageView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mImageSelectListener.onImageClick(holder.mImageView, path);
                    }
                });
                holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                    {
                        mImageSelectListener.onImageCheck(path, isChecked,holder.mCheckBox);
                    }
                });
            }

        }
    }

    @Override
    public int getItemCount()
    {
        return mPaths.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private CheckBox mCheckBox;

        private ImageView mImageView;

        public ViewHolder(View itemView)
        {
            super(itemView);
            mCheckBox = itemView.findViewById(R.id.image_select_cb);
            mImageView = itemView.findViewById(R.id.item_iv);
        }
    }

    public void setImageSelectListener(OnImageSelectListener listener)
    {
        mImageSelectListener = listener;
    }

    public interface OnImageSelectListener
    {
        void onImageCheck(String path, boolean isCheck,CheckBox checkBox);

        void onImageClick(View view, String path);
    }
}
