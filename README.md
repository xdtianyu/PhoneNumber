# PhoneNumber
一个获取号码归属地和其他信息（诈骗、骚扰等）的开源库。支持本地离线（含归属地、骚扰、常用号码）和网络（百度、聚合数据或自定义 API）查询。[English](https://github.com/xdtianyu/PhoneNumber/blob/master/README-EN.md)

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-PhoneNumber-green.svg?style=true)](https://android-arsenal.com/details/1/3325)
[![Build Status](https://travis-ci.org/xdtianyu/PhoneNumber.svg?branch=master)](https://travis-ci.org/xdtianyu/PhoneNumber)
[![JAR](https://img.shields.io/maven-central/v/org.xdty.phone.number/phone-number.svg)](http://central.maven.org/maven2/org/xdty/phone/number/phone-number/)
[![Download](https://api.bintray.com/packages/xdtianyu/maven/phone-number/images/download.svg)](https://bintray.com/xdtianyu/maven/phone-number/_latestVersion)

## 下载

通过 gradle 下载

```groovy
dependencies {
    compile 'org.xdty.phone.number:phone-number:0.5.1'
}
```

或 maven

```xml
<dependency>
  <groupId>org.xdty.phone.number</groupId>
  <artifactId>phone-number</artifactId>
  <version>0.5.1</version>
  <type>aar</type>
</dependency>
```

或从 [maven central](http://central.maven.org/maven2/org/xdty/phone/number/phone-number/) 直接下载 AAR 文件

## 用法

1\. 添加 `meta-data` 到 `AndroidManifest`

```xml
<meta-data
    android:name="org.xdty.phone.number.API_KEY"
    android:value="API_KEY"/>
<meta-data
    android:name="org.xdty.phone.number.JUHE_API_KEY"
    android:value="JUHE_API_KEY"/>
```

你可以从 [百度 API 中心](http://apistore.baidu.com/astore/usercenter) 获取 `API_KEY` , 从 [聚合数据 (360)](https://www.juhe.cn/docs/api/id/72) 获取 `JUHE_API_KEY`。这一部分是用于联网查询的 API 私钥。

也可以在代码中设置 `baidu_api_key` 和 `juhe_api_key` 的 `SharedPreferences` 来动态控制 API 密钥，更多内容请参考 [来电信息](https://github.com/xdtianyu/CallerInfo) 的实现。

2\. 代码中添加如下内容, 更多内容请参考 [example](https://github.com/xdtianyu/PhoneNumber/tree/master/example) 和 [CallerInfo](https://github.com/xdtianyu/CallerInfo)

```java
new PhoneNumber(this, new PhoneNumber.Callback() {
    @Override
    public void onResponseOffline(INumber number) {
    }
    
    @Override
    public void onResponse(INumber number) {
        // Do your jobs here
        textView.setText(number.getName());
    }

    @Override
    public void onResponseFailed(INumber number) {
    }
}).fetch("10086", "10000", "10001", "OTHER_PHONE_NUMBER");
```
本地查询会通过 `onResponseOffline(INumber number)` 返回，联网查询会通过 `onResponse(INumber number)` 返回。

3\. 自定义服务器及 API

请参考 [自定义 API 文档](https://github.com/xdtianyu/PhoneNumber/blob/master/CUSTOM.md)

4\. 自定义号码处理器

请参考 [model](https://github.com/xdtianyu/PhoneNumber/tree/master/phone-number/src/main/java/org/xdty/phone/number/model/) 中的实现，实现 `INumber` 和 `NumberHandler` 接口。并在调用 `fetch` 前调用 `addNumberHandler(new YourCustomNumberHandler())`。

5\. 禁用联网查询

请设置 `only_offline_key` 的 `SharedPreferences` 来全局控制，或使用 `PhoneNumber(context, true, callback)` 临时控制。

6\. 优先查询控制

默认的查询顺序为 `本地特殊号码->本地常用号码->本地标记号码->本地离线归属地->Google离线归属地->联网自定义 API->联网百度 API->联网聚合数据(360) API` 。可以通过设置 `api_type` 的 `SharedPreferences` 来控制优先的网络查询接口，值为每个模块的 `getApiId()` 返回值。
