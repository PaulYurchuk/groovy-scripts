class upload {

    public static void main(def args) {
        println("Printing arguments");
        for(String arguments : args) {
            println (arguments);
        }
    }

def cred = "nexus-service-user:jenkins"
def artifact = "hello-59-2.0-release.tar.gz"
def repo = "project-releases"
def way = "http://192.168.50.11:8081/"
def File = new File ("${ARTIFACT_SUFFIX}-{$BUILD_NUMBER}.tar.gz").getBytes()

def connection = new URL( "${way}/repository/${repo}/${ARTIFACT_SUFFIX}/${ARTIFACT_SUFFIX}/2.0/${ARTIFACT_SUFFIX}-{$BUILD_NUMBER}.tar.gz" )
        .openConnection() as HttpURLConnection
def auth = "${cred}".getBytes().encodeBase64().toString()

connection.setRequestMethod("PUT")
connection.doOutput = true
connection.setRequestProperty("Authorization" , "Basic ${auth}")

def writer = new DataOutputStream(connection.outputStream)
writer.write (File)
writer.flush()
writer.close()

println connection.responseCode
