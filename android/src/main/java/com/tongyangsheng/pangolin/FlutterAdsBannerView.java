package com.tongyangsheng.pangolin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.util.List;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;

public class FlutterAdsBannerView implements PlatformView, MethodChannel.MethodCallHandler {

    private final FrameLayout rootView;
    private TTAdNative mTTAdNative;
    private TTNativeExpressAd mTTAd;

    private long startTime = 0;
    private boolean mHasShowDownloadActive = false;
    public Context mContext;
    private Activity mActivity;


    public FlutterAdsBannerView(Activity activity, Context context, BinaryMessenger messenger, int id) {
        mActivity = activity;
        mContext = context;
        MethodChannel methodChannel = new MethodChannel(messenger, "banner_" + id);
        methodChannel.setMethodCallHandler(this);

        rootView = new FrameLayout(context);
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rootView.setLayoutParams(layoutParams);
        rootView.setBackgroundColor(Color.WHITE);
        TTAdManager ttAdManager = TTAdManagerHolder.get();

        mTTAdNative = ttAdManager.createAdNative(context);
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

    @Override
    public View getView() {
        return rootView;
    }

    @Override
    public void dispose() {
    }

    private void loadAd(MethodCall call, MethodChannel.Result result) {
        String mCodeId = call.argument("mCodeId");
        Boolean debug = call.argument("debug");
        Boolean supportDeepLink = call.argument("supportDeepLink");
        double width = call.argument("width");
        double height = call.argument("height");
        int adCount = call.argument("adCount");

        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(mCodeId) //广告位id
                .setSupportDeepLink(supportDeepLink)
                .setAdCount(adCount) //请求广告数量为1到3条
                .setExpressViewAcceptedSize((float) width, (float) height) //期望个性化模板广告view的size,单位dp
                .setImageAcceptedSize(640, 320)//这个参数设置即可，不影响个性化模板广告的size
                .build();
        //加载广告
        mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                rootView.removeAllViews();
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> list) {
                if (list == null || list.size() == 0) {
                    return;
                }
                mTTAd = list.get(0);
                mTTAd.setSlideIntervalTime(30 * 1000);//设置轮播间隔 ms,不调用则不进行轮播展示
                bindAdListener(mTTAd);
                mTTAd.render();//调用render开始渲染广告
            }
        });

        result.success(null);

    }

    //绑定广告行为
    private void bindAdListener(TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
//                TToast.show(mContext, "广告被点击");
            }

            @Override
            public void onAdShow(View view, int type) {
//                TToast.show(mContext, "广告展示");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.e("ExpressView", "render fail:" + (System.currentTimeMillis() - startTime));
//                TToast.show(mContext, msg + " code:" + code);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                //返回view的宽高 单位 dp
//                TToast.show(mContext, "渲染成功");
                //在渲染成功回调时展示广告，提升体验
                rootView.removeAllViews();
                rootView.addView(view);
            }
        });
        //dislike设置
//        bindDislike(ad, false);
        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            return;
        }
        //可选，下载监听设置
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
//                TToast.show(mContext, "点击开始下载", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
//                    TToast.show(mContext, "下载中，点击暂停", Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
//                TToast.show(mContext, "下载暂停，点击继续", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
//                TToast.show(mContext, "下载失败，点击重新下载", Toast.LENGTH_LONG);
            }

            @Override
            public void onInstalled(String fileName, String appName) {
//                TToast.show(mContext, "安装完成，点击图片打开", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
//                TToast.show(mContext, "点击安装", Toast.LENGTH_LONG);
            }
        });
    }

}
