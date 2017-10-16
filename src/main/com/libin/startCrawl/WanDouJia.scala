package libin.startCrawl

import libin.download.DownloadInfo
import libin.parse.ParseWanDouJia

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

  }

}
