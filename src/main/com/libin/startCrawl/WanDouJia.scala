package libin.startCrawl

import libin.download.DownloadInfo
import libin.parse.ParseWanDouJia
import libin.utils.HtmlUtils
import org.htmlcleaner.{TagNode, HtmlCleaner}

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
    println(wanDouJiaPage)
    //解析豌豆荚页面
    val htmlCleaner: HtmlCleaner = new HtmlCleaner
    val rootNode: TagNode = htmlCleaner.clean(wanDouJiaPage)
    println("为什么是空：" + HtmlUtils.getText(rootNode, "/html/body[@class='detail PC ']/div[@class='container']/div[@class='detail-wrap ']/div[@class='detail-top clearfix']/div[@class='app-info']/p[@class='app-name']/span[@class='title']"))
    //ParseWanDouJia.parseWanDouJia(wanDouJiaPage)
  }

}
