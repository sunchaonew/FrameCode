package com.dahanis.main.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;

import com.dahanis.main.R;
import com.dh.foundation.utils.download.DownLoadUtil;
import com.dh.foundation.utils.download.DownloadListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 文件下载示例demo
 * Created By: Seal.Wu
 * Date: 2015/10/9
 * Time: 14:29
 */
public class DownloadActivity extends Activity {

    String url = "http://cdn6.down.apk.gfan.com/asdf/Pfiles/2015/9/21/148567_5cc64a68-393b-4872-ae54-739873c146bc.apk";


    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.startDownLoad)
    Button startDownLoad;
    @Bind(R.id.reDownload)
    Button reDownload;


    @OnClick(R.id.startDownLoad)
    void startDownLoad() {

        DownLoadUtil.getInstance().startADownloadTask(url, new DownloadListener() {
            @Override
            public void onLoadChange(int total, int currentSize, int state) {

                progressBar.setMax(total);

                progressBar.setProgress(currentSize);

            }

            @Override
            public void onComplete(long downloadId, String filePath) {
                startDownLoad.setText("下载完成");
                reDownload.setText("重新下载");
            }
        });
    }


    @OnClick(R.id.reDownload)
    void reDownLoad() {
        startDownLoad.setText("开始下载");
        reDownload.setText("正在下载");
        startDownLoad();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_donwload);
        ButterKnife.bind(this);


    }

}