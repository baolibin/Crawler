package libin.startCrawl

import java.io.File

import libin.parse.ParseTencentYYB
import libin.store.StoreUtils
import libin.utils.PageUtils
import org.joda.time.DateTime

import scala.collection.mutable

/**
  * Created by baolibin on 2017/10/15.
  */
object TencentYYB {

  val softwareGame = false //true表示抓取软件内容,false表示抓取游戏内容

  //开始下载的初始地址
  val initPathSoftware = "http://android.myapp.com/myapp/category.htm?orgame=1"
  val initPathGame = "http://android.myapp.com/myapp/category.htm?orgame=2"
  var urlLevel1 = ""
  var outRootPath = ""
  var CLASSLEVEL1 = ""

  val classificationMap = new mutable.HashMap[String, String]() //目录类别

  def main(args: Array[String]): Unit = {
    //初始化一些信息
    initClass()
    //开始爬取
    startCrawler()
  }

  /**
    * 开始爬取腾讯应用宝的信息
    */
  def startCrawler(): Unit = {
    /**
      * Start crawling
      */
    //下载每一个一级URL
    val mapLevel: mutable.HashMap[String, String] = ParseTencentYYB.getTitleUrlLevel1(urlLevel1, classificationMap)
    println(urlLevel1)
    println("一级标题URL个数:" + mapLevel.size)
    mapLevel.foreach(println)
    println()
    //下载每一个二级URL
    for ((k1, v1) <- mapLevel) {
      println("===================================== " + k1 + " 开始爬啦! ================================================")
      val file = new File(outRootPath)
      if (!file.exists()) {
        file.mkdirs()
      }
      val writer = StoreUtils.getWriter(outRootPath + classificationMap(k1) + ".txt")
      val errorUrl = StoreUtils.getWriter(outRootPath + classificationMap(k1) + "_errorUrl.txt")
      println(classificationMap(k1) + ".txt")
      val mapLevel2: mutable.HashSet[String] = ParseTencentYYB.getTitleUrlLevel2(v1)
      mapLevel2.foreach(println)
      println()
      //根据二级URL,爬取每个页面的内容
      var count: Int = 0
      val length: Int = mapLevel2.size
      for (v2 <- mapLevel2) {
        count += 1
        val pageInfo: String = ParseTencentYYB.parseYYB(PageUtils.getHttpClientContent(v2), v2, CLASSLEVEL1)
        //if (!"error".equals(pageInfo)) {
        val title = pageInfo.split("\001")(1)
        val url = v2
        if (!pageInfo.contains("Connection reset")) {
          writer.println(pageInfo)
          println("=====================================" + title + " 已经爬完啦! ================================================")
          println("正在爬取" + title + "应用App的数据," + k1 + "分类已经爬完" + count + "条记录啦,一共" + length + "个App数据!")
        } else {
          errorUrl.println(url)
          println("=====================================" + title + " 爬取失败,已放进失败库! ================================================")
          println("正在爬取" + title + "应用App的数据,爬取失败," + k1 + "分类已经爬完" + count + "条记录啦,一共" + length + "个App数据!")
        }
        println()
        //} else {
        //  println("爬取失败：" + v2)
        //  errorUrl.println(v2)
        //}
        Thread.sleep(2000)
      }
      println("===================================== " + k1 + " 已经爬完啦!" + " =====================================")
      println()
      Thread.sleep(5000)
      StoreUtils.closeWriter(writer)
      StoreUtils.closeWriter(errorUrl)
    }
  }

  def initClass(): Unit = {
    val dateTime = new DateTime().minusDays(0).toString("yyyyMMdd")
    if (softwareGame) {
      urlLevel1 = "http://android.myapp.com/myapp/category.htm" //软件的地址
      outRootPath = "/home/baolibin/spider/crawler/crawlerData/tencentYYB/date=" + dateTime + "/software/" //软件输出目录
      CLASSLEVEL1 = "软件"
      classificationMap += ("url" -> "url")
      classificationMap += ("音乐" -> "music")
      classificationMap += ("安全" -> "security")
      classificationMap += ("健康" -> "health")
      classificationMap += ("视频" -> "video")
      classificationMap += ("社交" -> "socialization")
      classificationMap += ("儿童" -> "children")
      classificationMap += ("工具" -> "tools")
      classificationMap += ("摄影" -> "photography")
      classificationMap += ("教育" -> "education")
      classificationMap += ("通讯" -> "communication")
      classificationMap += ("旅游" -> "tourism")
      classificationMap += ("新闻" -> "news")
      classificationMap += ("出行" -> "travel")
      classificationMap += ("理财" -> "MoneyManagement")
      classificationMap += ("系统" -> "systems")
      classificationMap += ("生活" -> "life")
      classificationMap += ("美化" -> "beautify")
      classificationMap += ("购物" -> "shopping")
      classificationMap += ("阅读" -> "read")
      classificationMap += ("办公" -> "office")
      classificationMap += ("娱乐" -> "entertainment")
    } else {
      urlLevel1 = "http://android.myapp.com/myapp/category.htm?orgame=2" //游戏地址
      outRootPath = "/home/baolibin/spider/crawler/crawlerData/tencentYYB/date=" + dateTime + "/game/" //游戏输出目录
      CLASSLEVEL1 = "游戏"
      classificationMap += ("休闲益智" -> "leisurePuzzle")
      classificationMap += ("网络游戏" -> "networkGame")
      classificationMap += ("飞行射击" -> "flightShooting")
      classificationMap += ("动作冒险" -> "actionAdventure")
      classificationMap += ("体育竞速" -> "sportsRacing")
      classificationMap += ("棋牌中心" -> "chessCenter")
      classificationMap += ("经营策略" -> "businessStrategy")
      classificationMap += ("角色扮演" -> "cosplay")
    }
  }
}
