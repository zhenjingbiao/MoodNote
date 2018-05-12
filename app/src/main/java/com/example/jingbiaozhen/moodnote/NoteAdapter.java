package com.example.jingbiaozhen.moodnote;
/*
 * Created by jingbiaozhen on 2018/5/12.
 * 笔记布局适配器
 **/

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.Holder>
{
    private List<NoteBean> mNoteBeans;

    private Context mContext;

    private OnItemClickListener mItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }

    public NoteAdapter(Context context, List<NoteBean> noteBeans)
    {
        super();
        mContext = context;
        if (noteBeans == null)
        {
            mNoteBeans = new ArrayList<>();
        }
        else
        {
            mNoteBeans = noteBeans;
        }

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = View.inflate(mContext, R.layout.item_note, null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position)
    {
        NoteBean noteBean = mNoteBeans.get(position);
        if (noteBean != null)
        {
            holder.descTV.setText(noteBean.decs);
            holder.moodIv.setImageResource(Integer.parseInt(noteBean.mood));
            holder.timeTv.setText(noteBean.time);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(holder.itemView,position);
                }
            });

        }

    }

    @Override
    public int getItemCount()
    {
        return mNoteBeans.size();
    }

    public class Holder extends RecyclerView.ViewHolder
    {
        private TextView descTV;

        private ImageView moodIv;

        private TextView timeTv;

        private RelativeLayout itemLayout;

        public Holder(View itemView)
        {
            super(itemView);
            descTV = itemView.findViewById(R.id.tv_note_title);
            moodIv = itemView.findViewById(R.id.iv_note_mood);
            timeTv = itemView.findViewById(R.id.tv_note_time);
            itemLayout=itemView.findViewById(R.id.note_item_rl);

        }
    }
    public void  setOnItemClickListener(OnItemClickListener listener){
        mItemClickListener=listener;
    }
}
