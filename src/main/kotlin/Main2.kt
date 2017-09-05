import org.jsoup.Jsoup
import java.util.concurrent.TimeUnit

/**
 * Created by darylsze on 5/9/2017.
 */

class Psp {
    val chinese = 2
    val english = 1

    fun doOneDistrict(districtId: Int, language : Int) {


        val doc = Jsoup.connect("http://www.chsc.hk/psp2017/sch_list.php?district_id=$districtId&lang_id=$language&frmMode=pagebreak").get()
        val links = doc.select("body > div.table-con > div > table > tbody > tr > td.tdtl.td-bgc > a").map { it.absUrl("href") }

        links.forEach { link ->

            getASchool(link)

            /**
             * add 1 second delay to prevent 403
             */
            TimeUnit.SECONDS.sleep(1)
        }

        /**
         * add 1 second delay to prevent 403
         */
        TimeUnit.SECONDS.sleep(1)
    }

    /**
     * Use Jsoup's selector to grasp school info
     */
    fun getASchool(link: String) {
        val doc = Jsoup.connect(link).get()

        val email = doc.select("body > div.xxzl-info > dl > dd.xxzl-info-dz.c > table > tbody > tr:nth-child(2) > td:nth-child(5)").first().text()
        val name = doc.select("body > div.xxzl-info > dl > dd.xxzl-info-tit").first().text().split(" ")[0]
        val englishName = doc.select("body > div.xxzl-info > dl > dd.xxzl-info-tit").first().html().split("<br>")[1]
        val tel = doc.select("body > div.xxzl-info > dl > dd.xxzl-info-dz.c > table > tbody > tr:nth-child(2) > td:nth-child(2)").first().text()
        val fax = doc.select("body > div.xxzl-info > dl > dd.xxzl-info-dz.c > table > tbody > tr:nth-child(3) > td:nth-child(2)").first().text()
        val website = doc.select("body > div.xxzl-info > dl > dd.xxzl-info-dz.c > table > tbody > tr:nth-child(3) > td:nth-child(5)").first().text()
        val principal = doc.select("body > div.xxzl-con > dl > dd.xmcslr01 > div > table > tbody > tr:nth-child(2) > td:nth-child(3)").first().text()
        val address = doc.select("body > div.xxzl-info > dl > dd.xxzl-info-dz.c > table > tbody > tr:nth-child(1) > td:nth-child(2)").first().text()

        println("$address | $email | $name | $englishName | $tel | $fax | $website | $principal")
    }

}
fun main(args: Array<String>) {

    val pspService = Psp()

    // chinese
    (1..18).forEach { districtId ->
        print("========= doing district $districtId in chinese =========\n")

        /**
         * get one district each time.
         */
        pspService.doOneDistrict(districtId, pspService.chinese)

        /**
         * add 1 second delay to prevent 403
         */
        TimeUnit.SECONDS.sleep(1)
    }

    // english
    (1..18).forEach { districtId ->
        println("========= doing district $districtId in english =========\n")

        /**
         * get one district each time.
         */
        pspService.doOneDistrict(districtId, pspService.english)

        /**
         * add 1 second delay to prevent 403
         */
        TimeUnit.SECONDS.sleep(1)
    }
}

