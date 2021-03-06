import 'dart:async';

import 'package:flutter/material.dart';
import 'package:pangolin/pangolin.dart' as Pangolin;
import 'package:permission_handler/permission_handler.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  bool initialized = false;

  @override
  void initState() {
    Pangolin.pangolinResponseEventHandler.listen((value) {
      if (value is Pangolin.onRewardResponse) {
        print("激励视频回调：${value.rewardVerify}");
        print("激励视频回调：${value.rewardName}");
        print("激励视频回调：${value.rewardAmount}");
      } else {
        print("回调类型不符合");
      }
    });
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;

    Map<Permission, PermissionStatus> statuses = await [
      Permission.phone,
      Permission.location,
      Permission.storage,
    ].request();
    //校验权限
    if (statuses[Permission.location] != PermissionStatus.granted) {
      print("无位置权限");
    }
    _initPangolin();
    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  _initPangolin() async {
    await Pangolin.registerPangolin(
            appId: "5065491",
            useTextureView: true,
            appName: "爱看",
            allowShowNotify: true,
            allowShowPageWhenScreenLock: true,
            debug: true,
            supportMultiProcess: true)
        .then((v) {
//      _loadBannerExpressAd();
      setState(() {
        initialized = true;
      });
    });
  }

//  _loadSplashAd() async {
//    Pangolin.loadSplashAd(mCodeId: "887323415", debug: false);
//  }

//  _loadBannerExpressAd() async {
//    Pangolin.loadBannerExpressAd(mCodeId: "945174173", debug: false);
//  }

  //945122969
  _loadRewardAd() async {
    await Pangolin.loadRewardAd(
        isHorizontal: false,
        debug: false,
        mCodeId: "945180719",
        supportDeepLink: true,
        rewardName: "书币",
        rewardAmount: 3,
        isExpress: true,
        expressViewAcceptedSizeH: 500,
        expressViewAcceptedSizeW: 500,
        userID: "user123",
        mediaExtra: "media_extra");
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Stack(
          children: [
            Center(
              child: FlatButton(
                onPressed: () {},
                child: Text("Pangolin"),
              ),
            ),
            if (initialized)
              Pangolin.SplashAdView(
                mCodeId: '887323415',
                debug: false,
                asdIsFinish: () {
                  print('finished!!!');
                },
              ),
          ],
        ),
      ),
    );
  }
}
