def cred = "admin:admin123"
def artifact = "hello-59-2.0-release.tar.gz"
def local = "/home/student/"
def repo = "task8"
//def way = "http://nexus/repository/${repo}/helloworld/hello-59/2.0/"
def File = new File ("/home/student/hello-59-2.0-release.tar.gz").getBytes()

def connection = new URL( "http://nexus/repository/${repo}/helloworld/hello-59/2.0/hello-59-2.0-release.tar.gz" )
        .openConnection() as HttpURLConnection
def auth = "${cred}".getBytes().encodeBase64().toString()

connection.setRequestMethod("PUT")
//allow write to connection
connection.doOutput = true
connection.setDoInput(true)
// set some headers
connection.setRequestProperty("Authorization" , "Basic ${auth}")
connection.setRequestProperty( "Content-Type", "application/octet-stream" )
connection.setRequestProperty( "Accept", "*/*" )


// write query to Output in JSON format
def writer = new DataOutputStream(connection.outputStream)
writer.write (File)
writer.flush()
writer.close()

println connection.responseCode
