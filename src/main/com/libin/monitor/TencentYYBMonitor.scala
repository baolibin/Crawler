package libin.monitor

import java.io.File

import libin.download.DownloadInfo
import libin.parse.ParseTencentYYB
import libin.store.StoreUtils

import scala.collection.mutable
import scala.io.Source

/**
  * Created by baolibin on 17-10-18.
  * 爬取腾讯应用宝失败的URL
  */
object TencentYYBMonitor {
  val softwareGame = false //true表示抓取软件内容,false表示抓取游戏内容

  /**
    * 重新下载解析失败的URL
    * path3：失败的三级URL本地地址
    * path4:失败的App的URL的本地地址
    * savePath:爬取失败URL的保存的本地路径
    */
  def retryParseFailedUrl(path3: String, path4: String,savePath:String): Unit = {
    //所有失败App的url地址
    val setUrlAll = new mutable.HashSet[String]()
    //获取三级失败URL,比如：http://www.wandoujia.com/category/6007_694
    if(path3!= ""){
      val setUrlLevel3 = getFailedUrlLevel3(path3)
      for (url3 <- setUrlLevel3) {
        //解析每个三级分类里面的所有App的url地址
        val dealSet = ParseTencentYYB.getTitleUrlLevel2(url3).toSet
        setUrlAll ++= dealSet
      }
    }
    if(path4!= ""){
      //掉用getFailedUrlLevel4
      setUrlAll ++= getFailedUrlLevel4(path4)
    }

    var outRootPath = ""
    if (softwareGame) {
      outRootPath = savePath + "/software/"
      val file = new File(outRootPath)
      if (!file.exists()) {
        file.mkdirs()
      }
    } else {
      outRootPath = savePath + "/game/"
      val file = new File(outRootPath)
      if (!file.exists()) {
        file.mkdirs()
      }
    }
    val writerSuccess = StoreUtils.getWriter(outRootPath+ "success.txt")
    val writerFailed = StoreUtils.getWriter(outRootPath+ "failed.txt")
    //下载失败App应用的内容
    for(url <- setUrlAll){
      val sb = ParseTencentYYB.parseYYB(DownloadInfo.downloadWanDouJia(url),url,if(softwareGame)"软件" else "应用")
      if("error".equals(sb)){
        writerFailed.println(url)
      }else{
        writerSuccess.println(sb)
      }
    }
    StoreUtils.closeWriter(writerSuccess)
    StoreUtils.closeWriter(writerFailed)
  }

  /**
    * 获取全部失败的三级分类URL
    * input：本地的三级url文件地址
    * output：所有三级url集合
    */
  def getFailedUrlLevel3(path: String): mutable.HashSet[String] = {
    val set = new mutable.HashSet[String]
    val localFile = Source.fromFile(path)
    for (line <- localFile.getLines()) {
      //println(line)
      set += line.split(":")(1).trim
    }
    localFile.close()
    set
  }

  /**
    * 获取所有失败的App的url
    * input：本地的失败的App的url地址
    */
  def getFailedUrlLevel4(path: String): mutable.HashSet[String] = {
    val set = new mutable.HashSet[String]
    val localFile = Source.fromFile(path)
    for (line <- localFile.getLines()) {
      //println(line)
      //例如:具体App页面内容下载失败:http://www.wandoujia.com/apps/com.fgol.wdj
      set += line.split(":")(1).trim
    }
    localFile.close()
    set
  }
}
