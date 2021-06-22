package com.jz.testapplication

/**
 * @Author:         tdn
 * @Time            2021/2/8
 * @Description:
 */
fun main() {
    val component = Component()
    component.print("hahhah")
}

class Component {
    private val str: String by lazyString()

    private val lazyString:String by lazy { "系统自带" }

    fun print(str:String){
        println(str)
        println(lazyString)
    }
}


class LazyString : Lazy<String> {
    private var cached: String? = null

    override val value: String
        get() {
            return cached ?: "我是第一次加载出现的属性"
        }

    override fun isInitialized() = cached != null
}

fun Component.lazyString(): Lazy<String> {
    return LazyString()
}