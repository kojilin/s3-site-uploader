Simple file uploader for static site on aws s3. Will check all files which are need to upload and then uplaod to s3 bucket.

This project using [Typesafe Config](https://github.com/typesafehub/config), so you just need to set following properties.

	aws.accessKeyId=<id>
	aws.secretKey=<secret>
	bucket=<target bucket name>
	localKeyPath=<local file folder map to remote bucket>

run like below:

	$sbt -Daws.accessKeyId=XXX \
    	 -Daws.secretKey=XXX \
    	 -Dbucket=XXX \
	     -DlocalKeyPath=XXX 



	