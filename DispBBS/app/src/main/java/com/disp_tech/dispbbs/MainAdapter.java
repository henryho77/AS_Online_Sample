package com.disp_tech.dispbbs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Knuckles on 2015/12/22.
 */
public class MainAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater mInflater;
    JSONArray mJsonArray;

    // 用來儲存row裡每個view的id，以免每次都要取一次
    private static class ViewHolder {
        public ImageView thumbImageView;
        public TextView titleTextView;
        public TextView descTextView;
    }

    // 類別的建構子
    public MainAdapter(Context context, LayoutInflater inflater) {
        mContext = context;
        mInflater = inflater;
        mJsonArray = new JSONArray();
    }

    // 輸入JSON資料
    public void updateData(JSONArray jsonArray) {
        mJsonArray = jsonArray;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mJsonArray.length();
    }

    @Override
    public Object getItem(int position) {
        return mJsonArray.optJSONObject(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // 檢查view是否已存在，如果已存在就不用再取一次id
        if (convertView == null) {
            // Inflate the custom row layout from your XML.
            convertView = mInflater.inflate(R.layout.row_main, null);
            // create a new "Holder" with subviews
            holder = new ViewHolder();
            holder.thumbImageView = (ImageView) convertView.findViewById(R.id.img_thumb);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.text_title);
            holder.descTextView = (TextView) convertView.findViewById(R.id.text_desc);
            // hang onto this holder for future recyclage
            convertView.setTag(holder);
        } else {
            // skip all the expensive inflation/findViewById
            // and just get the holder you already made
            holder = (ViewHolder) convertView.getTag();
        }

        // 取得目前這個Row的JSON資料
        JSONObject jsonObject = (JSONObject) getItem(position);

        Boolean hasThumb = false;
        if (jsonObject.has("img_list")) {
            JSONArray img_list = jsonObject.optJSONArray("img_list");
            if(img_list.length()!=0) {
                String imageURL = img_list.optString(0);
                // 使用 Picasso 來載入網路上的圖片
                // 圖片載入前先用placeholder顯示預設圖片
                Picasso.with(mContext).load(imageURL).placeholder(R.drawable.displogo300).into(holder.thumbImageView);
                hasThumb = true;
            }
        }
        if(!hasThumb){ // 沒有縮圖的話放 disp logo
            holder.thumbImageView.setImageResource(R.drawable.displogo300);
        }

        // 從JSON資料取得標題和摘要
        String title = "";
        String desc = "";
        if (jsonObject.has("title")) {
            title = jsonObject.optString("title");
        }
        if (jsonObject.has("desc")) {
            desc = jsonObject.optString("desc");
        }

        // 將標題和摘要顯示在TextView上
        holder.titleTextView.setText(title);
        holder.descTextView.setText(desc);

        return convertView;
    }

}
