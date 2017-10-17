package libin.store

import java.io.{File, PrintWriter}

import libin.utils.StoreUtils
import org.joda.time.DateTime

/**
  * Created by Administrator on 2017/10/16.
  */
object WanDouJia {
  /**
    * 下载失败的三级分类地址页面以及App的页面会存入指定文件
    */
  def saveSuccessContent(content: String): Unit = {
    val dateTime = new DateTime()
    val datePath = dateTime.toString("yyyyMMdd")
    //获取PrintWriter对象
    val writer:PrintWriter = StoreUtils.getWriter("/home/baolibin/spider/crawler/crawlerData/WanDouJia/WanDouJia_" + datePath + ".txt")
    writer.println(content)
    //关闭PrintWriter对象
    StoreUtils.closeWriter(writer)
  }
}
