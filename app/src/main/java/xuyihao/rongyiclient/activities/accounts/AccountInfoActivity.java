package xuyihao.rongyiclient.activities.accounts;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import xuyihao.JohnsonHttpConnector.connectors.http.RequestSender;
import xuyihao.rongyiclient.activities.MainActivity;
import xuyihao.rongyiclient.R;
import xuyihao.rongyiclient.entity.Accounts;

/**
 * 账户详细信息界面
 * <pre>
 *     包括用户自己以及其他用户的相信信息显示界面
 *     两者的显示逻辑会有所不同(由开启的传值来区分)
 *
 *     activity传值：
 *     (1)isMyself boolean值
 *     (2)Acc_ID String值
 *     (3)accountHeadPhotoPathName 头像缩略图文件路径
 *     (4)accountHeadPhotoThumbnailPathName 头像文件路径
 * </pre>
 *
 * Created by Xuyh at 2016-10-09 10:10.
 */
public class AccountInfoActivity extends AppCompatActivity {
    /**
     * 控件
     */
    private ImageView btnLike;
    private ImageView btnHeadPhoto;
    private ImageView btnPhotoCombine;
    private Button btnSetting;
    private Button btnLogout;
    private TextView txtAcc_name;
    private TextView txtAcc_sex;
    private TextView txtAcc_loc;
    private TextView txtAcc_lvl;
    private TextView txtAcc_ryb;
    private TextView txtAcc_shop;
    private TextView txtAcc_atn;
    private TextView txtAcc_atnd;
    private TextView txtAcc_pub;
    private TextView txtAcc_no;
    private TextView txtAcc_name2;
    private TextView txtAcc_tel;
    private TextView txtAcc_addTime;

    /**
     * 显示账户信息
     */
    private boolean isMyself;
    private String Acc_ID;
    private Accounts accounts;
    private String accountHeadPhotoPathName;
    private String accountHeadPhotoThumbnailPathName;


    private SQLiteDatabase database;
    private RequestSender sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        initView();
        initAccuntInfo();
        initEvent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 初始化显示账户信息
     */
    private void initAccuntInfo(){
        this.database = MainActivity.database;
        this.sender = MainActivity.sender;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        this.isMyself = bundle.getBoolean("isMyself");
        this.Acc_ID = bundle.getString("Acc_ID");
        if(isMyself){//如果是本用户
            if(MainActivity.isLogin) {
                this.accounts = MainActivity.accounts;
                this.Acc_ID = MainActivity.accounts.getAcc_ID();
                this.txtAcc_name.setText(this.accounts.getAcc_name());
                this.txtAcc_sex.setText(this.accounts.getAcc_sex());
                this.txtAcc_loc.setText(this.accounts.getAcc_loc());
                this.txtAcc_lvl.setText(String.valueOf(this.accounts.getAcc_lvl()));
                this.txtAcc_ryb.setText(String.valueOf(this.accounts.getAcc_ryb()));
                this.txtAcc_shop.setText(String.valueOf(this.accounts.isAcc_shop()));
                this.txtAcc_atn.setText(String.valueOf(this.accounts.getAcc_atn()));
                this.txtAcc_atnd.setText(String.valueOf(this.accounts.getAcc_atnd()));
                this.txtAcc_pub.setText(String.valueOf(this.accounts.getAcc_pub()));
                this.txtAcc_no.setText(this.accounts.getAcc_no());
                this.txtAcc_name2.setText(this.accounts.getAcc_name2());
                this.txtAcc_tel.setText(this.accounts.getAcc_tel());
                this.txtAcc_addTime.setText(this.accounts.getAcc_addTime());
                this.accountHeadPhotoPathName = bundle.getString("accountHeadPhotoPathName");
                this.accountHeadPhotoThumbnailPathName = bundle.getString("accountHeadPhotoThumbnailPathName");
                if(this.accountHeadPhotoThumbnailPathName != null && !this.accountHeadPhotoThumbnailPathName.equals("")){
                    Bitmap headPhotoThumbnail = BitmapFactory.decodeFile(this.accountHeadPhotoThumbnailPathName);
                    this.btnHeadPhoto.setImageBitmap(headPhotoThumbnail);
                }
            }else {
                Toast.makeText(AccountInfoActivity.this, "错误!", Toast.LENGTH_SHORT).show();
            }
        }else{//如果是其他用户

        }
    }

    /**
     * 初始化控件
     */
    private void initView(){
        btnLike = (ImageView)findViewById(R.id.accountInfoActivity_imageView_like);
        btnHeadPhoto = (ImageView)findViewById(R.id.accountInfoActivity_imageView_headPhoto);
        btnPhotoCombine = (ImageView)findViewById(R.id.accountInfo_imageView_photoCombine);
        btnSetting = (Button) findViewById(R.id.accountInfoActivity_button_setting);
        btnLogout = (Button)findViewById(R.id.accountInfoActivity_button_logout);
        txtAcc_name = (TextView)findViewById(R.id.accountInfoActivity_textView_Acc_name);
        txtAcc_sex = (TextView)findViewById(R.id.accountInfoActivity_textView_Acc_sex);
        txtAcc_loc = (TextView)findViewById(R.id.accountInfoActivity_textView_Acc_loc);
        txtAcc_lvl = (TextView)findViewById(R.id.accountInfoActivity_textView_Acc_lvl);
        txtAcc_ryb = (TextView)findViewById(R.id.accountInfoActivity_textView_Acc_ryb);
        txtAcc_shop = (TextView)findViewById(R.id.accountInfoActivity_textView_Acc_shop);
        txtAcc_atn = (TextView)findViewById(R.id.accountInfoActivity_textView_Acc_atn);
        txtAcc_atnd = (TextView)findViewById(R.id.accountInfoActivity_textView_Acc_atnd);
        txtAcc_pub = (TextView)findViewById(R.id.accountInfoActivity_textView_Acc_pub);
        txtAcc_no = (TextView)findViewById(R.id.accountInfoActivity_textView_Acc_no);
        txtAcc_name2 = (TextView)findViewById(R.id.accountInfoActivity_textView_Acc_name2);
        txtAcc_tel = (TextView)findViewById(R.id.accountInfoActivity_textView_Acc_tel);
        txtAcc_addTime = (TextView)findViewById(R.id.accountInfoActivity_textView_Acc_addTime);
    }

    /**
     * 初始化事件监听
     */
    private void initEvent(){
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnHeadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnPhotoCombine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMyself){//非本用户按钮无效
                    new AsyncTask(){
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            Toast.makeText(AccountInfoActivity.this, "正在注销...", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            if(Boolean.parseBoolean(o.toString())){//注销成功
                                MainActivity.accounts = new Accounts();
                                MainActivity.isLogin = false;
                                MainActivity.database.execSQL("delete from " + Accounts.BASE_TABLE_NAME + "");
                                Toast.makeText(AccountInfoActivity.this, "注销成功!", Toast.LENGTH_SHORT).show();
                                Intent intent = getIntent();
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("result", true);
                                intent.putExtras(bundle);
                                setResult(0x02, intent);
                                AccountInfoActivity.this.finish();
                            }else{
                                Toast.makeText(AccountInfoActivity.this, "注销失败!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        protected Object doInBackground(Object[] params) {
                            String json = sender.executeGet(MainActivity.accountsActionURL + "?action=logout");
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                if(jsonObject.get("result").toString().equals("true")){
                                    return true;
                                }else {
                                    return false;
                                }
                            }catch (JSONException e){
                                return false;
                            }
                        }
                    }.execute();
                }
            }
        });
    }
}
