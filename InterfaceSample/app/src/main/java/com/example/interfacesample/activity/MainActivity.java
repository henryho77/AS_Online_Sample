package com.example.interfacesample.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.interfacesample.R;
import com.example.interfacesample.adapter.BookInfoListAdapter;
import com.example.interfacesample.model.BookInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity  {

    private ListView mBookInfoListView;
    private BookInfoListAdapter mBookInfoListAdapter;
    private List<BookInfo> mListBookInfo = new ArrayList<BookInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBookInfoListView = (ListView) findViewById(R.id.bookInfoListView);

        populateBookInfo();
        mBookInfoListAdapter = new BookInfoListAdapter(MainActivity.this, mListBookInfo);
        mBookInfoListView.setAdapter(mBookInfoListAdapter);

    }

    private void populateBookInfo() {
        mListBookInfo.add(new BookInfo("BookA","PriceA","DateA"));
        mListBookInfo.add(new BookInfo("BookB","PriceB","DateB"));
        mListBookInfo.add(new BookInfo("BookC","PriceC","DateC"));
        mListBookInfo.add(new BookInfo("BookD","PriceD","DateD"));
        mListBookInfo.add(new BookInfo("BookE","PriceE","DateE"));
    }

}
