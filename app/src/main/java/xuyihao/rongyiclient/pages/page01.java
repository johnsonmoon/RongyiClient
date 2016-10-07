package xuyihao.rongyiclient.pages;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import xuyihao.JohnsonHttpConnector.connectors.http.RequestSender;
import xuyihao.rongyiclient.MainActivity;
import xuyihao.rongyiclient.entity.Accounts;
import xuyihao.rongyiclient.widget.*;
import xuyihao.rongyiclient.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by xuyihao on 2016/3/5.
 */
public class page01{

    private Accounts accounts = MainActivity.accounts;
    private SQLiteDatabase database = MainActivity.database;
    private RequestSender sender = MainActivity.sender;

    private View page01;//page01的布局文件生成的view，并且是MainActivity中显示的布局
    private View page01_2part;//page01的第二部分，用来加入listview头部
    private Activity context;//上文Activity

    //按钮
    private ImageButton btnFocus;
    private ImageButton btnHot;
    private ImageButton btnTeach;
    private ImageButton btnNear;
    private ImageButton btnGood;

    //推荐文章、视频列表
    private ListView mListview;
    private ArrayList<HashMap<String, Object>> mapList;
    private String[] item_Type = {"[分享]", "[视频]", "[视频]"};
    private String[] item_Title = {"护肤小哥带你飞", "校园女神的护肤秘籍", "专业发型师教你如何穿搭"};
    private String[] item_ReadNum = {"22", "1000+", "10000+"};
    private int[] item_Pictures = {R.drawable.share_1, R.drawable.share_2, R.drawable.share_3};


    private MyNoScrollViewPager mImageCycleViewPager;
    private PagerAdapter mPagerAdapter;
    private List<View> mViews = new ArrayList<View>();
    private int[] imageIds = {R.drawable.pic_2, R.drawable.pic_3, R.drawable.pic_4, R.drawable.pic_5, R.drawable.pic_6};//图片资源
    private ImageView[] points;//装小点的数组
    private ViewGroup group;
    private int currentCycleImage;//当前显示的小图片
    //设置线程操作
    private android.os.Handler mHandler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if(currentCycleImage == imageIds.length-1){
                    currentCycleImage = -1;
                }
                currentCycleImage++;
                mImageCycleViewPager.setCurrentItem(currentCycleImage);
            } else {

            }
            super.handleMessage(msg);
        }
    };


    /**
     * 构造器，一参为上文，二参为上文的布局view
     * */
    public page01(Activity ac, View view){
        context = ac;
        page01 = view;
        LayoutInflater in = LayoutInflater.from(context);
        page01_2part = in.inflate(R.layout.page01_2part_layout, null);
        this.database = database;
        init();
        initPoint();
        initImageCycle();
        initButtonEvent();
        initListView();
    }

    /*
    *设置初始化条件，绑定控件
    * */
    private void init(){
        mImageCycleViewPager = (MyNoScrollViewPager)page01_2part.findViewById(R.id.page01_imageCycleViewPager);
        group = (ViewGroup)page01_2part.findViewById(R.id.page01_viewGroupPiont);
        btnFocus = (ImageButton)page01_2part.findViewById(R.id.page01_button_focus);
        btnGood = (ImageButton)page01_2part.findViewById(R.id.page01_button_good);
        btnHot = (ImageButton)page01_2part.findViewById(R.id.page01_button_hot);
        btnNear = (ImageButton)page01_2part.findViewById(R.id.page01_button_near);
        btnTeach = (ImageButton)page01_2part.findViewById(R.id.page01_button_teach);
        mListview = (ListView)page01.findViewById(R.id.page01_ListView);
    }


    /*
    * 初始化listView
    * */
    private void initListView(){
        this.setListViewData();
        SimpleAdapter mSimpleAdapter = new SimpleAdapter(
                context,
                mapList,
                R.layout.share_list_item_layout,
                new String[]{"ItemPicture", "ItemTitle", "ItemType", "ItemReadNum"},
                new int[]{R.id.ShareListItem_ImageView, R.id.ShareListItem_text_Title,
                R.id.ShareListItem_text_type, R.id.ShareListItem_text_ReadNum}
        );
        mListview.addHeaderView(page01_2part);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int afterPosition = position - 2;
                //添加具体查看的代码，跳转Activity
            }
        });
        mListview.setAdapter(mSimpleAdapter);
    }

    /*
    * 设置按钮监听
    * */
    public void initButtonEvent(){
        btnTeach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(page01.this.context, "hah", Toast.LENGTH_SHORT).show();
            }
        });

        btnNear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(page01.this.context, "hahaha", Toast.LENGTH_SHORT).show();
            }
        });

        btnHot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(page01.this.context, "hani", Toast.LENGTH_SHORT).show();
            }
        });

        btnGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(page01.this.context, "hamolk", Toast.LENGTH_SHORT).show();
            }
        });

        btnFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(page01.this.context, "lagh", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
    * 初始化循环图片列表
    * */
    private void initImageCycle(){

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        for(int i = 0; i < imageIds.length; i++){
            View view = layoutInflater.inflate(R.layout.image_cycle_layout, null);

            //设置图片资源
            ImageView image = (ImageView)view.findViewById(R.id.imageCycleImage);
            image.setImageResource(imageIds[i]);
            //对每个view单独设置监听事件
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(page01.this.context, "yeah!", Toast.LENGTH_SHORT).show();
                }
            });

            mViews.add(view);//添加到资源列表
        }
        mPagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return mViews.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = mViews.get(position);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mViews.get(position));
            }
        };
        mImageCycleViewPager.setAdapter(mPagerAdapter);

        currentCycleImage = mImageCycleViewPager.getCurrentItem();


        //设置线程
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);//等待
                    Message message = new Message();
                    message.what = 1;
                    mHandler.sendMessage(message);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();//开始线程
    }

    /*
    * 初始化小点
    * */
    private void initPoint(){
        points = new ImageView[imageIds.length];
        for(int j = 0; j < imageIds.length; j++){
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(10,10));
            points[j] = imageView;
            if(j == 0){
                points[j].setBackgroundResource(R.mipmap.icon_point_pre);
            }
            else{
                points[j].setBackgroundResource(R.mipmap.icon_point);
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;
            group.addView(imageView, layoutParams);
        }
    }


    private void setListViewData(){
        this.mapList = new ArrayList<HashMap<String, Object>>();
        for(int i = 0; i < this.item_Pictures.length; i++){
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemPicture", this.item_Pictures[i]);
            map.put("ItemTitle", this.item_Title[i]);
            map.put("ItemType", this.item_Type[i]);
            map.put("ItemReadNum", this.item_ReadNum[i]);
            mapList.add(map);
        }
    }

}
