# PhoneNumber
A library to get phone number location and other info from baidu api.

## Download

Grab via gradle

```
dependencies {
    compile 'org.xdty.phone.number:phone-number:0.0.2'
}
```

or maven

```
<dependency>
  <groupId>org.xdty.phone.number</groupId>
  <artifactId>phone-number</artifactId>
  <version>0.0.2</version>
  <type>aar</type>
</dependency>
```

or download the latest AAR

[![JAR](https://img.shields.io/maven-central/v/org.xdty.phone.number/phone-number.svg)](http://repo1.maven.org/maven2/org/xdty/phone/number/phone-number/0.0.2/phone-number-0.0.2.aar)
[ ![Download](https://api.bintray.com/packages/xdtianyu/maven/phone-number/images/download.svg) ](https://bintray.com/xdtianyu/maven/phone-number/_latestVersion)
## Usage

```
PhoneNumber number = PhoneNumber.key("YOUR_API_KEY");
String s = number.get("10086", "PHONE_NUMBER");
```
