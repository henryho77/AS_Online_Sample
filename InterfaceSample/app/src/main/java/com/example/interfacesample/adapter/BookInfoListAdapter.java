package com.example.interfacesample.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.interfacesample.R;
import com.example.interfacesample.model.BookInfo;

import java.util.List;

/**
 * Created by HenryHo on 2016/9/11.
 */
public class BookInfoListAdapter extends BaseAdapter {

    private Context mContext;
    private List<BookInfo> mListBookInfo;

    public BookInfoListAdapter(Context context, List<BookInfo> listBookInfo) {
        this.mContext = context;
        this.mListBookInfo = listBookInfo;
    }

    @Override
    public int getCount() {
//        return 0;
        return  mListBookInfo.size();
    }

    @Override
    public Object getItem(int position) {
//        return null;
        return mListBookInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder mViewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.bookinfo_list_adapter, parent, false);
            mViewHolder = new ViewHolder();
            mViewHolder.bookIcon = (ImageView) convertView.findViewById(R.id.imgIcon);
            mViewHolder.bookTitle = (TextView) convertView.findViewById(R.id.txtBookTitle);
            mViewHolder.bookPrice = (TextView) convertView.findViewById(R.id.txtBookPrice);
            mViewHolder.bookDate = (TextView) convertView.findViewById(R.id.txtBookDate);
            mViewHolder.bookCheckBox = (CheckBox) convertView.findViewById(R.id.checkBox);

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.bookIcon.setImageResource(R.mipmap.ic_launcher);
        mViewHolder.bookTitle.setText(mListBookInfo.get(position).getTitle());
        mViewHolder.bookPrice.setText(mListBookInfo.get(position).getPrice());
        mViewHolder.bookDate.setText(mListBookInfo.get(position).getDate());
        mViewHolder.bookCheckBox.setChecked(false);

        mViewHolder.bookCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewHolder.bookCheckBox.isChecked())
                    mViewHolder.bookCheckBox.setBackgroundColor(mContext.getResources().getColor(R.color.colorPurple));
                else
                    mViewHolder.bookCheckBox.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        private ImageView bookIcon;
        private TextView bookTitle;
        private TextView bookPrice;
        private TextView bookDate;
        private CheckBox bookCheckBox;
    }


}
