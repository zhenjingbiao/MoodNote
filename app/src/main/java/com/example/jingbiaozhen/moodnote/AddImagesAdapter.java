package com.example.jingbiaozhen.moodnote;
/*
 * Created by jingbiaozhen on 2018/5/12.
 * 添加图片或者语音的适配器
 **/

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class AddImagesAdapter extends RecyclerView.Adapter<AddImagesAdapter.Holder>
{
    private List<String> mImagePaths;

    private Context mContext;

    private OnItemClickListener mItemClickListener;

    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);

        void onDeleteClick(View view, int position);
    }

    public AddImagesAdapter(Context context, List<String> noteBeans)
    {
        super();
        mContext = context;
        /*
         * if (noteBeans == null) { mImagePaths = new ArrayList<>(); } else {
         * mImagePaths = noteBeans; }
         */
        mImagePaths = noteBeans;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = View.inflate(mContext, R.layout.item_image, null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position)
    {
        if (mImagePaths != null && mImagePaths.size() > 0)
        {
            String imagePath = mImagePaths.get(position);
            if (!TextUtils.isEmpty(imagePath))
            {
                if (imagePath.length() > 3)
                {
                    // 取出路径的后缀
                    String type = imagePath.substring(imagePath.length() - 3, imagePath.length());
                    if (type.equals("amr"))
                    {
                        holder.addImageIV.setImageResource(R.drawable.record_icon);
                    }
                    else
                    {
                        Bitmap bm = BitmapFactory.decodeFile(imagePath);
                        holder.addImageIV.setImageBitmap(bm);
                    }
                    holder.itemView.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            mItemClickListener.onItemClick(holder.itemView, position);

                        }
                    });
                    holder.deleteIv.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            mItemClickListener.onDeleteClick(holder.itemView, position);
                        }
                    });
                }
            }

        }

    }

    @Override
    public int getItemCount()
    {
        return mImagePaths.size();
    }

    public class Holder extends RecyclerView.ViewHolder
    {
        private ImageView addImageIV;

        private ImageView deleteIv;

        public Holder(View itemView)
        {
            super(itemView);
            addImageIV = itemView.findViewById(R.id.add_image_iv);
            deleteIv = itemView.findViewById(R.id.delete_iv);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mItemClickListener = listener;
    }
}
