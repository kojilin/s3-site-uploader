package s3uploader

import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import java.nio.file.{Paths, Path}
import org.scalamock.scalatest.MockFactory
/**
 *
 * Created by kojilin on 2014/10/12.
 */
@RunWith(classOf[JUnitRunner])
class MD5CheckerTest extends org.scalatest.FunSuite with MockFactory with BeforeAndAfter{
  
  trait CheckRoot{
    def s3Client:S3SiteClient
  }

  trait CheckRoot1 extends CheckRoot{
    def checker = new MD5Checker(Paths.get("src/test/resources/root"), s3Client)
  }
  
  trait CheckRoot2 extends CheckRoot{
    def checker = new MD5Checker(Paths.get("src/test/resources/root2"), s3Client)
  }

  test("md5 in folder"){
    new CheckRoot1{
      val s3Client = mock[S3SiteClient]
      assert(List((Paths.get("file1.txt"), "5782f85f9c922f0f9f870b81e9852abf")) === checker.filesWithMd5(Paths.get("")))
    }
  }

  test("md5 in folder contians folder"){
    new CheckRoot2{
      val s3Client = mock[S3SiteClient]
      assert(List((Paths.get("file1.txt"), "ebd7385edfbbb36cd3a7a5dffb134b98")) === checker.filesWithMd5(Paths.get("")))
    }
  }

  test("new files in folder"){
    new CheckRoot2{
      val s3Client = mock[S3SiteClient]
      (s3Client.filesWithMd5 _).expects().returning(Nil)
      assert(List((Paths.get("file1.txt"), true), (Paths.get("subDir1/file1.txt"), true), (Paths.get("subDir1/file2.txt"), true)) 
        === checker.check())
    }
  }
  test("new files in folder diff with remote s3 md5"){
    new CheckRoot2{
      val s3Client = mock[S3SiteClient]
      (s3Client.filesWithMd5 _).expects()
        .returning(List(("subDir1/file1.txt", "ebd7385edfbbb36cd3a7a5dffb134b98"),
          ("subDir1/file2.txt", "a8bbd0d44af64b0d69cca2de2ff602a9")))
      assert(List((Paths.get("file1.txt"), true)) === checker.check())
    }
  }

  test("ignore invisible file"){
    val s3Client = mock[S3SiteClient]
    (s3Client.filesWithMd5 _).expects().returning(Nil)
    val checker = new MD5Checker(Paths.get("src/test/resources/root3"), s3Client)
    assert(checker.check.size == 1)
  }

}
