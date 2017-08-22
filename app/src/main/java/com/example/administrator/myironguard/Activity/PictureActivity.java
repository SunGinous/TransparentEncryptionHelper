package com.example.administrator.myironguard.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.administrator.myironguard.Bean.Aes_bean;
import com.example.administrator.myironguard.Db.Mydao;
import com.example.administrator.myironguard.R;
import com.example.administrator.myironguard.Utils.AESCoder;
import com.example.administrator.myironguard.Utils.MyAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.administrator.myironguard.Bean.Aes_bean;
import com.example.administrator.myironguard.Service.Bitmap_Service;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import xander.elasticity.ElasticityHelper;

public class PictureActivity extends AppCompatActivity {
    private ListView mListView;
    private Handler handler;
    private ImageView image_blank;
    private TextView text_blank;
    private SwipeRefreshLayout swipeRefreshLayout;
    private float nowY;
    private float nowX;
    private long nowTime;

    private TextView jindu;
    private Handler jinduhandler;
    private Handler positionhandler=null;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        /*5.0以上版本的方法，4.4.4不能用
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();
        */

        /*4.4以上可用，设置透明状态栏*/
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        mListView=(ListView)findViewById(R.id.lv);
        ElasticityHelper.setUpOverScroll(mListView);
        image_blank=(ImageView)findViewById(R.id.image_blank);
        text_blank=(TextView)findViewById(R.id.text_blank);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_layout_picture);
        progressBar=(ProgressBar)findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        /*
         *圆圈背景色
         */
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(this.getResources().getColor(R.color.colorWhite));
        /*
         *圆圈内箭头颜色
         */
        swipeRefreshLayout.setColorSchemeResources(R.color.colorTitle);
        handler=new Handler();
        initData();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ElasticityHelper.setUpOverScroll(mListView);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Mydao mydao =new Mydao(getApplicationContext());
                        ArrayList<Aes_bean> arrayList = mydao.query();
                        Collections.reverse(arrayList);//item倒序排列
                        MyAdapternn myAdapter = new MyAdapternn(arrayList,getApplicationContext());
                        mListView.setAdapter(myAdapter);
                        myAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        if(mListView.getCount()==0){
                            image_blank.setVisibility(View.VISIBLE);
                            text_blank.setVisibility(View.VISIBLE);
                            image_blank.setImageResource(R.drawable.blank_back);
                            text_blank.setText("还没有加密图片");
                        }else {
                            image_blank.setVisibility(View.GONE);
                            text_blank.setVisibility(View.GONE);
                        }
                    }
                },500);
            }
        });

        /*
         *这里的handler都是与adapter进行数据传递的
         */
        jindu=(TextView)findViewById(R.id.jindu);
        jinduhandler=new Handler(){
            @Override
            public void handleMessage(Message message){
                final int progress=message.arg1;
                jindu.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                jindu.setText(progress+""+"%");

                if (progress>=99){
                    jindu.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
                super.handleMessage(message);
            }
        };
        positionhandler=new Handler(){
            @Override
            public void handleMessage(Message message){
                int position=message.arg1;
                Log.d("TimeInfo","接收位置="+position);
                AnimationSet animationSet=new AnimationSet(true);
                TranslateAnimation translateAnimation = new TranslateAnimation(1,1500,0,0);
                translateAnimation.setDuration(500);
                animationSet.addAnimation(translateAnimation);
                mListView.getChildAt(position).startAnimation(animationSet);
            }
        };
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                handler.post(runUI);
            }
        }.start();
    }
        Runnable runUI = new Runnable() {
            @Override
            public void run() {
                final Mydao mydao =new Mydao(getApplicationContext());
                final ArrayList<Aes_bean> arrayList = mydao.query();
                Collections.reverse(arrayList);//item倒序排列
                final MyAdapternn myAdapter = new MyAdapternn(arrayList,getApplicationContext());
                mListView.setAdapter(myAdapter);
                myAdapter.notifyDataSetChanged();
                if(mListView.getCount()==0){
                    image_blank.setVisibility(View.VISIBLE);
                    text_blank.setVisibility(View.VISIBLE);
                    image_blank.setImageResource(R.drawable.blank_back);
                    text_blank.setText("还没有加密图片");
                }else {
                    image_blank.setVisibility(View.GONE);
                    text_blank.setVisibility(View.GONE);
                }
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mListView.setLongClickable(false);
                    }
                });
                mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id) {
                        final AlertDialog mDialog=new AlertDialog.Builder(PictureActivity.this).create();
                        mDialog.show();
                        mDialog.getWindow().setContentView(R.layout.dialog_delete);
                        mDialog.getWindow().findViewById(R.id.btn_no)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mDialog.cancel();
                                    }
                                });
                        mDialog.getWindow().findViewById(R.id.btn_yes)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mDialog.cancel();
                                        try{
                                            Animation.AnimationListener al = new Animation.AnimationListener() {
                                                @Override
                                                public void onAnimationEnd(Animation arg0) {
                                                }
                                                @Override public void onAnimationRepeat(Animation animation) {}
                                                @Override public void onAnimationStart(Animation animation) {}
                                            };
                                            collapse(view, al);

                                            Aes_bean aes_bean = arrayList.get(position);
                                            File file =new File(aes_bean.des);
                                            File file1=new File(aes_bean.viewpath);
                                            final File fileSCA=new File(aes_bean.sca);

                                            new Thread(){
                                                @Override
                                                public void run() {
                                                    super.run();
                                                    try {
                                                        Thread.sleep(500);
                                                        arrayList.remove(position);//不能使用mListView.removeView()!!
                                                        handler.post(runUI);      //这里还是需要handler，在UI线程里处理view,t跳转至**处
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }.start();

                                            new Thread(){
                                                @Override
                                                public void run(){
                                                    super.run();
                                                    try{
                                                        Thread.sleep(600);
                                                        fileSCA.delete();                      //动画完了以后才能删缩略图
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }.start();

                                            file.delete();

                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                Intent mediaScanIntent = new Intent(
                                                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                                Uri contentUri = Uri.fromFile(file1);
                                                mediaScanIntent.setData(contentUri);
                                                getApplicationContext().sendBroadcast(mediaScanIntent);
                                            } else {
                                                getApplicationContext().sendBroadcast(new Intent(
                                                        Intent.ACTION_MEDIA_MOUNTED,
                                                        Uri.parse("file://"
                                                                + Environment.getExternalStorageDirectory())));
                                            }

                                            Toast.makeText(PictureActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                                            mydao.delete(aes_bean);
                                            myAdapter.notifyDataSetChanged();      //这里是**处
                                        }catch (Exception e){
                                            Toast.makeText(PictureActivity.this,"操作太快",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        return false;
                    }
                });
           }
        };

    /**
     * @param v  ListView的Item
     * @param al 设置好的Animation
     * 被删除item的下面的item依次上移
     */
    private void collapse(final View v, Animation.AnimationListener al) {
        final int initialHeight = v.getMeasuredHeight();

        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                }
                else {
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        if (al!=null) {
            anim.setAnimationListener(al);
        }
        anim.setDuration(300);
        v.startAnimation(anim);
    }



    @Override
    public void onBackPressed(){
        Intent intent=new Intent(PictureActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0,R.anim.slide_out_right);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (!(mListView.isLongClickable())){
                    mListView.setLongClickable(true);
                }
                nowY=event.getY();
                nowX=event.getX();
                Log.d("TimeInfo","nowY1="+nowY);
                nowTime=System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaY=event.getY()-nowY;
                float deltaX=event.getX()-nowX;
                Log.d("TimeInfo","deltaY="+deltaY);
                if (deltaY!=0){
                    mListView.setClickable(false);
                    mListView.setLongClickable(false);
                    Log.d("TimeInfo","执行刷新");
                }
                WindowManager wm = this.getWindowManager();
                float width = (wm.getDefaultDisplay().getWidth())/15;
                if (nowX<width){
                    Log.d("TimeInfo","nowX="+nowX);
                    if (deltaX>0){
                        Intent intent=new Intent(PictureActivity.this,MainActivity.class);
                        startActivity(intent);
                        PictureActivity.this.finish();
                        overridePendingTransition(0,R.anim.slide_out_right);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                long deltaTime=System.currentTimeMillis()-nowTime;
                Log.d("TimeInfo","deltaTime="+deltaTime);
                if (deltaTime>800){
                    mListView.setClickable(true);
                    mListView.setLongClickable(true);
                    Log.d("TimeInfo","执行长按");
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mListView.setClickable(true);
                mListView.setLongClickable(true);
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /*
     *adapter必须是内部类才能与activity通信
     */
    class MyAdapternn extends BaseAdapter {
        private Handler jiemihandler=null;
        private ArrayList<Aes_bean> mAes_beanArrayList;
        private Context mContext;
        public  MyAdapternn( ArrayList<Aes_bean> mAes_beanArrayList,Context mContext)
        {
            this.mAes_beanArrayList=mAes_beanArrayList;
            this.mContext=mContext;
        }

        @Override
        public int getCount() {
            return mAes_beanArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return mAes_beanArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            jiemihandler=new Handler();
            try{
                final Aes_bean aes_bean = mAes_beanArrayList.get(position);
                Bitmap bitmap= BitmapFactory.decodeFile(aes_bean.sca);
                final ViewHolder viewHolder;
                final int[] len = {-1};
                final byte buf[]  = new byte[1024];
                if(convertView==null){
                    Log.d("convertView","convertView此时为空" + position);
                    convertView= LayoutInflater.from(mContext).inflate(R.layout.listview_item,null);
                    viewHolder = new ViewHolder();
                    viewHolder.button=(Button)convertView.findViewById(R.id.item_bnt);
                    viewHolder.imageView=(ImageView)convertView.findViewById(R.id.item_iv);
                    viewHolder.textView=(TextView)convertView.findViewById(R.id.item_text);
                    viewHolder.imageViewFrom=(ImageView)convertView.findViewById(R.id.item_from);
                    convertView.setTag(viewHolder);
                }
                else{
                    Log.d("convertView","convertView此时不为空" + position);
                    viewHolder=(ViewHolder)convertView.getTag();
                }
                viewHolder.imageView.setImageBitmap(bitmap);
                Log.v("aes_bean.des",aes_bean.des);
                Log.v("aes_bean.viewpath",aes_bean.viewpath);
                /*
                 *加载时间信息
                 */
                long time = System.currentTimeMillis();
                SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日HH:mm");
                Date date=new Date(time);
                String now=format.format(date);
                if (now.substring(0,4).equals(aes_bean.date.substring(0,4))
                        &&now.substring(4,7).equals(aes_bean.date.substring(4,7))
                        &&now.substring(7,10).equals(aes_bean.date.substring(7,10))){
                    viewHolder.textView.setText("今天"+aes_bean.date.substring(11));//年月日均相同
                }else if (!now.substring(0,4).equals(aes_bean.date.substring(0,4))){
                    if (aes_bean.date.substring(5,6).equals("0")){
                        if (aes_bean.date.substring(8,9).equals("0")){
                            viewHolder.textView.setText(aes_bean.date.substring(0,5)+aes_bean.date.substring(6,8)
                                    +aes_bean.date.substring(9));
                        }else {
                            viewHolder.textView.setText(aes_bean.date.substring(0,5)+aes_bean.date.substring(6));
                        }
                    }else {
                        viewHolder.textView.setText(aes_bean.date);//不同年
                    }
                }else {
                    if (aes_bean.date.substring(5, 6).equals("0")) {
                        if (aes_bean.date.substring(8,9).equals("0")){
                            viewHolder.textView.setText(aes_bean.date.substring(6,8)+aes_bean.date.substring(9));
                        }else {
                            viewHolder.textView.setText(aes_bean.date.substring(6));
                        }
                    } else {
                        viewHolder.textView.setText(aes_bean.date.substring(5));//同年不同日期
                    }
                }
                /*
                 *加载图片来源信息
                 */
                if (aes_bean.type==1){
                    viewHolder.imageViewFrom.setImageResource(R.drawable.laizixiangji);
                }else if (aes_bean.type==2){
                    viewHolder.imageViewFrom.setImageResource(R.drawable.laizishoudong);
                }
                viewHolder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("TimeInfo","开始解密");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    final File file =new File(aes_bean.des);
                                    final File fileSCA=new File(aes_bean.sca);
                                    Log.v("aes_bean.des",aes_bean.des);
                                    Log.v("aes_bean.viewpath",aes_bean.viewpath);

                                    byte [] bytes = Base64.decode(aes_bean.key,Base64.NO_WRAP);
                                    InputStream inputStream= AESCoder.decrypt(file,bytes);
                                    File file1 = new File(aes_bean.viewpath);
                                    File dir = new File(file1.getParent());
                                    if (!dir.exists())
                                        dir.mkdirs();
                                    FileOutputStream fileOutputStream = new FileOutputStream(file1);

                                    int now_length=0;
                                    long total_length=file.length();
                                    while((len[0] = inputStream.read(buf)) != -1) {
                                        fileOutputStream.write(buf, 0, len[0]);
                                        now_length+=len[0];
                                        final Message message=new Message();
                                        message.arg1=((int) ((now_length / (float) total_length) * 100));
                                        jinduhandler.sendMessage(message);
                                        Log.d("TimeInfo","正在循环");
                                    }
                                    Log.d("TimeInfo","跳出循环");
                                    inputStream.close();
                                    Log.d("TimeInfo","1");
                                    fileOutputStream.close();
                                    Log.d("TimeInfo","2");
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        Intent mediaScanIntent = new Intent(
                                                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                        Uri contentUri = Uri.fromFile(file1);
                                        mediaScanIntent.setData(contentUri);
                                        mContext.sendBroadcast(mediaScanIntent);
                                    } else {
                                        mContext.sendBroadcast(new Intent(
                                                Intent.ACTION_MEDIA_MOUNTED,
                                                Uri.parse("file://"
                                                        + Environment.getExternalStorageDirectory())));
                                    }

                                    Message message=new Message();
                                    message.arg1=position;
                                    Log.d("TimeInfo","发送位置="+message.arg1);
                                    new Thread(){
                                        @Override
                                        public void run(){
                                            super.run();
                                            try {
                                                fileSCA.delete();
                                                Log.d("TimeInfo","照片解密完成");
                                                Mydao mydao = new Mydao(mContext);
                                                mydao.delete(aes_bean);
                                                mAes_beanArrayList.remove(position);
                                                file.delete();
                                                Log.d("TimeInfo","3");

                                                jiemihandler.post(runUInn);
                                                Log.d("TimeInfo","跳转至主线程");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }.start();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }).start();
                    }
                });
            }catch (Exception e){
                Toast.makeText(mContext,"操作太快",Toast.LENGTH_SHORT).show();
            }
            return convertView;
        }

        Runnable runUInn=new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
                Toast.makeText(PictureActivity.this,"解密成功",Toast.LENGTH_SHORT).show();
            }
        };
        class ViewHolder{
            Button button;
            ImageView imageView;
            TextView textView;
            ImageView imageViewFrom;
        }
    }
}



