package org.xdty.phone.number.model.web

import org.xdty.phone.number.model.INumber
import org.xdty.phone.number.model.Type

class WebNumber(private var number: String = "") : INumber {

    private var name: String = ""
    private var province: String = ""
    private var city: String = ""
    private var type: Type = Type.NORMAL
    private var count: Int = 0

    override fun getName(): String? = name

    override fun getProvince(): String = province

    override fun getType(): Type = type

    override fun getCity(): String = city

    override fun getNumber(): String = number

    override fun getProvider(): String = ""

    override fun getCount(): Int = count

    override fun isValid(): Boolean {
        return true
    }

    override fun isOnline(): Boolean = true

    override fun hasGeo(): Boolean {
        return true
    }

    override fun getApiId(): Int = INumber.API_ID_WEB

    override fun patch(i: INumber) {

    }

    fun setName(name: String) {
        this.name = name
    }

    fun setNumber(number: String) {
        this.number = number
    }

    fun setProvince(province: String) {
        this.province = province
    }

    fun setCity(city: String) {
        this.city = city
    }

    fun setType(type: Type) {
        this.type = type
    }

    fun setCount(count: Int) {
        this.count = count
    }

    override fun toString(): String {
        return javaClass.simpleName + "{" + getNumber() + ", " + getName() + ", " + getCount() + '}'.toString()
    }
}
