package org.xdty.phone.number.model.web;

public interface WebFactory {

    WebConfig SEARCH_360 = new WebConfig("https://m.so.com/s?q=",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36",
            "#mohe-m-mobilecheck",
            "#mohe-m-mobilecheck > div.mohe-cont > div.g-flex.mh-tel-header > div > div.mh-tel-num",
            "#mohe-m-mobilecheck > div.mohe-cont > div.g-flex.mh-tel-header > div > div.mh-tel-adr > a",
            "#mohe-m-mobilecheck > div.mohe-cont > div.g-flex.mh-tel-footer > div.mh-tel-mark",
            "#mohe-m-mobilecheck > div.mohe-cont > div.g-flex.mh-tel-footer > div.g-flex-item.mh-tel-desc > b",
            "#mohe-m-mobilecheck > div.mohe-cont > div.g-flex.mh-tel-header > div > div.mh-tel-adr > p",
            0
    );

    WebConfig SEARCH_BAIDU = new WebConfig("https://m.baidu.com/s?word=",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36",
            "#results > div:nth-child(1) > div.c-container > div.c-row.wa-fraudphone-topbtmsmall > div.c-span10.wa-fraudphone-middle",
            "#results > div:nth-child(1) > div.c-container > h3 > em",
            "#mohe-m-mobilecheck > div.mohe-cont > div.g-flex.mh-tel-header > div > div.mh-tel-adr > a",
            "#results > div:nth-child(1) > div.c-container > div.c-row.wa-fraudphone-topbtmsmall > div.c-span10.wa-fraudphone-middle > p:nth-child(1)",
            "#results > div:nth-child(1) > div.c-container > div.c-row.wa-fraudphone-topbtmsmall > div.c-span10.wa-fraudphone-middle > p.c-color-gray",
            "#results > div:nth-child(1) > div.c-container > div.c-row.wa-fraudphone-topbtmsmall > div.c-span10.wa-fraudphone-middle > span"
            ,1
    );
}
