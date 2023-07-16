## RSS Specifications

This file contains the specifications of RSS version like tags, namespaces, attributes and more.

### Elements

- rss
  version = 2.0
    - channel
        - **description** ⚡ (character data)
        - **link** ⚡ (URL)
        - **title** ⚡ (character data)
        - category+ (slash-delimited string) [ref][1]
          domain = ""
        - cloud [ref][2]
        - **copyright** (human-readable copyright statement)
        - docs (URL - spec)
        - generator (credits software that created feed)
        - **image** (graphical logo)
            - **link** ⚡ (URL - website)
            - **title** ⚡ (character data)
            - **url** ⚡ (URL - image /GIF/JPEG/PNG)
            - **description** (character data)
            - **height** (pixels < 400 default 31)
            - **width** (pixels < 144 default 88)
        - **language** [RSS language codes][3], [w3c language codes][4], [ISO 639][5]
        - **lastBuildDate** (date and time)
        - **managingEditor** (email address of person contact)
        - pubDate (date and time)
        - rating [ref][6]
        - skipDays (days of week during feed is not updated)
            - day+7 ⚡ [weekday in DMT](#gmt-days)
        - skipHours (hour of day during feed is not updated)
            - hour+24 ⚡ (hour of day in DMT 0-24)
        - textInput [refer][8]
        - ttl (number of minutes feed can be cached)
        - webMaster (e-mail address of person- technical issues)
        - **item+**
            - **author** (e-mail - wrote article)
            - **category+**
            - comments (URL - comments received for this item)
            - **description** (HTML as escaped or CDATA)
            - **enclosure** (associates a media object)
                - **length** ⚡ (size of file in bytes)
                - **type** ⚡ [MIME media types][9]
                - **url** ⚡ (URL)
            - guid [uniquely identifies this item][10]
            - **link** (URL)
            - **pubDate** (date and time)
            - source (republished from other feed)
            - **title** (character data)

## Symbols

1. ⚡ - required
2. "+" - zero or more

### GMT days

Monday, Tuesday, Wednesday, Thursday, Friday, Saturday or Sunday

[0]: https://www.rssboard.org/rss-draft-1

[1]: https://www.rssboard.org/rss-draft-1#element-channel-category

[2]: https://www.rssboard.org/rss-draft-1#element-channel-cloud

[3]: https://www.rssboard.org/rss-language-codes

[4]: http://www.w3.org/TR/REC-html40/struct/dirlang.html#langcodes

[5]: http://www.loc.gov/standards/iso639-2/

[6]: https://www.rssboard.org/rss-draft-1#element-channel-rating

[8]: https://www.rssboard.org/rss-draft-1#element-channel-textinput

[9]: http://www.iana.org/assignments/media-types/

[10]: https://www.rssboard.org/rss-draft-1#element-channel-item-guid
