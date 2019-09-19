package tw.org.iii.appps.h22_webview;
//**目的連接webView,1.讓頁面導入我們的webView 2.按下返回鍵ok 3.讓js顯示出來
//你的用戶不用在那邊搜尋
//可以照相,gps定位,資料可以傳遞,這是網頁不同,可以掃條碼,這是app的好處,沒有這些功能用web-view就好
//1.配置webview夜面,initWebView();
//2.網路權限開啟<uses-permission android:name="android.permission.INTERNET"/>
//3.使用明瑪傳送true   android:usesCleartextTraffic="true"
//4.js才是玩web-view的重點

//**以下目的是讓我們安卓也可以撰寫js方法
//1.app => new => Folder => Assets Forder 資源不大包進去還ok,可把關卡的資料放這
//2.在assets新增File寫網頁html
//3.也可以套bootstrap要intentr權限
//4.也可以寫js,但有一些類似alert等止在瀏覽器有用,行動裝置沒有
//5.如果要加入圖片assets=>Directory=>imgs
//6.也可以連接到第二html還有跟行動裝置元件互動

//goggle map要這功能要信用卡,goggle地圖
//有兩種方式,目前這種事javascript
//1.搜尋google map javascript方式
//2.複製他的hellow方式,寫一頁新的html,其中有一個key參數就是等你信用卡寫好後,改植才能傭有的功能
//map:https://developers.google.com/maps/documentation/javascript/tutorial
//gps:要打開這兩個設定
//<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
//<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
//brad.html當你輸入數值,要用java來抓
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
   private WebView webView;
   private EditText num;
   private LocationManager lmgr;//gps物件
     private MyListener listener; //gps要準備的監聽者
    private TextView urname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //8.gps權限打開
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},//gps連線全線
                    123);
        }
        listener = new MyListener();
        urname = findViewById(R.id.hello);
        lmgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);//取得系統伺服器gps允許權限
        num = findViewById(R.id.num); //抓到editText輸入熱透的id
        webView = findViewById(R.id.webview);//初始化抓到webview
        initWebView();
    }

    //9.//用java取得js.html的物件方法
    public  class  MyJSObject{
        @JavascriptInterface //跟JS的溝通橋梁要讓他認識
        public  void callFromJS(String urname){ //這個方法將會被js呼叫,當你js輸入名字時這邊會顯示出來
            Log.v("brad", "Hello, " + urname);
            MainActivity.this.urname.setText("Hello, " + urname);
        }
    }

    //8.gps打開時連接定位追蹤定位
    @SuppressLint("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();
        lmgr.requestLocationUpdates( //(1.gps 2.幾秒 3.幾公尺 .監聽者)
                LocationManager.GPS_PROVIDER, 1*1000,10, listener);
    }
    //9.關掉gps時,移除監聽事件
    @Override
    protected void onStop() {
        super.onStop();
        lmgr.removeUpdates(listener);//移除gps監聽事件
    }

    //7.叫出gps接聽者,時做呼叫js方法,抓到gps使用者的經緯度參數,帶到移動js方法
    private  class MyListener implements LocationListener{
        //當gps位置轉移時你的location參數傳度,才可以玩
        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();//取得使用者的緯度
            double lng = location.getLongitude();//取得使用者的精度
            Log.v("brad","onLocationChanged:" +lat + ", " + lng );
            webView.loadUrl("javascript:moveTo(" + lat + ", " + lng + ")");//使用妳寫好的輸入經緯度顯示做標,追蹤你到哪地圖到哪
        }


        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    //1.初始化webview方法
    private  void initWebView(){
//        webView.loadUrl("http://https://www.iii.org.tw"); //連接外部url要接的web("網址");
        webView.loadUrl("file:///android_asset/brad.html");//file內部檔案:三根斜線通訊協定/安卓程式_asset存放區不加s因為只有單一頁面/html頁面
        WebViewClient webViewClient = new WebViewClient();//網頁視野客戶端處理物件
        webView.setWebViewClient(webViewClient);//設定網頁客戶端使用(webViewClient ),這樣使用我們自己的view到下一頁
        // 3.設定讓js可以使用
        WebSettings Settings = webView.getSettings();//webView.取得設定物件(回傳WebSettings)
        Settings.setJavaScriptEnabled(true); //設定js是否開啟(bollean是/否)
        //4.一開始圖片很大張,讓圖片可以縮放,打開三個設定才能進行縮放
        Settings.setSupportZoom(true);//設定圖片支援縮放(是/否)
        Settings.setBuiltInZoomControls(true);//設定內部的縮放控制開關(是/否)
        Settings.setDisplayZoomControls(true);//設定顯示縮放控制開關(是/否)

        //5.讓圖片大小直接跟你的螢幕寬依樣
        Settings.setUseWideViewPort(true);//設定使用者的圖片跟螢幕裝置一樣寬
        Settings.setLoadWithOverviewMode(true);//設定當你讀黨時圖片寬就跟全景一樣寬
    }

    //2.當按下返回鍵時,因為你是webview所以回上一頁就離開app,改成讓案goback建不會離開
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.v("brad","onKeyDown:" + keyCode); //發現keyCode 是4時,代表有按下叫onBackPressed()去處理
        if(keyCode == 4 && webView.canGoBack()){//如果有案ketcode ==4 代表有案而且webview.有按返回鍵嗎?
            webView.goBack();//webView.返回動作
        }
        return true; //這邊叫爸改成回傳值true就不會真的回上一頁關掉
    }

    //設置返回按鈕介紹
    public void goForward(View view) {
        webView.goForward(); //設置返回鍵,回到你按上衣頁的功能
        Log.v("brad","goForward有到");
    }


    public void reload(View view) {
        webView.reload();
        Log.v("brad","reload有到");
    }

    //6.利用行動裝置的按鈕,連接呼叫自己寫的js方法
    public void lottery(View view) {
        webView.loadUrl("javascript:test1("+num.getText().toString()+")");//呼叫樂透方法,且帶到edeitex處理,用戶輸入的值呈現參數,帶到js
    }


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }
}
