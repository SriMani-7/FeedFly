package srimani7.apps.rssparser

import java.text.DateFormat
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * A helper class that parses Dates out of Strings with date time in RFC822 and W3CDateTime formats
 * plus the variants Atom (0.3) and RSS (0.9, 0.91, 0.92, 0.93, 0.94, 1.0 and 2.0) specificators
 * added to those formats.
 *
 *
 * It uses the JDK java.text.SimpleDateFormat class attemtping the parse using a mask for each one
 * of the possible formats.
 *
 *
 */
object DateParser {

    // order is like this because the SimpleDateFormat.parse does not fail with exception if it can
    // parse a valid date out of a substring of the full string given the mask so we have to check
    // the most complete format first, then it fails with exception
    private val RFC822_MASKS = arrayOf(
        "EEE, dd MMM yy HH:mm:ss z",
        "EEE, dd MMM yy HH:mm z",
        "dd MMM yy HH:mm:ss z",
        "dd MMM yy HH:mm z"
    )

    // order is like this because the SimpleDateFormat.parse does not fail with exception if it can
    // parse a valid date out of a substring of the full string given the mask so we have to check
    // the most complete format first, then it fails with exception together with logic in the parseW3CDateTime they handle W3C dates without time forcing them to be GMT
    private val W3CDATETIME_MASKS = arrayOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSz",
        "yyyy-MM-dd't'HH:mm:ss.SSSz",
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd't'HH:mm:ss.SSS'z'",
        "yyyy-MM-dd'T'HH:mm:ssz",
        "yyyy-MM-dd't'HH:mm:ssz",
        "yyyy-MM-dd'T'HH:mm:ssZ",
        "yyyy-MM-dd't'HH:mm:ssZ",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd't'HH:mm:ss'z'",
        "yyyy-MM-dd'T'HH:mmz",
        "yyyy-MM'T'HH:mmz",
        "yyyy'T'HH:mmz",
        "yyyy-MM-dd't'HH:mmz",
        "yyyy-MM-dd'T'HH:mm'Z'",
        "yyyy-MM-dd't'HH:mm'z'",
        "yyyy-MM-dd",
        "yyyy-MM",
        "yyyy"
    )

    /**
     * The masks used to validate and parse the input to this Atom date. These are a lot more
     * forgiving than what the Atom spec allows. The forms that are invalid according to the spec
     * are indicated.
     */
    private val masks = arrayOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSz", "yyyy-MM-dd't'HH:mm:ss.SSSz",  // invalid
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd't'HH:mm:ss.SSS'z'",  // invalid
        "yyyy-MM-dd'T'HH:mm:ssz", "yyyy-MM-dd't'HH:mm:ssz",  // invalid
        "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd't'HH:mm:ss'z'",  // invalid
        "yyyy-MM-dd'T'HH:mmz",  // invalid
        "yyyy-MM-dd't'HH:mmz",  // invalid
        "yyyy-MM-dd'T'HH:mm'Z'",  // invalid
        "yyyy-MM-dd't'HH:mm'z'",  // invalid
        "yyyy-MM-dd", "yyyy-MM", "yyyy"
    )

    /**
     * Parses a Date out of a string using an array of masks.
     *
     *
     * It uses the masks in order until one of them succedes or all fail.
     *
     *
     *
     * @param masks array of masks to use for parsing the string
     * @param sDate string to parse for a date.
     * @return the Date represented by the given string using one of the given masks. It returns
     * **null** if it was not possible to parse the the string with any of the masks.
     */
    private fun parseUsingMask(masks: Array<String>, sDate: String, locale: Locale): Date? {
        val sDate1: String = sDate.trim { it <= ' ' }
        var parsePosition: ParsePosition?
        var date: Date? = null
        var i = 0
        while (date == null && i < masks.size) {
            val format: DateFormat = SimpleDateFormat(masks[i].trim { it <= ' ' }, locale)
            // df.setLenient(false);
            format.isLenient = true
            try {
                parsePosition = ParsePosition(0)
                date = format.parse(sDate1, parsePosition)
                if (parsePosition.index != sDate1.length) date = null
            } catch (ex1: java.lang.Exception) {
                ex1.printStackTrace()
            }
            i++
        }
        return date
    }

    /**
     * Parses a Date out of a String with a date in RFC822 format.
     *
     *
     * It parsers the following formats:
     *
     *  * "EEE, dd MMM yyyy HH:mm:ss z"
     *  * "EEE, dd MMM yyyy HH:mm z"
     *  * "EEE, dd MMM yy HH:mm:ss z"
     *  * "EEE, dd MMM yy HH:mm z"
     *  * "dd MMM yyyy HH:mm:ss z"
     *  * "dd MMM yyyy HH:mm z"
     *  * "dd MMM yy HH:mm:ss z"
     *  * "dd MMM yy HH:mm z"
     *
     *
     *
     * Refer to the java.text.SimpleDateFormat javadocs for details on the format of each element.
     *
     *
     *
     * @param sDate string to parse for a date.
     * @return the Date represented by the given RFC822 string. It returns **null** if it was not
     * possible to parse the given string into a Date.
     */
    private fun parseRFC822(sDate: String, locale: Locale): Date? {
        var sDate1 = sDate
        sDate1 = convertUnsupportedTimeZones(sDate1)
        return parseUsingMask(
            RFC822_MASKS,
            sDate1,
            locale
        )
    }

    private fun convertUnsupportedTimeZones(sDate: String): String {
        val unsupportedZeroOffsetTimeZones = listOf("UT", "Z")
        val splitted = listOf(*sDate.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray())
        for (timeZone in unsupportedZeroOffsetTimeZones) {
            if (splitted.contains(timeZone)) {
                return replaceLastOccurrence(
                    sDate,
                    timeZone,
                    "UTC"
                )
            }
        }
        return sDate
    }

    private fun replaceLastOccurrence(
        original: String,
        target: String,
        replacement: String
    ): String {
        val lastIndexOfTarget = original.lastIndexOf(target)
        return if (lastIndexOfTarget == -1) {
            original
        } else {
            StringBuilder(original)
                .replace(lastIndexOfTarget, lastIndexOfTarget + target.length, replacement)
                .toString()
        }
    }

    /**
     * Parses a Date out of a String with a date in W3C date-time format.
     *
     *
     * It parsers the following formats:
     *
     *  * "yyyy-MM-dd'T'HH:mm:ssz"
     *  * "yyyy-MM-dd'T'HH:mmz"
     *  * "yyyy-MM-dd"
     *  * "yyyy-MM"
     *  * "yyyy"
     *
     *
     *
     * Refer to the java.text.SimpleDateFormat javadocs for details on the format of each element.
     *
     *
     *
     * @param sDate string to parse for a date.
     * @return the Date represented by the given W3C date-time string. It returns **null** if it
     * was not possible to parse the given string into a Date.
     */
    private fun parseW3CDateTime(sDate: String, locale: Locale): Date? {
        // if sDate has time on it, it injects 'GTM' before de TZ displacement to allow the
        // SimpleDateFormat parser to parse it properly
        var sDate = sDate
        val tIndex = sDate.indexOf("T")
        if (tIndex > -1) {
            if (sDate.endsWith("Z")) {
                sDate = sDate.substring(0, sDate.length - 1) + "+00:00"
            }
            var tzdIndex = sDate.indexOf("+", tIndex)
            if (tzdIndex == -1) {
                tzdIndex = sDate.indexOf("-", tIndex)
            }
            if (tzdIndex > -1) {
                var pre = sDate.substring(0, tzdIndex)
                val secFraction = pre.indexOf(",")
                if (secFraction > -1) {
                    pre = pre.substring(0, secFraction)
                }
                val post = sDate.substring(tzdIndex)
                sDate = pre + "GMT" + post
            }
        } else {
            sDate += "T00:00GMT"
        }
        return parseUsingMask(
            W3CDATETIME_MASKS,
            sDate,
            locale
        )
    }

    /**
     * Parses a Date out of a String with a date in W3C date-time format or in a RFC822 format.
     *
     *
     *
     * @param sDate string to parse for a date.
     * @return the Date represented by the given W3C date-time string. It returns **null** if it
     * was not possible to parse the given string into a Date.
     *
     */
    fun parseDate(sDate: String, locale: Locale = Locale.getDefault()): Date? {
        var date: Date? = parseW3CDateTime(sDate, locale)
        if (date == null) {
            date = parseRFC822(sDate, locale)
        }
        return date
    }

    /**
     * create a RFC822 representation of a date.
     *
     *
     * Refer to the java.text.SimpleDateFormat javadocs for details on the format of each element.
     *
     *
     *
     * @param date Date to parse
     * @return the RFC822 represented by the given Date It returns **null** if it was not
     * possible to parse the date.
     */
    fun formatRFC822(date: Date, locale: Locale?): String {
        val dateFormater = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", locale)
        dateFormater.timeZone = TimeZone.getTimeZone("GMT")
        return dateFormater.format(date)
    }

    /**
     * create a W3C Date Time representation of a date.
     *
     *
     * Refer to the java.text.SimpleDateFormat javadocs for details on the format of each element.
     *
     *
     *
     * @param date Date to parse
     * @return the W3C Date Time represented by the given Date It returns **null** if it was not
     * possible to parse the date.
     */
    fun formatW3CDateTime(date: Date, locale: Locale?): String {
        val dateFormater = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", locale)
        dateFormater.timeZone = TimeZone.getTimeZone("GMT")
        return dateFormater.format(date)
    }

    fun formatDate(date: Date?): String? {
        if (date == null) return null
        val currentDate = Date()
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        val currentDay = calendar.get(Calendar.DAY_OF_YEAR)

        calendar.time = date
        val inputDay = calendar.get(Calendar.DAY_OF_YEAR)

        val timeDifference = currentDate.time - date.time
        val hoursDifference = timeDifference / (1000 * 60 * 60)
        val minutesDifference = timeDifference / (1000 * 60)

        return when {
            hoursDifference <= 0 -> {
                when {
                    minutesDifference < 1 -> "Just now"
                    minutesDifference < 10 -> "$minutesDifference minutes ago"
                    else -> "$minutesDifference minutes ago"
                }
            }

            hoursDifference < 12 -> "$hoursDifference hours ago"
            currentDay - inputDay == 1 -> "Yesterday"
            currentDay == inputDay -> "Today"
            else -> DateFormat.getDateInstance().format(date)
        }
    }

    fun formatTime(date: Date?) : String? = date?.let {
        SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(it)
    }
}