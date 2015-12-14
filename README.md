# PhoneNumber
A library to get phone number location and other info from baidu api.

## Usage

**1\. GRADLE**
```
compile 'org.xdty.phone.number:phone-number:0.0.2'
```

**2\. Get the phone number info**

```
PhoneNumber number = PhoneNumber.key("YOUR_API_KEY");
String s = number.get("10086", "PHONE_NUMBER");
```
