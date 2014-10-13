package s3uploader

import java.nio.file.DirectoryStream.Filter
import java.nio.file.{Files, LinkOption, Path, Paths}

import com.typesafe.scalalogging.StrictLogging

import scala.collection.immutable.List
import scala.collection.JavaConversions._
import java.security.MessageDigest


class MD5Checker(val root: Path, val s3SiteClient: S3SiteClient) extends StrictLogging {

  abstract class FileStatus 

  case class New(val path:Path) extends FileStatus
  case class Update(val path:Path) extends FileStatus
  case class Same() extends FileStatus
  case class Ignore() extends FileStatus


  lazy val md5 = MessageDigest.getInstance("MD5")

  lazy val s3Files = s3SiteClient.filesWithMd5.map(t => (Paths.get(t._1), t._2)).toMap

  def check():List[(Path, Boolean)] = check(Paths.get(""))

  def check(keyPath: Path): List[(Path, Boolean)] = {
    val files = filesWithMd5(keyPath).map(_ match{
      case (path, md5) if Files.isHidden(path) || path.toString.endsWith(".less") => Ignore()
      case (path, md5) => s3Files.get(path) match {
        case None => New(path)
        case Some(oldMd5) if oldMd5 == md5  => Same()
        case _ => Update(path)
      }
    }).flatMap(_ match {
      case New(path) => List((path, true))  
      case Update(path) => List((path, false))  
      case _ => Nil
    })
    files ::: listFiles(keyPath, true).flatMap(check(_))
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
