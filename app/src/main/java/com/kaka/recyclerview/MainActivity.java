package com.kaka.recyclerview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.OnMyRecyclerViewClickListener {

    RecyclerViewWrapper mRecyclerView;
    ListView listView;
    private MyRecyclerViewAdapter myRecyclerViewAdapter;
    private MyListViewAdapter myListViewAdapter;
    private List<UserBean> mDatas;
    RecyclerView.RecycledViewPool recycledViewPool;
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler_view);
        listView = findViewById(R.id.list_view);
        initData();
        recycledViewPool = new RecyclerView.RecycledViewPool();
        recycledViewPool.setMaxRecycledViews(MyRecyclerViewAdapter.MY_ITEM_TYPE, 6);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setLayoutListener(new RecyclerViewWrapper.LayoutListener() {
            @Override
            public void onBeforeLayout() {
                try {
                    Field mRecycler =
                            Class.forName("androidx.recyclerview.widget.RecyclerView").getDeclaredField("mRecycler");
                    mRecycler.setAccessible(true);
                    RecyclerView.Recycler recyclerInstance =
                            (RecyclerView.Recycler) mRecycler.get(mRecyclerView);

                    Class<?> recyclerClass = Class.forName(mRecycler.getType().getName());
                    Field mAttachedScrap = recyclerClass.getDeclaredField("mAttachedScrap");
                    mAttachedScrap.setAccessible(true);
                    mAttachedScrap.set(recyclerInstance, new ArrayListWrapper<RecyclerView.ViewHolder>());

                    ArrayList<RecyclerView.ViewHolder> mAttached =
                            (ArrayList<RecyclerView.ViewHolder>) mAttachedScrap.get(recyclerInstance);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onAfterLayout() {

                showMessage(mRecyclerView);
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                showMessage(mRecyclerView);
            }
        });
        myRecyclerViewAdapter = new MyRecyclerViewAdapter(this, mDatas, this);
        mRecyclerView.setAdapter(myRecyclerViewAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

    }

    private void initData() {
        mDatas = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            UserBean userBean = new UserBean();
            userBean.setUserName("测试" + i);
            userBean.setAge(new Random().nextInt());
            userBean.setId("" + i);
            mDatas.add(userBean);
        }
    }


    /**
     * 利用java反射机制拿到RecyclerView内的缓存并打印出来
     * */
    private void showMessage(RecyclerViewWrapper rv) {
        try {
            Field mRecycler =
                    Class.forName("androidx.recyclerview.widget.RecyclerView").getDeclaredField("mRecycler");
            mRecycler.setAccessible(true);
            RecyclerView.Recycler recyclerInstance = (RecyclerView.Recycler) mRecycler.get(rv);

            Class<?> recyclerClass = Class.forName(mRecycler.getType().getName());
            Field mViewCacheMax = recyclerClass.getDeclaredField("mViewCacheMax");
            Field mAttachedScrap = recyclerClass.getDeclaredField("mAttachedScrap");
            Field mChangedScrap = recyclerClass.getDeclaredField("mChangedScrap");
            Field mCachedViews = recyclerClass.getDeclaredField("mCachedViews");
            Field mRecyclerPool = recyclerClass.getDeclaredField("mRecyclerPool");
            mViewCacheMax.setAccessible(true);
            mAttachedScrap.setAccessible(true);
            mChangedScrap.setAccessible(true);
            mCachedViews.setAccessible(true);
            mRecyclerPool.setAccessible(true);


            int mViewCacheSize = (int) mViewCacheMax.get(recyclerInstance);
            ArrayListWrapper<RecyclerView.ViewHolder> mAttached =
                    (ArrayListWrapper<RecyclerView.ViewHolder>) mAttachedScrap.get(recyclerInstance);
            ArrayList<RecyclerView.ViewHolder> mChanged =
                    (ArrayList<RecyclerView.ViewHolder>) mChangedScrap.get(recyclerInstance);
            ArrayList<RecyclerView.ViewHolder> mCached =
                    (ArrayList<RecyclerView.ViewHolder>) mCachedViews.get(recyclerInstance);
            RecyclerView.RecycledViewPool recycledViewPool =
                    (RecyclerView.RecycledViewPool) mRecyclerPool.get(recyclerInstance);

            Class<?> recyclerPoolClass = Class.forName(mRecyclerPool.getType().getName());

            Log.e(TAG, "mAttachedScrap（一缓） size is:" + mAttached.maxSize + ", \n" + "mCachedViews（二缓） max size is:" + mViewCacheSize + ","
                    + getMCachedViewsInfo(mCached) + getRVPoolInfo(recyclerPoolClass, recycledViewPool));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getRVPoolInfo(Class<?> aClass, RecyclerView.RecycledViewPool recycledViewPool) {
        try {
            Field mScrapField = aClass.getDeclaredField("mScrap");
            mScrapField.setAccessible(true);
            SparseArray mScrap = (SparseArray) mScrapField.get(recycledViewPool);

            Class<?> scrapDataClass =
                    Class.forName("androidx.recyclerview.widget.RecyclerView$RecycledViewPool$ScrapData");
            Field mScrapHeapField = scrapDataClass.getDeclaredField("mScrapHeap");
            Field mMaxScrapField = scrapDataClass.getDeclaredField("mMaxScrap");
            mScrapHeapField.setAccessible(true);
            mMaxScrapField.setAccessible(true);
            String s = "\n mRecyclerPool（四缓） info:  ";
            for (int i = 0; i < mScrap.size(); i++) {
                ArrayList<RecyclerView.ViewHolder> item =
                        (ArrayList<RecyclerView.ViewHolder>) mScrapHeapField.get(mScrap.get(i));
                for (int j = 0; j < item.size(); j++) {
                    if (j == item.size() - 1) {
                        s += ">>> ";
                    } else if (j == 0) {
                        s += "mScrap[" + i + "] max size is:" + (mMaxScrapField.get(mScrap.get(i)));
                    }
                    s += "mScrap[" + i + "] 中的 mScrapHeap[" + j + "] info is:" + item.get(j) + "\n";
                }
            }
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            return "  ";
        }
    }

    private String getMCachedViewsInfo(ArrayList<RecyclerView.ViewHolder> viewHolders) {
        String s = "mCachedViews（二缓） info:  ";
        if (viewHolders.size() > 0) {
            int i = 0;
            for (; i < viewHolders.size(); i++) {
                s += "\n mCachedViews[" + i + "] is " + viewHolders.get(i).toString();
            }

            // append   
            if (i == 0) {
                s += "      ";
            } else if (i == 1) {
                s += "    ";
            } else if (i == 2) {
                s += "  ";
            }
        } else {
            s += "      ";
        }
        return s
                + " \n";
    }


    @Override
    public void onUserClick(int position) {
//        Toast.makeText(this,"点击的是"+mDatas.get(position).getUserName(),Toast.LENGTH_SHORT).show();
//        mDatas.remove(position);
//        myRecyclerViewAdapter.notifyItemRemoved(position);
    }
}
