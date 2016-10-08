package xuyihao.rongyiclient;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import xuyihao.JohnsonHttpConnector.connectors.http.RequestSender;
import xuyihao.rongyiclient.entity.Accounts;
import xuyihao.rongyiclient.tool.DatabaseOperator;
import xuyihao.rongyiclient.widget.*;
import xuyihao.rongyiclient.pages.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity{
    /**
     * 当前用户
     */
    public static Accounts accounts;
    /**
     * 本地缓存数据库及操作
     */
    private DatabaseOperator operator;
    public static SQLiteDatabase database;

    /**
     * 本地文件存放路径
     */
    public static String BASE_FILE_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + "rogyi";

    /**
     * 网络请求相关
     */
    public static RequestSender sender = new RequestSender();
    /**
     * 是否成功登录
     */
    public static boolean isLogin = false;

    /**
     * 账户相关URL
     */
    public static final String accountsActionURL = "http://115.28.192.61:8088/rongyi/accounts";
    /**
     * 视频相关URL
     */
    public static final String coursesActionURL = "http://115.28.192.61:8088/rongyi/courses";
    /**
     * 帖子相关URL
     */
    public static final String postsActionURL = "http://115.28.192.61:8088/rongyi/posts";
    /**
     * 店铺相关URL
     */
    public static final String shopsActionURL = "http://115.28.192.61:8088/rongyi/shops";

    private MyNoScrollViewPager mViewPager;//viewPager用来切换界面
    private PagerAdapter mPagerAdapter;//view适配器
    private List<View> mViews = new ArrayList<View>();//存放用来切换界面的View

    /**
     * 页面类
     */
    private page01 page1;
    private page04 page4;

    /**
     * 主页底部按钮
     */
    private ImageButton btnHomePage;
    private ImageButton btnProduct;
    private ImageButton btnCommunity;
    private ImageButton btnMine;

    /**
     * 主页顶部控件
     * */
    private EditText searchText;
    private Button btnSearch;
    private Spinner mSpinner;
    private ArrayAdapter<String> adapter;
    private static final String[] kind = {"找人", "找产品", "找视频", "找分享"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
        initSpinner();
        InitViewPage();
        InitEvent();
        initAccounts();
    }

    /**
     * 初始化用户
     */
    private void initAccounts(){
        this.operator = new DatabaseOperator(this);
        this.database = operator.getReadableDatabase();
        this.accounts = new Accounts();
        Cursor cursor = database.rawQuery("select * from "+Accounts.BASE_TABLE_NAME+"", null);
        if(cursor.moveToNext()){
            accounts.set_id(cursor.getLong(cursor.getColumnIndex(Accounts.BASE_ACCOUNT_PHYSICAL_ID)));
            accounts.setAcc_addTime(cursor.getString(cursor.getColumnIndex(Accounts.BASE_ACCOUNT_ADD_TIME)));
            accounts.setAcc_atn(cursor.getInt(cursor.getColumnIndex(Accounts.BASE_ACCOUNT_ATTENTION_COUNT)));
            accounts.setAcc_atnd(cursor.getInt(cursor.getColumnIndex(Accounts.BASE_ACCOUNT_ATTENED_COUNT)));
            accounts.setAcc_ID(cursor.getString(cursor.getColumnIndex(Accounts.BASE_ACCOUNT_ID)));
            accounts.setAcc_loc(cursor.getString(cursor.getColumnIndex(Accounts.BASE_ACCOUNT_LOCATION)));
            accounts.setAcc_lvl(cursor.getInt(cursor.getColumnIndex(Accounts.BASE_ACCOUNT_LEVEL)));
            accounts.setAcc_name(cursor.getString(cursor.getColumnIndex(Accounts.BASE_ACCOUNT_NAME)));
            accounts.setAcc_name2(cursor.getString(cursor.getColumnIndex(Accounts.BASE_ACCOUNT_NAME2)));
            accounts.setAcc_no(cursor.getString(cursor.getColumnIndex(Accounts.BASE_ACCOUNT_NO)));
            accounts.setAcc_pub(cursor.getInt(cursor.getColumnIndex(Accounts.BASE_ACCOUNT_PUBLISH)));
            accounts.setAcc_pwd(cursor.getString(cursor.getColumnIndex(Accounts.BASE_ACCOUNT_PASSWORD)));
            accounts.setAcc_ryb(cursor.getInt(cursor.getColumnIndex(Accounts.BASE_ACCOUNT_RYB)));
            accounts.setAcc_sex(cursor.getString(cursor.getColumnIndex(Accounts.BASE_ACCOUNT_SEX)));
            accounts.setAcc_shop(cursor.getLong(cursor.getColumnIndex(Accounts.BASE_ACCOUNT_IS_SHOP_OWNER)) == 0L);
            accounts.setAcc_tel(cursor.getString(cursor.getColumnIndex(Accounts.BASE_ACCOUNT_TELEPHONE)));
        }
        //如果存在用户，执行登录操作
        if(accounts.get_id() != 0L) {
            new AsyncTask() {
                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    try {
                        JSONObject json = new JSONObject(o.toString());
                        String result = json.getString("result");
                        if (result.equals("true")) {
                            Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            MainActivity.isLogin = true;
                            page4.initAccountsPhoto();
                        } else {
                            Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                            MainActivity.isLogin = false;
                        }
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                protected Object doInBackground(Object[] params) {
                    HashMap<String, String> parameters = new HashMap<String, String>();
                    parameters.put("action", "login");
                    parameters.put("Acc_name", accounts.getAcc_name());
                    parameters.put("Acc_pwd", accounts.getAcc_pwd());
                    return MainActivity.this.sender.executePostByUsual(MainActivity.accountsActionURL, parameters);
                }
            }.execute();
        }
    }

    /**
    * 初始化View及控件
    * */
    private void Init(){
        mViewPager = (MyNoScrollViewPager)findViewById(R.id.mainViewPager);
        btnHomePage = (ImageButton)findViewById(R.id.imageButton_Bottom_Home);
        btnProduct = (ImageButton)findViewById(R.id.imageButton_Bottom_Product);
        btnCommunity = (ImageButton)findViewById(R.id.imageButton_Bottom_Community);
        btnMine = (ImageButton)findViewById(R.id.imageButton_Bottom_Mine);
        searchText = (EditText)findViewById(R.id.mainTopText_search);
        btnSearch = (Button)findViewById(R.id.mainTopButton_search);
        mSpinner = (Spinner)findViewById(R.id.mainTopSpinner);
    }

    /**
    * 初始化Spinner列表
    * */
    private void initSpinner(){
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, kind);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setVisibility(View.VISIBLE);
    }

    /**
    * 初始化ViewPage
    * */
    private void InitViewPage(){
        LayoutInflater mLayoutInflater = LayoutInflater.from(this);
        View tab1 = mLayoutInflater.inflate(R.layout.page01_layout, null);
        View tab2 = mLayoutInflater.inflate(R.layout.page02_layout, null);
        View tab3 = mLayoutInflater.inflate(R.layout.page03_layout, null);
        View tab4 = mLayoutInflater.inflate(R.layout.page04_layout, null);
        mViews.add(tab1);
        mViews.add(tab2);
        mViews.add(tab3);
        mViews.add(tab4);
        mPagerAdapter = new PagerAdapter() {
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

            @Override
            public int getCount() {
                return mViews.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        };
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setNoScroll(true);//设置viewPage不能左右滑动
        //初始化第一个页面类
        this.page1 = new page01(this, tab1);//注意传参
        //初始化第4个页面类
        this.page4 = new page04(this, tab4);
    }

    /**
     * 底部按钮变换颜色
     *
     * @param index
     */
    private void bottomButtonChangeBackground(int index){
        switch (index){
            case 0:
                btnHomePage.setBackground(getDrawable(R.mipmap.bottom_button_image_1_light));
                btnProduct.setBackground(getDrawable(R.mipmap.bottom_button_image_2));
                btnCommunity.setBackground(getDrawable(R.mipmap.bottom_button_image_3));
                btnMine.setBackground(getDrawable(R.mipmap.bottom_button_image_4));
                break;
            case 1:
                btnHomePage.setBackground(getDrawable(R.mipmap.bottom_button_image_1));
                btnProduct.setBackground(getDrawable(R.mipmap.bottom_button_image_2_light));
                btnCommunity.setBackground(getDrawable(R.mipmap.bottom_button_image_3));
                btnMine.setBackground(getDrawable(R.mipmap.bottom_button_image_4));
                break;
            case 2:
                btnHomePage.setBackground(getDrawable(R.mipmap.bottom_button_image_1));
                btnProduct.setBackground(getDrawable(R.mipmap.bottom_button_image_2));
                btnCommunity.setBackground(getDrawable(R.mipmap.bottom_button_image_3_light));
                btnMine.setBackground(getDrawable(R.mipmap.bottom_button_image_4));
                break;
            case 3:
                btnHomePage.setBackground(getDrawable(R.mipmap.bottom_button_image_1));
                btnProduct.setBackground(getDrawable(R.mipmap.bottom_button_image_2));
                btnCommunity.setBackground(getDrawable(R.mipmap.bottom_button_image_3));
                btnMine.setBackground(getDrawable(R.mipmap.bottom_button_image_4_light));
                break;
        }
    }

    /**
    * 初始化触摸事件
    * */
    private void InitEvent(){
        btnHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
                bottomButtonChangeBackground(0);
            }
        });
        btnProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1);
                bottomButtonChangeBackground(1);
            }
        });
        btnCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(2);
                bottomButtonChangeBackground(2);
            }
        });
        btnMine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(3);
                bottomButtonChangeBackground(3);
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if((requestCode == 0x01) && (resultCode == 0x01)){//登录界面返回
            Bundle bundle = data.getExtras();
            if(bundle.getBoolean("result")){//如果登录成功
                page4.initAccountsPhoto();
            }
        }
    }
}
