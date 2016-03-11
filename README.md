# PhoneNumber
A library to get phone number location and other info from baidu and juhe api.


[![Build Status](https://travis-ci.org/xdtianyu/PhoneNumber.svg?branch=master)](https://travis-ci.org/xdtianyu/PhoneNumber)
[![JAR](https://img.shields.io/maven-central/v/org.xdty.phone.number/phone-number.svg)](http://central.maven.org/maven2/org/xdty/phone/number/phone-number/)
[![Download](https://api.bintray.com/packages/xdtianyu/maven/phone-number/images/download.svg)](https://bintray.com/xdtianyu/maven/phone-number/_latestVersion)

## Download

Grab via gradle

```
dependencies {
    compile 'org.xdty.phone.number:phone-number:0.4.0'
}
```

or maven

```
<dependency>
  <groupId>org.xdty.phone.number</groupId>
  <artifactId>phone-number</artifactId>
  <version>0.4.0</version>
  <type>aar</type>
</dependency>
```

or JAR from [maven central](http://central.maven.org/maven2/org/xdty/phone/number/phone-number/)

## Usage

1\. Add `meta-data` to `AndroidManifest`

```
<meta-data
    android:name="org.xdty.phone.number.API_KEY"
    android:value="YOUR_API_KEY"/>
```

You can get `YOUR_API_KEY` from [Baidu apistore's usercenter](http://apistore.baidu.com/astore/usercenter)

2\. Add these lines to `MainActivity`, For more details, see [example](https://github.com/xdtianyu/PhoneNumber/tree/master/example) and [CallerInfo](https://github.com/xdtianyu/CallerInfo)

```
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
