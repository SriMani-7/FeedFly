package srimani7.apps.feedfly.audio

data class AudioMetaData(
    val title: CharSequence,
    val artist: CharSequence,
    val artworkWork: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AudioMetaData

        if (title != other.title) return false
        if (artist != other.artist) return false
        if (artworkWork != null) {
            if (other.artworkWork == null) return false
            if (!artworkWork.contentEquals(other.artworkWork)) return false
        } else if (other.artworkWork != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + (artworkWork?.contentHashCode() ?: 0)
        return result
    }
}