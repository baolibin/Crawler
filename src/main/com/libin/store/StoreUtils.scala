package libin.store

import java.io.{File, PrintWriter}

import org.joda.time.DateTime

/**
  * Created by baolibin on 17-10-17.
  */
object StoreUtils {
  /**
    * 返回一个PrintWriter对象
    */
  def getWriter(title: String): PrintWriter = {
    val writer = new PrintWriter(new File(title))
    writer
  }

  /**
    * 关闭一个PrintWriter对象
    */

  def closeWriter(writer: PrintWriter): Unit = {
    writer.close()
  }

  /**
    * 下载失败的三级分类地址页面以及App的页面会存入指定文件
    * 未用
    */
  def saveSuccessContent(content: String): Unit = {
    val dateTime = new DateTime()
    val datePath = dateTime.toString("yyyyMMdd")
    //获取PrintWriter对象
    val writer: PrintWriter = StoreUtils.getWriter("/home/baolibin/spider/crawler/crawlerData/WanDouJia/WanDouJia_" + datePath + ".txt")
    writer.println(content)
    //关闭PrintWriter对象
    StoreUtils.closeWriter(writer)
  }
}
