package com.tongyangsheng.pangolin;

import android.app.Activity;
import android.content.Context;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class SplashViewFactory extends PlatformViewFactory {
    private final BinaryMessenger messenger;
    private final Activity activity;

    public FlutterAdsSplashView getView() {
        return flutterAdsSplashView;
    }

    private FlutterAdsSplashView flutterAdsSplashView;


    public SplashViewFactory(Activity activity, BinaryMessenger messenger) {
        super(StandardMessageCodec.INSTANCE);
        this.messenger = messenger;
        this.activity = activity;
    }

    @Override
    public PlatformView create(Context context, int id, Object o) {
        flutterAdsSplashView = new FlutterAdsSplashView(activity, context, this.messenger, id);
        return flutterAdsSplashView;
    }
}
