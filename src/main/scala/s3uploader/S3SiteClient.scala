package s3uploader

import awscala._, s3._
import com.typesafe.config.ConfigFactory
import java.io.File

trait S3SiteClient {
  def filesWithMd5(): List[(String, String)]
  def uploadFile(key: String, file: File)
}


object S3SiteClientImpl extends S3SiteClient{
  val config = ConfigFactory.load()
  implicit val s3 = S3(Credentials(config.getString("aws.accessKeyId"),config.getString("aws.secretKey")))
  val siteBucket = s3.bucket(config.getString("bucket"))
  def filesWithMd5(): List[(String, String)] = {
    siteBucket.get.objectSummaries().toList.filter(!_.getKey.endsWith("/")).map(x => (x.getKey, x.getETag()))    
  }

  def uploadFile(key: String, file: File) {
    siteBucket.get.putAsPublicRead(key, file)
  }

}
