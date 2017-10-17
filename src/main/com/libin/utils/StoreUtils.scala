package libin.utils

import java.io.{File, PrintWriter}

/**
  * Created by baolibin on 17-10-17.
  */
object StoreUtils {
  /**
    * 返回一个PrintWriter对象
    */
  def getWriter(title:String):PrintWriter={
    val writer = new PrintWriter(new File(title))
    writer
  }

  /**
    * 关闭一个PrintWriter对象
    */

  def closeWriter(writer:PrintWriter): Unit ={
    writer.close()
  }

}
