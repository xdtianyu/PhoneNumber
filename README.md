# PhoneNumber
A library to get phone number location and other info from baidu api.

[![JAR](https://img.shields.io/maven-central/v/org.xdty.phone.number/phone-number.svg)](http://repo1.maven.org/maven2/org/xdty/phone/number/phone-number/0.0.3/phone-number-0.0.3.aar)
[ ![Download](https://api.bintray.com/packages/xdtianyu/maven/phone-number/images/download.svg) ](https://bintray.com/xdtianyu/maven/phone-number/_latestVersion)

## Download

Grab via gradle

```
dependencies {
    compile 'org.xdty.phone.number:phone-number:0.0.3'
}
```

or maven

```
<dependency>
  <groupId>org.xdty.phone.number</groupId>
  <artifactId>phone-number</artifactId>
  <version>0.0.3</version>
  <type>aar</type>
</dependency>
```

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
    }
}).fetch("10086", "PHONE_NUMBER");
```
