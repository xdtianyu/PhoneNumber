package org.xdty.phone.number.model.web

import android.util.Log
import org.jsoup.Jsoup
import org.xdty.phone.number.model.INumber
import org.xdty.phone.number.model.NumberHandler

class WebNumberHandler(private val mWebConfig: WebConfig) : NumberHandler<WebNumber> {

    private val tag = "WebNumberHandler"

    override fun url(): String {
        return mWebConfig.url
    }

    override fun key(): String? {
        return null
    }

    override fun find(n: String): WebNumber? {

        return fetchDAta(mWebConfig, n)
    }

    override fun isOnline(): Boolean {
        return true
    }

    override fun getApiId(): Int {
        return INumber.API_ID_WEB
    }

    private fun fetchDAta(config: WebConfig, num: String): WebNumber? {

        val webNumber = WebNumber(num)

        val doc = Jsoup.connect(config.url + num)
                .userAgent(config.ua)
                .get()

        Log.d(tag, doc.title())

        val elements = doc.select(config.root)
        for (element in elements) {
            Log.d(tag, "%s\n\t%s".format(element.text(), ""))
        }

        val number = doc.select(config.number)

        if (number.size == 1) {
            Log.d(tag, "number: %s\n\t".format(number[0].text()))
            webNumber.setNumber(number[0].text())
        }

        val mark = doc.select(config.mark)

        if (mark.size == 1) {
            Log.d(tag, "mark: %s\n\t".format(mark[0].text()))
            webNumber.setName(mark[0].text())
        }

        val count = doc.select(config.count)

        if (count.size == 1) {
            val c = count[0].text().replace("[^.0123456789]".toRegex(), "")
            Log.d(tag, "count: %s\n\t".format(c))
            webNumber.count = c.toInt()
        }

        val name = doc.select(config.name)

        if (name.size == 1) {
            Log.d(tag, "name: %s\n\t".format(name[0].text()))
            webNumber.setName(name[0].text())
        }

        val address = doc.select(config.address)

        if (address.size == 1) {
            Log.d(tag, "address: %s\n\t".format(address[0].text()))
            webNumber.setProvince(address[0].text())
        }

        return webNumber
    }
}
