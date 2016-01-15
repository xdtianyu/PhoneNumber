# Custom API support

API server is `https://www.example.com/api`, client request:

```
curl https://www.example.com/api?tel=02151860253&key=xxxxxxxxxxxxxxxxxxxxxxx
```

Server return:

```
{
    "reason": "查询成功",
    "result": {
        "province": "",    /*号码所属省份*/
        "city": "上海",     /*号码所属城市*/
        "provider": "",        /*号码所属运营商*/
        "phone": "02151860253",     /*查询号码*/
        "name": "张三",
        "company": "上海xxxxxx有限公司",
        "info": ""
    },
    "error_code": 0   /*返回码：0表示查询成功*/
}
```
