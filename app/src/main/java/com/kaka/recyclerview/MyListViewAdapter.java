package com.kaka.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Xiaoqi.
 * Date:2019-05-23 11:31.
 * Project:RecyclerViewDemo.
 */
public class MyListViewAdapter extends BaseAdapter {

    private Context mContext;

    private List<UserBean> mDatas;

    public MyListViewAdapter(Context mContext, List<UserBean> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ListViewHolder listViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list, null);
            listViewHolder = new ListViewHolder(convertView);
            convertView.setTag(listViewHolder);
        }else {
            listViewHolder = (ListViewHolder) convertView.getTag();
        }
        listViewHolder.tvName.setText(mDatas.get(position).getUserName());
        listViewHolder.tvAge.setText(mDatas.get(position).getAge() + "");
        listViewHolder.tvSex.setText(mDatas.get(position).getSex());

        return convertView;
    }


    static public class ListViewHolder {
        TextView tvName;
        TextView tvAge;
        TextView tvSex;

        public ListViewHolder(View itemView) {
            tvName = itemView.findViewById(R.id.tv_name);
            tvAge = itemView.findViewById(R.id.tv_age);
            tvSex = itemView.findViewById(R.id.tv_sex);
        }
    }

}
