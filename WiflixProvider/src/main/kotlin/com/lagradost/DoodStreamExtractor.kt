package com.lagradost
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.utils.ExtractorApi
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.getQualityFromName



open class DoodStreamExtractor : ExtractorApi() {
    override var name = "DoodStream"
    override var mainUrl = "https://doodstream.com"
    override val requiresReferer = false

    override fun getExtractorUrl(id: String): String {
        return "$mainUrl/e/$id"
    }

 override suspend fun getUrl(url: String, referer: String?): List<ExtractorLink>? {
    val response0 = app.get(url).text // html of DoodStream page to look for /pass_md5/...
    
    // find the video URL from the JavaScript code
    val videoUrlRegex = Regex("""dsplayer\.src\(\{type: "video/mp4",src: "(.+?)"\}\)""")
    val videoUrlMatch = videoUrlRegex.find(response0) ?: return null

    // get the actual video URL
    val trueUrl = videoUrlMatch.groupValues[1]

    val quality = Regex("\\d{3,4}p").find(response0.substringAfter("<title>").substringBefore("</title>"))?.groupValues?.get(0)
    return listOf(
        ExtractorLink(
            trueUrl,
            this.name,
            trueUrl,
            mainUrl,
            getQualityFromName(quality),
            false
        )
    )
}
