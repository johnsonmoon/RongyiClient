package xuyihao.rongyiclient.activities.common;

import android.content.Intent;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import xuyihao.JohnsonHttpConnector.connectors.http.Downloader;
import xuyihao.rongyiclient.R;
import xuyihao.rongyiclient.activities.MainActivity;
import xuyihao.rongyiclient.tools.utils.BitmapUtils;

/**
 * 显示图片的activity
 * <pre>
 *    设计思路：
 *    (1)通过传值获取图片的路径名(列表)，或者给出图片下载的URL并下载图片以及缩略图
 *    (2)网络下载图片耗时，边加载边显示
 *    (3)逐张显示图片
 *    模式：
 *    (1)IMAGE_DISPLAY_MODE_DOWNLOAD 下载模式，需要给出图片(以及缩略图)下载地址列表[MODE(int), thumbnailURLList(ArrayList<String>), imageURLList(ArrayList<String>)]
 *    (2)IMAGE_DISPLAY_MODE_LOCALFILE 本地模式，需要给出图片(以及缩略图)路径名列表[MODE(int), thumbnailFilePathNameList(ArrayList<String>), imageFilePathNameList(ArrayList<String>)]
 *    (3)IMAGE_DISPLAY_MODE_DOWNLOAD_TO_LOCAL 下载至本地存储，再显示模式[MODE(int), thumbnailURLList(ArrayList<String>), imageURLList(ArrayList<String>)]
 * </pre>
 *
 * Created by Xuyh at 2016-10-09 12:45.
 */
public class ImageDisplayActivity extends AppCompatActivity {
    /**
     * activity开启模式
     */
    public static int IMAGE_DISPLAY_MODE_DOWNLOAD = 0;
    public static int IMAGE_DISPLAY_MODE_LOCALFILE = 1;
    public static int IMAGE_DISPLAY_MODE_DOWNLOAD_TO_LOCAL = 2;
    /**
     * 图片URL列表
     */
    private List<String> thumbnailURLList = new ArrayList<>();
    private List<String> imageURLList = new ArrayList<>();
    /**
     * 图片文件路径名列表
     */
    private List<String> thumbnailFilePathNameList = new ArrayList<>();
    private List<String> imageFilePathNameList = new ArrayList<>();
    /**
     * 缩略图BitMap列表
     */
    private List<Bitmap> thumbnailBitMapList = new ArrayList<>();
    private List<Bitmap> imageBitMapList = new ArrayList<>();
    /**
     * 所有图片总数
     */
    private int imageTotalCount = 0;
    /**
     * 已经加载好的缩略图个数
     */
    private int thumbnailDownloadedCount = 0;
    /**
     * 已经加载好的原图图个数
     */
    private int imageDownloadedCount = 0;
    /**
     * 当前显示图片位置
     */
    private int imageIndex = 0;
    /**
     * 控件
     */
    private TextView textPercent;
    private ImageView imageViewDisplay;
    private Button btnPreviousImage;
    private Button btnNextImage;
    /**
     * 下载工具
     */
    private Downloader downloader;
    /**
     * 上文传递数据
     */
    private Intent intent;
    private Bundle bundle;
    private int MODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        initView();
        initImages();
        initEvent();
    }

    /**
     * 初始化图片列表
     */
    private void initImages(){
        downloader = new Downloader(MainActivity.sender.getCookie());
        intent = getIntent();
        bundle = intent.getExtras();
        MODE = bundle.getInt("MODE");
        if(MODE == IMAGE_DISPLAY_MODE_DOWNLOAD){//下载模式
            imageURLList = bundle.getStringArrayList("imageURLList");
            thumbnailURLList = bundle.getStringArrayList("thumbnailURLList");
            imageTotalCount = thumbnailURLList.size();
            textPercent.setText(1 + " / " + thumbnailURLList.size());
        }else if (MODE == IMAGE_DISPLAY_MODE_LOCALFILE){//本地模式
            imageFilePathNameList = bundle.getStringArrayList("imageFilePathNameList");
            thumbnailFilePathNameList = bundle.getStringArrayList("thumbnailFilePathNameList");
            imageTotalCount = thumbnailFilePathNameList.size();
            textPercent.setText(1 + " / " + thumbnailFilePathNameList.size());
        }else if (MODE == IMAGE_DISPLAY_MODE_DOWNLOAD_TO_LOCAL){//下载至本地模式
            imageURLList = bundle.getStringArrayList("imageURLList");
            thumbnailURLList = bundle.getStringArrayList("thumbnailURLList");
            imageTotalCount = thumbnailURLList.size();
            textPercent.setText(1 + " / " + thumbnailURLList.size());
        }
        imageViewDisplay.setImageResource(R.mipmap.image_loading_file);
        new downloadImageAsyncTask().execute();
    }

    /**
     * 初始化控件
     */
    private void initView(){
        this.textPercent = (TextView)findViewById(R.id.imageDispalyActivity_textView_percentOfImages);
        this.imageViewDisplay = (ImageView) findViewById(R.id.imageDisplayActivity_imageView_centerView);
        this.btnPreviousImage = (Button)findViewById(R.id.imageDisplayActivity_button_formerImage);
        this.btnNextImage = (Button)findViewById(R.id.imageDisplayActivity_button_nextImage);
    }

    /**
     * 初始化触摸事件
     */
    private void initEvent(){
        this.btnPreviousImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageIndex > 0){
                    if(imageDownloadedCount >= imageIndex){//有原图
                        imageIndex--;
                        textPercent.setText((imageIndex+1) + " / " + thumbnailURLList.size());
                        imageViewDisplay.setImageBitmap(imageBitMapList.get(imageIndex));
                    }else{
                        imageIndex--;
                        textPercent.setText((imageIndex+1) + " / " + thumbnailURLList.size());
                        imageViewDisplay.setImageBitmap(thumbnailBitMapList.get(imageIndex));
                    }
                }
            }
        });
        /**
         * 下一张，如果原图还未加载好，则显示缩略图
         */
        this.btnNextImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(imageIndex < imageTotalCount-1){
                if(imageDownloadedCount-1 > imageIndex){//原图加载完成
                    imageIndex++;
                    textPercent.setText((imageIndex+1) + " / " + thumbnailURLList.size());
                    imageViewDisplay.setImageBitmap(imageBitMapList.get(imageIndex));
                }else{
                    if(thumbnailDownloadedCount-1 > imageIndex){//缩略图加载完成
                        imageIndex++;
                        textPercent.setText((imageIndex+1) + " / " + thumbnailURLList.size());
                        imageViewDisplay.setImageBitmap(thumbnailBitMapList.get(imageIndex));
                    }else{//都没加载完成
                        imageIndex++;
                        textPercent.setText((imageIndex+1) + " / " + thumbnailURLList.size());
                        imageViewDisplay.setImageResource(R.mipmap.image_loading_file);
                    }
                }
            }
            }
        });
    }

    /**
     * 重写下载、加载文件的异步任务类
     */
    private class downloadImageAsyncTask extends AsyncTask{
        private int totalCount;

        public downloadImageAsyncTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(MODE == IMAGE_DISPLAY_MODE_DOWNLOAD){//下载模式
                totalCount = thumbnailURLList.size();
                Toast.makeText(ImageDisplayActivity.this, "正在下载...", Toast.LENGTH_SHORT).show();
            }else if (MODE == IMAGE_DISPLAY_MODE_LOCALFILE){//本地模式
                totalCount = thumbnailFilePathNameList.size();
                Toast.makeText(ImageDisplayActivity.this, "正在解析...", Toast.LENGTH_SHORT).show();
            }else if (MODE == IMAGE_DISPLAY_MODE_DOWNLOAD_TO_LOCAL){//下载至本地模式
                totalCount = thumbnailURLList.size();
                Toast.makeText(ImageDisplayActivity.this, "正在下载...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Toast.makeText(ImageDisplayActivity.this, "图片全部加载成功!", Toast.LENGTH_SHORT).show();
        }

        /**
         * 下载完 n 张图片，更新一下状态
         *
         * <pre>
         *     先加载缩略图，边加载变更新状态
         *     后加载大图，边加载边更新状态
         * </pre>
         *
         * @param params
         * @return
         */
        @Override
        protected Object doInBackground(Object[] params) {
            if(MODE == IMAGE_DISPLAY_MODE_DOWNLOAD){//下载模式
                //加载缩略图
                for(int i = 0; i < totalCount; i++){
                    BitmapUtils.downloadFiles(downloader, thumbnailURLList, thumbnailBitMapList, i, 1);
                    publishProgress("thumbnail");
                }
                //加载原图
                for(int i = 0; i < totalCount; i++){
                    BitmapUtils.downloadFiles(downloader, imageURLList, imageBitMapList, i, 1);
                    publishProgress("image");
                }
            }else if (MODE == IMAGE_DISPLAY_MODE_LOCALFILE){//本地模式
                //加载缩略图
                for(int i = 0; i < totalCount; i++){
                    BitmapUtils.initializeFiles(thumbnailFilePathNameList, thumbnailBitMapList, i, 1);
                    publishProgress("thumbnail");
                }
                //加载原图
                for(int i = 0; i < totalCount; i++){
                    BitmapUtils.initializeFiles(imageFilePathNameList, imageBitMapList, i, 1);
                    publishProgress("image");
                }
            }else if (MODE == IMAGE_DISPLAY_MODE_DOWNLOAD_TO_LOCAL){//下载至本地模式
                //加载缩略图
                for(int i = 0; i < totalCount; i++){
                    BitmapUtils.downloadFilesToLocal(downloader, thumbnailURLList, thumbnailBitMapList, i, 1);
                    publishProgress("thumbnail");
                }
                //加载原图
                for(int i = 0; i < totalCount; i++){
                    BitmapUtils.downloadFilesToLocal(downloader, imageURLList, imageBitMapList, i, 1);
                    publishProgress("image");
                }
            }
            return null;
        }

        /**
         * 更新状态
         *
         * @param values
         */
        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            String value = values[0].toString();
            if(value.equals("thumbnail")){
                thumbnailDownloadedCount++;
            }else if(value.equals("image")){
                imageDownloadedCount++;
            }
            if(imageIndex == 0 && imageDownloadedCount > 0){
                imageViewDisplay.setImageBitmap(imageBitMapList.get(0));
            }else if(imageIndex == 0 && thumbnailDownloadedCount > 0){
                imageViewDisplay.setImageBitmap(thumbnailBitMapList.get(0));
            } else if(imageDownloadedCount-1 == imageIndex){
                imageViewDisplay.setImageBitmap(imageBitMapList.get(imageIndex));
            } else if(thumbnailDownloadedCount-1 == imageIndex){
                imageViewDisplay.setImageBitmap(thumbnailBitMapList.get(imageIndex));
            }
        }
    }
}
