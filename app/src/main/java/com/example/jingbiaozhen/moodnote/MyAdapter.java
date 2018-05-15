package com.example.jingbiaozhen.moodnote;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

//首页笔记列表适配器
public class MyAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<NoteBean> mList;

    public MyAdapter(Context context, List<NoteBean> list) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder(mInflater.inflate(R.layout.recyclerview_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        BaseViewHolder viewHolder = (BaseViewHolder) holder;
        NoteBean noteBean=mList.get(position);
        if (noteBean != null) {

            viewHolder.setText(R.id.tv_note_title,noteBean.decs);
            viewHolder.setText(R.id.tv_note_time,noteBean.time);
            viewHolder.setImageResource(R.id.iv_note_mood,Integer.parseInt(noteBean.mood));
        }
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void removeItem(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
    }
}
