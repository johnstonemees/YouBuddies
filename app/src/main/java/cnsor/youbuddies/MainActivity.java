package cnsor.youbuddies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;




import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SpeechConstant;

import cnsor.youbuddies.utils.speechWakeUp;
import cnsor.youbuddies.utils.util;

/*
* 初始化语音唤醒、语音识别和语音合成
* 先通过语音唤醒，唤醒词为“讯飞语音”，唤醒后开始进行用户语音识别，并将识别结果合成语音反馈给用户*/
public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private util mUtil;
    private TextView wakeupResult;
    private speechWakeUp mSpeechWake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUtil = new util();

        initResource();
        // 初始化讯飞接口
        SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID + "=" + getString(R.string.app_id));
        mSpeechWake = speechWakeUp.getInstance();
        mSpeechWake.initWakeupEngine(getApplicationContext());
        mSpeechWake.startWakeupListen();
    }

    private void initResource() {
        wakeupResult = (TextView) findViewById(R.id.textView);
        wakeupResult.setText("等待唤醒中....");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时释放连接

        Log.d(TAG, "onDestroy WakeDemo");
        if (mSpeechWake != null) {
            mSpeechWake.destroy();
        } else {
            mUtil.showTip(MainActivity.this, "唤醒未初始化");
        }
    }
}
