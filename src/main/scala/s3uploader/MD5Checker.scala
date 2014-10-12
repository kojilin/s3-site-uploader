package s3uploader

import java.nio.file.DirectoryStream.Filter
import java.nio.file.{Files, LinkOption, Path, Paths}

import com.typesafe.scalalogging.StrictLogging

import scala.collection.immutable.List
import scala.collection.JavaConversions._
import java.security.MessageDigest


class MD5Checker(val root: Path, val s3SiteClient: S3SiteClient) extends StrictLogging {

  lazy val md5 = MessageDigest.getInstance("MD5")

  lazy val s3Files = s3SiteClient.filesWithMd5.map(t => (Paths.get(t._1), t._2)).toMap

  def check():List[(Path, Boolean)] = check(Paths.get(""))

  def check(keyPath: Path): List[(Path, Boolean)] = {
    val localFiles = filesWithMd5(keyPath).filter(t => !Files.isHidden(t._1) && !t._1.toString.endsWith(".less"))
    val newFiles = localFiles filter(f => !s3Files.contains(f._1)) map(t => (t._1,  true))
    val updateFiles = localFiles filter(f => s3Files.get(f._1).filter(md5 => f._2 != md5).isDefined) map (t=>(t._1, false))
    newFiles ::: listFiles(keyPath, true).flatMap(check(_)) ::: updateFiles
  }

  def filesWithMd5(folder: Path): List[(Path, String)] = {
    val files = listFiles(folder, false)
    files.map(f => (f, md5.digest(Files.readAllBytes(root.resolve(f))).map("%02x".format(_)).mkString))
  }

  /**
   * relative in, relative out
   */
  def listFiles(folder: Path, isDirectory: Boolean) = {
    Files.newDirectoryStream(root.resolve(folder), new Filter[Path] {
      override def accept(entry: Path): Boolean = isDirectory == Files.isDirectory(entry,
        LinkOption.NOFOLLOW_LINKS)
    }).toList.map(root.relativize(_))
  }
}
