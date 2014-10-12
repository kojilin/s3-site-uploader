package s3uploader

import java.nio.file.Paths
import com.typesafe.config.ConfigFactory
/**
 *
 * Created by kojilin on 2014/10/12.
 */
object Uploader {
  def main(args: Array[String]){
    val config = ConfigFactory.load()
    val client = S3SiteClientImpl
    val root = Paths.get(config.getString("localKeyPath"))
    val result = new MD5Checker(root, client).check
    result.foreach(t => {
      println((if(t._2) "add new file" else "update exist file") + ">>>" + t._1)
      client.uploadFile(t._1.toString, root.resolve(t._1).toFile.getAbsoluteFile)
    })
  }
}
