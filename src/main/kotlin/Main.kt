import org.jsoup.Jsoup
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by darylsze on 12/5/2017.
 */
fun main(args: Array<String>) {

    (1..18).forEach { districtId ->
        doADistrict(districtId)
        TimeUnit.SECONDS.sleep(1)
    }
}

fun doADistrict(districtId: Int) {
    val totalPage = getTotalPage(districtId = districtId)

    (1..totalPage).forEach { page ->
        val doc = Jsoup.connect("http://applications.chsc.hk/ssp2016/m/sch_list.php?lang_id=2&frmMode=pagebreak&district_id=$districtId&page=$page").get()
        val links = doc.select("#main_content > table > tbody > tr:nth-child(6) > td > table > tbody > tr > td:nth-child(2) > a").map { it.absUrl("href") }

        links.forEach { link ->
            getASchool(link)
            TimeUnit.SECONDS.sleep(1)
        }

        /**
         * add 1 second delay to prevent 403
         */
        TimeUnit.SECONDS.sleep(1)
    }
}


fun getASchool(link: String) {
    val doc = Jsoup.connect(link).get()

    val email = doc.select("#main_content > table > tbody > tr:nth-child(6) > td > table:nth-child(3) > tbody > tr:nth-child(5) > td:nth-child(2)").first().text()
    val name = doc.select("#main_content > table > tbody > tr:nth-child(6) > td > table:nth-child(3) > tbody > tr:nth-child(1) > td").first().text()
    val englishName = doc.select("#main_content > table > tbody > tr:nth-child(6) > td > table:nth-child(3) > tbody > tr:nth-child(2) > td").first().text()
    val tel = doc.select("#main_content > table > tbody > tr:nth-child(6) > td > table:nth-child(3) > tbody > tr:nth-child(4) > td:nth-child(2)").first().text()
    val fax = doc.select("#main_content > table > tbody > tr:nth-child(6) > td > table:nth-child(3) > tbody > tr:nth-child(6) > td:nth-child(2)").first().text()
    val website = doc.select("#main_content > table > tbody > tr:nth-child(6) > td > table:nth-child(3) > tbody > tr:nth-child(7) > td:nth-child(2) > a > span").first().text()
    val principal = doc.select("#main_content > table > tbody > tr:nth-child(6) > td > table:nth-child(5) > tbody > tr:nth-child(8) > td:nth-child(2)").first().text()
    val address = doc.select("#main_content > table > tbody > tr:nth-child(6) > td > table:nth-child(3) > tbody > tr:nth-child(3) > td:nth-child(2)").first().text()

    println("$address | $email | $name | $englishName | $tel | $fax | $website | $principal")
}

fun getTotalPage(districtId: Int): Int {
    val doc = Jsoup
            .connect("http://applications.chsc.hk/ssp2016/m/sch_list.php?lang_id=2&frmMode=pagebreak&district_id=$districtId")
            .get()

    val numStr = doc.select("#main_content > table > tbody > tr:nth-child(6) > td > table > tbody > tr:nth-child(1) > td").first().text()

    if (Regex("(\\d+)").find(numStr)?.groups?.get(1)?.value == null) {
        println("!!! Regex().find($numStr)?.groups?.get(1)?.value is null!!!")
        return 1
    }
    val num: Int = Regex("(\\d+)").find(numStr)?.groups?.get(1)?.value!!.toInt()

    val page: Int = Math.round(num / 20.toFloat())

    println("district id : $districtId has page: $page")

    return page
}