package xuyihao.rongyiclient.pages;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import xuyihao.JohnsonHttpConnector.connectors.http.Downloader;
import xuyihao.JohnsonHttpConnector.connectors.http.RequestSender;
import xuyihao.rongyiclient.MainActivity;
import xuyihao.rongyiclient.R;
import xuyihao.rongyiclient.activities.LoginActivity;
import xuyihao.rongyiclient.entity.Accounts;
import xuyihao.rongyiclient.tool.utils.FileUtils;

/**
 * Created by xuyihao on 16-4-4.
 */
public class page04 {

    /**
     * 控件
     */
    private View page04;
    private Activity context;
    private Button btnEnvelope;
    private ImageView btnHeadPhoto;
    private Button btnSetting;
    private ImageView btnMyOrder;
    private ImageView btnWaitPay;
    private ImageView btnWaitSend;
    private ImageView btnWaitReceive;
    private ImageView btnWaitComment;
    private ImageView btnAddressManage;
    private ImageView btnMyYouHui;
    private ImageView btnMyRongYiBi;
    private ImageView btnMyCart;
    private ImageView btnOpenShop;
    private ImageView btnMyPublished;
    private ImageView btnMyFootPrint;
    private ImageView btnFavourite;
    private TextView textViewAccName;

    /**
     * 本地数据库
     */
    private SQLiteDatabase database = MainActivity.database;
    /**
     * 当前用户
     */
    private Accounts accounts = MainActivity.accounts;
    /**
     * 网络工具
     */
    private RequestSender sender = MainActivity.sender;
    private Downloader downloader = new Downloader(sender.getCookie());
    /**
     * 账户文件路径
     */
    private String accountPhotoPath = MainActivity.BASE_FILE_PATH + File.separator + "accounts";
    private String accountHeadPhotoName;
    private String accountHeadPhotoThumbnailName;
    /**
     * 账户图片的bitmap
     */
    private Bitmap headPhoto = null;
    private Bitmap headPhotoThumbnail = null;

    String photoId = "";

    public page04(Activity ac, View view){
        context = ac;
        page04 = view;
        if(!MainActivity.isLogin){
            Toast.makeText(ac, "未登录,请点击头像登录", Toast.LENGTH_LONG);
        }
        init();
        initEvent();
    }

    /**
     * 初始化用户图片
     *
     * 供activity调用
     */
    public void initAccountsPhoto(){
        FileUtils.checkAndCreateFilePath(accountPhotoPath);
        if(MainActivity.isLogin){
            this.textViewAccName.setText(accounts.getAcc_name());
            accountHeadPhotoName = accounts.getAcc_ID() + "headPhoto.jpeg";
            accountHeadPhotoThumbnailName = accounts.getAcc_ID() + "headPhotoThumbnail.jpeg";
            File photo = new File(accountPhotoPath + File.separator + accountHeadPhotoName);
            File thumbnail = new File(accountPhotoPath + File.separator + accountHeadPhotoThumbnailName);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String json = sender.executeGet(MainActivity.accountsActionURL + "?action=getHeadPhotoId&Acc_ID=" + accounts.getAcc_ID());
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        photoId = jsonObject.getString("headPhotoId");
                    }catch (JSONException e){
                        photoId = "";
                    }
                }
            }).start();
            if(photo.exists()){
                headPhoto = BitmapFactory.decodeFile(accountPhotoPath + File.separator + accountHeadPhotoName);
            }else{//下载图片
                new AsyncTask(){
                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                    }

                    @Override
                    protected Object doInBackground(Object[] params) {
                        boolean flag = downloader.downloadByGetSaveToPath(accountPhotoPath + File.separator + accountHeadPhotoName, MainActivity.accountsActionURL + "?action=getPhotoById&Photo_ID=" + photoId);
                        headPhoto = BitmapFactory.decodeFile(accountPhotoPath + File.separator + accountHeadPhotoName);
                        return flag;
                    }
                }.execute();
            }
            if(thumbnail.exists()){
                headPhotoThumbnail = BitmapFactory.decodeFile(accountPhotoPath + File.separator + accountHeadPhotoThumbnailName);
                this.btnHeadPhoto.setImageBitmap(headPhotoThumbnail);
            }else{
                new AsyncTask(){
                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        if(Boolean.parseBoolean(o.toString())){
                            btnHeadPhoto.setImageBitmap(headPhotoThumbnail);
                        }
                    }

                    @Override
                    protected Object doInBackground(Object[] params) {
                        boolean flag = downloader.downloadByGetSaveToPath(accountPhotoPath + File.separator + accountHeadPhotoThumbnailName, MainActivity.accountsActionURL + "?action=getThumbnailPhotoById&Photo_ID=" + photoId);
                        headPhotoThumbnail = BitmapFactory.decodeFile(accountPhotoPath + File.separator + accountHeadPhotoThumbnailName);
                        return flag;
                    }
                }.execute();
            }
        }
    }

    /**
    *  初始化控件
    * */
    private void init(){
        btnEnvelope = (Button)page04.findViewById(R.id.page04_button_envelope);
        btnHeadPhoto = (ImageView)page04.findViewById(R.id.page04_imageButton_headPhoto);
        btnSetting = (Button)page04.findViewById(R.id.page04_button_setting);
        btnMyOrder = (ImageView)page04.findViewById(R.id.page04_imageView_myOrder);
        btnWaitPay = (ImageView)page04.findViewById(R.id.page04_imageView_waitPay);
        btnWaitSend = (ImageView)page04.findViewById(R.id.page04_imageView_waitSend);
        btnWaitReceive = (ImageView)page04.findViewById(R.id.page04_imageView_waitReceive);
        btnWaitComment = (ImageView)page04.findViewById(R.id.page04_imageView_waitComment);
        btnAddressManage = (ImageView)page04.findViewById(R.id.page04_imageView_AddressManage);
        btnMyYouHui = (ImageView)page04.findViewById(R.id.page04_imageView_myYouHui);
        btnMyRongYiBi = (ImageView)page04.findViewById(R.id.page04_imageView_myRongYiBi);
        btnMyCart = (ImageView)page04.findViewById(R.id.page04_imageView_myCart);
        btnOpenShop = (ImageView)page04.findViewById(R.id.page04_imageView_openShop);
        btnMyPublished = (ImageView)page04.findViewById(R.id.page04_imageView_myPublished);
        btnMyFootPrint = (ImageView)page04.findViewById(R.id.page04_imageView_myFootPrint);
        btnFavourite = (ImageView)page04.findViewById(R.id.page04_imageView_myFavourite);
        textViewAccName = (TextView)page04.findViewById(R.id.page04_textView_Acc_name);
    }

    /**
    * 初始化控件触摸事件
    * */
    private void initEvent(){
        btnEnvelope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnHeadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.isLogin){//已经登录

                }else{
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivityForResult(intent, 0x01);
                }
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnMyOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnWaitPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnWaitSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnWaitReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnWaitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnAddressManage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

        btnMyYouHui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnMyRongYiBi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnMyCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnOpenShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnMyPublished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnMyFootPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
