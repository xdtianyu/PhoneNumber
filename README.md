# PhoneNumber
A library to get phone number location and other info from baidu api.


[![Build Status](https://travis-ci.org/xdtianyu/PhoneNumber.svg?branch=master)](https://travis-ci.org/xdtianyu/PhoneNumber)

[![JAR](https://img.shields.io/maven-central/v/org.xdty.phone.number/phone-number.svg)](http://central.maven.org/maven2/org/xdty/phone/number/phone-number/)
[ ![Download](https://api.bintray.com/packages/xdtianyu/maven/phone-number/images/download.svg) ](https://bintray.com/xdtianyu/maven/phone-number/_latestVersion)

## Download

Grab via gradle

```
dependencies {
    compile 'org.xdty.phone.number:phone-number:0.0.5'
}
```

or maven

```
<dependency>
  <groupId>org.xdty.phone.number</groupId>
  <artifactId>phone-number</artifactId>
  <version>0.0.5</version>
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

2\. Add these lines to `MainActivity`, For more details, see [example](https://github.com/xdtianyu/PhoneNumber/tree/master/example)

```
new PhoneNumber(this, new PhoneNumber.Callback() {
    @Override
    public void onResponse(NumberInfo numberInfo) {
        // Do your jobs here
        textView.setText(numberInfo.toString());

        List<Number> numbers = numberInfo.getNumbers();
        for (Number number : numbers) {
            Log.d(TAG, number.getNumber() +
                       ": " +
                       number.getType().getText() +
                       ", " +
                       number.getName() +
                       ", " +
                       number.getCount());
        }
    }

    @Override
    public void onResponseFailed(NumberInfo numberInfo) {
        ResponseHeader responseHeader = numberInfo.getResponseHeader();
        if (responseHeader != null) {
            textView.setText(responseHeader.getStatus());
        }
    }
}).fetch("10086", "10000, 10001", "OTHER_PHONE_NUMBER");
```
