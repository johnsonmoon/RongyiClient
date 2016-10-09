package xuyihao.rongyiclient.activities.pages;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
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
import xuyihao.rongyiclient.activities.MainActivity;
import xuyihao.rongyiclient.R;
import xuyihao.rongyiclient.activities.accounts.AccountInfoActivity;
import xuyihao.rongyiclient.activities.accounts.LoginActivity;
import xuyihao.rongyiclient.entity.Accounts;
import xuyihao.rongyiclient.tools.utils.FileUtils;

/**
 * Created by xuyihao on 16-4-4.
 */
public class PageMine {

    /**
     * 控件
     */
    private View viewPageMine;
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
     * 网络工具
     */
    private RequestSender sender = MainActivity.sender;
    private Downloader downloader = new Downloader(sender.getCookie());
    /**
     * 账户文件路径
     */
    private String accountPhotoPath = MainActivity.BASE_FILE_PATH + File.separator + "accounts";
    private String accountHeadPhotoPathName = accountPhotoPath + File.separator +"accountHeadPhoto.jpeg";
    private String accountHeadPhotoThumbnailPathName = accountPhotoPath + File.separator + "accountHeadPhotoThumbnail.jpeg";
    /**
     * 账户图片的bitmap(缩略图)
     */
    private Bitmap bitMapHeadPhotoThumbnail = null;

    String photoId = "";

    public PageMine(Activity ac, View view){
        context = ac;
        viewPageMine = view;
        init();
        initEvent();
    }

    /**
     * 初始化用户头像(登陆成功)
     *
     * 供activity调用
     */
    public void initAccountsPhoto(){
        FileUtils.checkAndCreateFilePath(accountPhotoPath);
        if(MainActivity.isLogin){
            this.textViewAccName.setText(MainActivity.accounts.getAcc_name());
            File photo = new File(accountHeadPhotoPathName);
            File thumbnail = new File(accountHeadPhotoThumbnailPathName);
            if(photo.exists() && thumbnail.exists()){
                bitMapHeadPhotoThumbnail = BitmapFactory.decodeFile(accountHeadPhotoThumbnailPathName);
                this.btnHeadPhoto.setImageBitmap(bitMapHeadPhotoThumbnail);
            }else{//图片不在本地，下载图片
                new AsyncTask(){
                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        if(!Boolean.parseBoolean(o.toString())){
                            Toast.makeText(context, "头像图片下载失败!", Toast.LENGTH_SHORT).show();
                        }else {
                            if(bitMapHeadPhotoThumbnail != null) {
                                btnHeadPhoto.setImageBitmap(bitMapHeadPhotoThumbnail);
                            }
                        }
                    }

                    @Override
                    protected Object doInBackground(Object[] params) {
                        String json = sender.executeGet(MainActivity.accountsActionURL + "?action=getHeadPhotoId&Acc_ID=" + MainActivity.accounts.getAcc_ID());
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            photoId = jsonObject.getString("headPhotoId");
                        } catch (JSONException e) {
                            photoId = "";
                            return false;
                        }
                        boolean flag = downloader.downloadByGet(accountHeadPhotoPathName, MainActivity.accountsActionURL + "?action=getPhotoById&Photo_ID=" + photoId);
                        flag = flag && downloader.downloadByGet(accountHeadPhotoThumbnailPathName, MainActivity.accountsActionURL + "?action=getThumbnailPhotoById&Photo_ID=" + photoId);
                        bitMapHeadPhotoThumbnail = BitmapFactory.decodeFile(accountHeadPhotoThumbnailPathName);
                        return flag;
                    }
                }.execute();
            }
        }

    }

    /**
     * 修改用户头像图片等(注销成功)
     *
     * 供activity调用
     */
    public void invalidateAccountsPhoto(){
        File photo = new File(accountHeadPhotoPathName);
        File thumbnail = new File(accountHeadPhotoThumbnailPathName);
        if (photo.exists() || thumbnail.exists()) {
            photo.delete();
            thumbnail.delete();
        }
        this.btnHeadPhoto.setImageResource(R.mipmap.page04_default_head_photo);
    }

    /**
    *  初始化控件
    * */
    private void init(){
        btnEnvelope = (Button) viewPageMine.findViewById(R.id.page04_button_envelope);
        btnHeadPhoto = (ImageView) viewPageMine.findViewById(R.id.page04_imageButton_headPhoto);
        btnSetting = (Button) viewPageMine.findViewById(R.id.page04_button_setting);
        btnMyOrder = (ImageView) viewPageMine.findViewById(R.id.page04_imageView_myOrder);
        btnWaitPay = (ImageView) viewPageMine.findViewById(R.id.page04_imageView_waitPay);
        btnWaitSend = (ImageView) viewPageMine.findViewById(R.id.page04_imageView_waitSend);
        btnWaitReceive = (ImageView) viewPageMine.findViewById(R.id.page04_imageView_waitReceive);
        btnWaitComment = (ImageView) viewPageMine.findViewById(R.id.page04_imageView_waitComment);
        btnAddressManage = (ImageView) viewPageMine.findViewById(R.id.page04_imageView_AddressManage);
        btnMyYouHui = (ImageView) viewPageMine.findViewById(R.id.page04_imageView_myYouHui);
        btnMyRongYiBi = (ImageView) viewPageMine.findViewById(R.id.page04_imageView_myRongYiBi);
        btnMyCart = (ImageView) viewPageMine.findViewById(R.id.page04_imageView_myCart);
        btnOpenShop = (ImageView) viewPageMine.findViewById(R.id.page04_imageView_openShop);
        btnMyPublished = (ImageView) viewPageMine.findViewById(R.id.page04_imageView_myPublished);
        btnMyFootPrint = (ImageView) viewPageMine.findViewById(R.id.page04_imageView_myFootPrint);
        btnFavourite = (ImageView) viewPageMine.findViewById(R.id.page04_imageView_myFavourite);
        textViewAccName = (TextView) viewPageMine.findViewById(R.id.page04_textView_Acc_name);
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
                    Intent intent = new Intent(context, AccountInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isMyself", true);
                    bundle.putString("Acc_ID", MainActivity.accounts.getAcc_ID());
                    bundle.putString("accountHeadPhotoPathName", accountHeadPhotoPathName);
                    bundle.putString("accountHeadPhotoThumbnailPathName", accountHeadPhotoThumbnailPathName);
                    intent.putExtras(bundle);
                    context.startActivityForResult(intent, 0x02);
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
