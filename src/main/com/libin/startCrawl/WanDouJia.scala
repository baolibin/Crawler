package libin.startCrawl

import java.util

import libin.download.DownloadInfo
import libin.parse.ParseWanDouJia
import libin.utils.PageUtils
import scala.collection.mutable

/**
  * Created by baolibin on 2017/10/15.
  * 爬取豌豆荚数据
  * 爬取分2大类：软件和游戏
  * 爬取页面地址：
  * http://www.wandoujia.com/category/app
  * http://www.wandoujia.com/category/game
  */
object WanDouJia {
  //一个App的具体页面
  val url = "http://www.wandoujia.com/apps/com.qiyi.video"

  def main(args: Array[String]) {
    //下载豌豆荚页面
    val wanDouJiaPage: String = DownloadInfo.downloadWanDouJia(url)
    //println(wanDouJiaPage)
    //解析豌豆荚页面
    ParseWanDouJia.parseWanDouJia(wanDouJiaPage)

    /*val doc: Document = Jsoup.parse(wanDouJiaPage)
    val contents: Elements = doc.select("div.tag-box")
    println(contents.size())
    val it = contents.iterator()
    while (it.hasNext) {
      val element: Element = it.next
      val titleNode: Node = element.child(0).childNode(0)
      println(titleNode.attr("a"))
    }*/
    val set = new mutable.HashSet[String]()
    val linkRegex =
      """ <a href="http://www.wandoujia.com/tag/([0-9]*)">(\D*)</a> """.trim.r
    for (m <- linkRegex.findAllIn(PageUtils.httpUrlSpider(url)).matchData) {
      set += m.group(2).trim
    }
    println(set.mkString("|"))



  }
}
