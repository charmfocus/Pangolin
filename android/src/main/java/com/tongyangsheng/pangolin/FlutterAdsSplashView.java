package com.tongyangsheng.pangolin;


import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.bytedance.sdk.openadsdk.TTSplashAd;

import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.StringCodec;
import io.flutter.plugin.platform.PlatformView;

public class FlutterAdsSplashView implements PlatformView, MethodChannel.MethodCallHandler {
    private static final String TAG = "FlutterAdsSplashView";
    private final FrameLayout rootView;
    private TTAdNative mTTAdNative;
    private TTNativeExpressAd mTTAd;

    //开屏广告加载超时时间,建议大于3000,这里为了冷启动第一次加载到广告并且展示,示例设置了3000ms
    private static final int AD_TIME_OUT = 3000;

    private final static String splashEventName = "plugins.pangolin.ads.event/splashview";

    private Handler handler;

    private static BasicMessageChannel<String> runTimeSender;
    private Context mContext;
    private Activity mActivity;


    FlutterAdsSplashView(Activity activity, Context context, BinaryMessenger messenger, int id) {
        mActivity = activity;
        mContext = context;
        MethodChannel methodChannel = new MethodChannel(messenger, "splash_" + id);
        methodChannel.setMethodCallHandler(this);

        rootView = new FrameLayout(context);
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootView.setLayoutParams(layoutParams);
//        LinearLayout flAd = new LinearLayout(context);
//        rootView.addView(flAd);
        this.handler = new Handler(Looper.getMainLooper());
        runTimeSender = new BasicMessageChannel<String>(messenger, splashEventName, StringCodec.INSTANCE);

        TTAdManager ttAdManager = TTAdManagerHolder.get();

        mTTAdNative = ttAdManager.createAdNative(context);
    }

    @Override
    public View getView() {
        return rootView;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        switch (call.method) {
            case "setId":
                loadAd(call, result);
                break;
            default:
                result.notImplemented();
        }
    }

    private void loadAd(MethodCall call, MethodChannel.Result result) {
        String mCodeId = call.argument("mCodeId");
        Boolean debug = call.argument("debug");
        Boolean supportDeepLink = call.argument("supportDeepLink");
        Boolean isExpress = call.argument("isExpress");
        double width = call.argument("width");
        double height = call.argument("height");

        //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = null;
        if (isExpress) {
            //个性化模板广告需要传入期望广告view的宽、高，单位dp，请传入实际需要的大小，
            //比如：广告下方拼接logo、适配刘海屏等，需要考虑实际广告大小
            float expressViewWidth = width == 0 ? UIUtils.getScreenWidthDp(mActivity) : (float) width;
            float expressViewHeight = height == 0 ? UIUtils.getHeight(mActivity) : (float) height;

            adSlot = new AdSlot.Builder()
                    .setCodeId(mCodeId)
                    .setSupportDeepLink(supportDeepLink)
                    .setImageAcceptedSize(1080, 1920)
                    //模板广告需要设置期望个性化模板广告的大小,单位dp,代码位是否属于个性化模板广告，请在穿山甲平台查看
                    .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight)
                    .build();
        } else {
            adSlot = new AdSlot.Builder()
                    .setCodeId(mCodeId)
                    .setSupportDeepLink(supportDeepLink)
                    .setImageAcceptedSize(1080, 1920)
                    .build();
        }


        //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
        mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
            @Override
            @MainThread
            public void onError(int code, String message) {
                Log.d(TAG, String.valueOf(message));
//                TToast.show(mContext, message);
                send("failure");
            }

            @Override
            @MainThread
            public void onTimeout() {
//                TToast.show(mContext, "开屏广告加载超时");
                send("timeout");
            }

            @Override
            @MainThread
            public void onSplashAdLoad(TTSplashAd ad) {
                Log.d(TAG, "开屏广告请求成功");
                if (ad == null) {
                    return;
                }
                //获取SplashView
                View view = ad.getSplashView();
                if (rootView != null) {
                    rootView.removeAllViews();
                    //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕高
                    rootView.addView(view);
                    //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
                    //ad.setNotAllowSdkCountdown();
                } else {
                    send("finish");
                }

                //设置SplashView的交互监听器
                ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        Log.d(TAG, "onAdClicked");
//                        TToast.show(mContext, "开屏广告点击");
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                        Log.d(TAG, "onAdShow");
//                        TToast.show(mContext, "开屏广告展示");
                    }

                    @Override
                    public void onAdSkip() {
                        Log.d(TAG, "onAdSkip");
//                        TToast.show(mContext, "开屏广告跳过");
                        send("finish");

                    }

                    @Override
                    public void onAdTimeOver() {
                        Log.d(TAG, "onAdTimeOver");
//                        TToast.show(mContext, "开屏广告倒计时结束");
                        send("finish");
                    }
                });
                if (ad.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
                    ad.setDownloadListener(new TTAppDownloadListener() {
                        boolean hasShow = false;

                        @Override
                        public void onIdle() {
                        }

                        @Override
                        public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                            if (!hasShow) {
//                                TToast.show(mContext, "下载中...");
                                hasShow = true;
                            }
                        }

                        @Override
                        public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
//                            TToast.show(mContext, "下载暂停...");

                        }

                        @Override
                        public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
//                            TToast.show(mContext, "下载失败...");

                        }

                        @Override
                        public void onDownloadFinished(long totalBytes, String fileName, String appName) {
//                            TToast.show(mContext, "下载完成...");

                        }

                        @Override
                        public void onInstalled(String fileName, String appName) {
//                            TToast.show(mContext, "安装完成...");

                        }
                    });
                }
            }
        }, AD_TIME_OUT);

    }

    void send(final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                runTimeSender.send(msg);
            }
        });
    }
}
