import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'banner_ad_View.dart';
import 'pangolin.dart';

const int NETWORK_STATE_MOBILE = 1;
const int NETWORK_STATE_2G = 2;
const int NETWORK_STATE_3G = 3;
const int NETWORK_STATE_WIFI = 4;
const int NETWORK_STATE_4G = 5;

MethodChannel _channel = MethodChannel('com.tongyangsheng.pangolin')
  ..setMethodCallHandler(_methodHandler);

StreamController<BasePangolinResponse> _pangolinResponseEventHandlerController =
    new StreamController.broadcast();

Stream<BasePangolinResponse> get pangolinResponseEventHandler =>
    _pangolinResponseEventHandlerController.stream;

Future<bool> registerPangolin({
  @required String appId,
  @required bool useTextureView,
  @required String appName,
  @required bool allowShowNotify,
  @required bool allowShowPageWhenScreenLock,
  @required bool debug,
  @required bool supportMultiProcess,
  List<int> directDownloadNetworkType,
}) async {
  return await _channel.invokeMethod("register", {
    "appId": appId,
    "useTextureView": useTextureView,
    "appName": appName,
    "allowShowNotify": allowShowNotify,
    "allowShowPageWhenScreenLock": allowShowPageWhenScreenLock,
    "debug": debug,
    "supportMultiProcess": supportMultiProcess,
    "directDownloadNetworkType": directDownloadNetworkType ??
        [
          NETWORK_STATE_MOBILE,
          NETWORK_STATE_3G,
          NETWORK_STATE_4G,
          NETWORK_STATE_WIFI
        ]
  });
}

Widget buildBannerAdView({
  @required String mCodeId,
  @required bool debug,
  int adCount = 1,
  double width,
  double height,
}) {
  return BannerAdView(
    mCodeId: mCodeId,
    debug: debug,
    adCount: adCount,
    width: width,
    height: height,
  );
}

Future loadRewardAd({
  @required String mCodeId,
  @required bool debug,
  @required bool supportDeepLink,
  @required String rewardName,
  @required int rewardAmount,
  @required bool isExpress,
  double expressViewAcceptedSizeH,
  double expressViewAcceptedSizeW,
  @required userID,
  String mediaExtra,
  @required bool isHorizontal,
}) async {
  return await _channel.invokeMethod("loadRewardAd", {
    "mCodeId": mCodeId,
    "debug": debug,
    "supportDeepLink": supportDeepLink,
    "rewardName": rewardName,
    "rewardAmount": rewardAmount,
    "isExpress": isExpress,
    "expressViewAcceptedSizeH": expressViewAcceptedSizeH,
    "expressViewAcceptedSizeW": expressViewAcceptedSizeW,
    "userID": userID,
    "mediaExtra": mediaExtra,
    "isHorizontal": isHorizontal,
  });
}

Future _methodHandler(MethodCall methodCall) {
  var response =
      BasePangolinResponse.create(methodCall.method, methodCall.arguments);
  _pangolinResponseEventHandlerController.add(response);
  return Future.value();
}
