# PhoneNumber
A library which can get phone number geo location and other infomation from local or network (baidu, juhe or custom api).

[![Build Status](https://travis-ci.org/xdtianyu/PhoneNumber.svg?branch=master)](https://travis-ci.org/xdtianyu/PhoneNumber)
[![JAR](https://img.shields.io/maven-central/v/org.xdty.phone.number/phone-number.svg)](http://central.maven.org/maven2/org/xdty/phone/number/phone-number/)
[![Download](https://api.bintray.com/packages/xdtianyu/maven/phone-number/images/download.svg)](https://bintray.com/xdtianyu/maven/phone-number/_latestVersion)

## Download

Grab via gradle

```groovy
dependencies {
    compile 'org.xdty.phone.number:phone-number:0.6.0'
}
```

or maven

```xml
<dependency>
  <groupId>org.xdty.phone.number</groupId>
  <artifactId>phone-number</artifactId>
  <version>0.6.0</version>
  <type>aar</type>
</dependency>
```

or JAR from [maven central](http://central.maven.org/maven2/org/xdty/phone/number/phone-number/)

## Usage

1\. Add `meta-data` to `AndroidManifest`

```xml
<meta-data
    android:name="org.xdty.phone.number.API_KEY"
    android:value="YOUR_API_KEY"/>
```

You can get `YOUR_API_KEY` from [Baidu apistore's usercenter](http://apistore.baidu.com/astore/usercenter)

2\. Add these lines to `MainActivity`, For more details, see [example](https://github.com/xdtianyu/PhoneNumber/tree/master/example) and [CallerInfo](https://github.com/xdtianyu/CallerInfo)

```java
new PhoneNumber(this, new PhoneNumber.Callback() {
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
