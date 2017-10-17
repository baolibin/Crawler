package libin.monitor

import java.io.{File, PrintWriter}

import org.joda.time.DateTime

/**
  * Created by baolibin on 17-10-17.
  */
object WanDouJiaMonitor {
  /**
    * 下载失败的三级分类地址页面以及App的页面会存入指定文件
    */
  def saveFaildUrl(failedUrl: String): Unit = {
    val dateTime = new DateTime()
    val datePath = dateTime.toString("yyyyMMdd")
    val writer = new PrintWriter(new File("/home/baolibin/spider/crawler/crawlerData/WanDouJia/WanDouJia_" + datePath + ".txt"))
    writer.println(failedUrl)
    writer.close()
  }
}
