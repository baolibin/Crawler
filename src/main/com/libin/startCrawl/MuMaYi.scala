package libin.startCrawl

import libin.download.DownloadInfo
import libin.utils.{PageUtils, HtmlUtils}
import org.apache.commons.lang3.StringUtils
import scala.collection.mutable

/**
  * Created by baolibin on 2017/10/15.
  * 下载木蚂蚁所有的App的URL
  * 软件应用入口：http://www.mumayi.com/android/xitonggongju/
  * 游戏应用入口：http://www.mumayi.com/android/juesebanyan
  */
object MuMaYi {
  def main(args: Array[String]): Unit = {
    val titleUrl: mutable.HashMap[String, String] = getTitleUrl("http://www.mumayi.com/android/xitonggongju/")
    println(titleUrl.mkString("\n"))
  }

  /**
    * 下载一级标题
    */
  def getTitleUrl(url: String): mutable.HashMap[String, String] = {
    val titleUrlLevel1: mutable.HashMap[String, String] = new mutable.HashMap[String, String]()
    val rootPath = "http://www.mumayi.com/"
    val content: String = PageUtils.httpUrlSpider(url)
    println(content)
    //<li><a href="/android/xitonggongju/" title="系统工具" class="current" ><em>系统工具<i>(5643)</i></em></a></li>
    val linkRegex =
      """ <li><a href="([a-zA-Z/]*)" title="(\D*)" """.trim.r
    //获取有效的分类名和URL地址
    for (m <- linkRegex.findAllIn(content).matchData) {
      titleUrlLevel1 += m.group(2).trim.split("\\s+")(0) -> (rootPath + m.group(1).trim)
    }
    titleUrlLevel1
  }

}
