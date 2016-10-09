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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import xuyihao.JohnsonHttpConnector.connectors.http.Downloader;
import xuyihao.rongyiclient.R;
import xuyihao.rongyiclient.activities.MainActivity;

/**
 * 显示图片的activity
 * <pre>
 *    设计思路：
 *    (1)通过传值获取图片的路径名(列表)，或者给出图片下载的URL并
 *    (2)获取图片(本地、下载)将图片文件转换成Bitmap列表
 *    (3)逐张显示图片
 *    模式：
 *    (1)IMAGE_DISPLAY_MODE_DOWNLOAD 下载模式，需要给出图片下载地址列表[MODE(int), imageURLList(ArrayList<String>)]
 *    (2)IMAGE_DISPLAY_MODE_LOCALFILE 本地模式，需要给出图片路径名列表[MODE(int), imageFilePathNameList(ArrayList<String>)]
 *    (3)IMAGE_DISPLAY_MODE_DOWNLOAD_TO_LOCAL 下载至本地存储，再显示模式[MODE(int), imageURLList(ArrayList<String>)]
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
    private List<String> imageURLList = new ArrayList<>();
    /**
     * 图片文件路径名列表
     */
    private List<String> imageFilePathNameList = new ArrayList<>();
    /**
     * 图片BitMap列表
     */
    private List<Bitmap> imageBitMapList = new ArrayList<>();
    /**
     * 当前显示图片位置
     */
    private int imageIndex = 0;
    /**
     * 控件
     */
    private TextView textDisplayName;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        this.intent = getIntent();
        this.bundle = intent.getExtras();
        initView();
        initImages();
        initEvent();
    }

    /**
     * 初始化图片列表
     */
    private void initImages(){
        this.downloader = new Downloader(MainActivity.sender.getCookie());
        new AsyncTask(){
            int mode;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mode = bundle.getInt("MODE");
                if(mode == IMAGE_DISPLAY_MODE_DOWNLOAD){//下载模式
                    textDisplayName.setText("正在下载图片...");
                }else if (mode == IMAGE_DISPLAY_MODE_LOCALFILE){//本地模式
                    textDisplayName.setText("正在解析本地图片...");
                }else if (mode == IMAGE_DISPLAY_MODE_DOWNLOAD_TO_LOCAL){//下载至本地模式
                    textDisplayName.setText("正在下载图片...");
                }
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if(imageBitMapList.size() > 0){
                    textDisplayName.setText("");
                    textPercent.setText(1 + " / " + imageBitMapList.size());
                    imageViewDisplay.setImageBitmap(imageBitMapList.get(0));
                }
            }

            @Override
            protected Object doInBackground(Object[] params) {
                if(mode == IMAGE_DISPLAY_MODE_DOWNLOAD){//下载模式
                    imageURLList = bundle.getStringArrayList("imageURLList");
                    downloadFiles(imageURLList);
                }else if (mode == IMAGE_DISPLAY_MODE_LOCALFILE){//本地模式
                    imageFilePathNameList = bundle.getStringArrayList("imageFilePathNameList");
                    initializeFiles(imageFilePathNameList);
                }else if (mode == IMAGE_DISPLAY_MODE_DOWNLOAD_TO_LOCAL){//下载至本地模式
                    imageURLList = bundle.getStringArrayList("imageURLList");
                    downloadFilesToLocal(imageURLList);
                }
                return null;
            }
        }.execute();
    }

    /**
     * 下载图片至Bitmap列表方法
     *
     * AsyncTask调用
     *
     * @param urlList
     */
    private void downloadFiles(List<String> urlList){
        for(String url : urlList){
            byte[] imageByteArray = downloader.downloadByGet(url);
            this.imageBitMapList.add(BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length));
        }
    }

    /**
     * 下载图片至本地并初始化bitmap列表方法
     *
     * AsyncTask调用
     *
     * @param urlList
     */
    private void downloadFilesToLocal(List<String> urlList){
        int i = 0;
        for(String url : urlList){
            i++;
            String filePathName = MainActivity.BASE_TEMP_FILE_PATH + File.separator + "tempImage" + i +".jpg";
            if(this.downloader.downloadByGet(filePathName, url)) {
                this.imageBitMapList.add(BitmapFactory.decodeFile(filePathName));
            }
        }
    }

    /**
     * 通过本地文件初始化BitMap列表
     *
     * AsyncTask调用(可能很耗时)
     *
     * @param filePathNameList
     */
    private void initializeFiles(List<String> filePathNameList){
        for(String pathName : filePathNameList){
            if(new File(pathName).exists()){
                this.imageBitMapList.add(BitmapFactory.decodeFile(pathName));
            }
        }
    }

    /**
     * 初始化控件
     */
    private void initView(){
        this.textDisplayName = (TextView)findViewById(R.id.imageDispalyActivity_textView_displayName);
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
                if(imageBitMapList.size() > 0){
                    if(imageIndex > 0){
                        imageIndex--;
                        imageViewDisplay.setImageBitmap(imageBitMapList.get(imageIndex));
                    }
                }
            }
        });
        this.btnNextImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageBitMapList.size() > 0){
                    if(imageIndex < (imageBitMapList.size()-1)) {
                        imageIndex++;
                        imageViewDisplay.setImageBitmap(imageBitMapList.get(imageIndex));
                    }
                }
            }
        });
    }
}
