package com.ithoughts.mynaa.tsd.rss

class Rss(val list: List<Channel>) : XmlElement() {
    class Channel : XmlElement() {
        var title: String? = null
        var link: String? = null
        var description: String? = null
        var lastBuildDate: String? = null
        var list: MutableList<Item> = mutableListOf()

        class Item(var title: String, var link: String, var category: String, var guid: String) :
            XmlElement() {

            constructor() : this("", "", "", "")

            override fun set(prop: String, value: String) {
                when (prop) {
                    "title" -> title = value
                    "link" -> link = value
                    "category" -> category = value
                    "guid" -> guid = value
                }
            }
        }

        override fun set(prop: String, value: String) {
            when (prop) {
                "title" -> title = value
                "link" -> link = value
                "description" -> description = value
            }
        }
    }

    override fun set(prop: String, value: String) {}
}