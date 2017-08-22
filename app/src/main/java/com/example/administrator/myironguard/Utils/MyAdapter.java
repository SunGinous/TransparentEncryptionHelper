package com.example.administrator.myironguard.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.ViewDragHelper;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.administrator.myironguard.Activity.PictureActivity;
import com.example.administrator.myironguard.Bean.Aes_bean;
import com.example.administrator.myironguard.Db.Mydao;
import com.example.administrator.myironguard.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/12/24.
 * 因为要实现解密图片时同步显示解密进度的效果，需要adapter与Activity间通过handler传递数据，adapter必须为
 * Activity的内部类，因此该MyAdapter不再使用，转而使用PictureActivity内的MyAdapternn
 */
public class MyAdapter extends BaseAdapter {
    private Handler handler=null;
    private ArrayList<Aes_bean> mAes_beanArrayList;
    private Context mContext;
    public  MyAdapter( ArrayList<Aes_bean> mAes_beanArrayList,Context mContext)
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
        handler=new Handler();
        try{
            final Aes_bean aes_bean = mAes_beanArrayList.get(position);
            Bitmap bitmap= BitmapFactory.decodeFile(aes_bean.sca);
            final ViewHolder viewHolder;
            final int[] len = {-1};
            final byte buf[]  = new byte[1024];
            if(convertView==null){
                convertView= LayoutInflater.from(mContext).inflate(R.layout.listview_item,null);
                viewHolder = new ViewHolder();
                viewHolder.button=(Button)convertView.findViewById(R.id.item_bnt);
                viewHolder.imageView=(ImageView)convertView.findViewById(R.id.item_iv);
                convertView.setTag(viewHolder);
            }
            else{
                viewHolder=(ViewHolder)convertView.getTag();
            }
            viewHolder.imageView.setImageBitmap(bitmap);
            Log.v("aes_bean.des",aes_bean.des);
            Log.v("aes_bean.viewpath",aes_bean.viewpath);
            final View finalConvertView = convertView;
            viewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TimeInfo","开始解密");
                /*
                 *位移动画
                 */
                    AnimationSet animationSet=new AnimationSet(true);
                    TranslateAnimation translateAnimation = new TranslateAnimation(1,1500,0,0);
                    translateAnimation.setDuration(500);
                    animationSet.addAnimation(translateAnimation);
                    finalConvertView.startAnimation(animationSet);

                    try {
                        File file =new File(aes_bean.des);
                        final File fileSCA=new File(aes_bean.sca);
                        Log.v("aes_bean.des",aes_bean.des);
                        Log.v("aes_bean.viewpath",aes_bean.viewpath);

                        byte [] bytes =Base64.decode(aes_bean.key,Base64.NO_WRAP);
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
                            Message message=new Message();
                            message.arg1=((int) ((now_length / (float) total_length) * 100));
                        }
                        inputStream.close();
                        fileOutputStream.close();
                        Toast.makeText(mContext,"解密成功",Toast.LENGTH_SHORT).show();
                        file.delete();
                        fileSCA.delete();
                        Log.d("TimeInfo","照片解密完成");

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
                        Mydao mydao = new Mydao(mContext);
                        mydao.delete(aes_bean);
                        new Thread(){
                            @Override
                            public void run(){
                                super.run();
                                try {
                                    Thread.sleep(500);
                                    mAes_beanArrayList.remove(position);
                                    handler.post(runUI);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(mContext,"操作太快",Toast.LENGTH_SHORT).show();
        }
        return convertView;
    }

    Runnable runUI=new Runnable() {
        @Override
        public void run() {
            notifyDataSetChanged();
        }
    };
    class ViewHolder{
        Button button;
        ImageView imageView;
    }
}
