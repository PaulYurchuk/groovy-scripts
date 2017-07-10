CliBuilder cli = new CliBuilder(
    usage: 'groovy upload.groovy -p {PULLPUSH}  -a {ARTIFACT_NAME} -b {ARTIFACT_SUFFIX} -c {BUILD_NUMBER}')
cli.with {
   p longOpt: 'PULLPUSH', args: 1, required: true, values: ['pull','push'], 'Choose pull or push artifact'
   a longOpt: 'ARTIFACT_NAME', args: 1, 'ARTIFACT_NAME from job Jenkins'
   b longOpt: 'ARTIFACT_SUFFIX', args: 1, 'ARTIFACT_SUFFIX from job Jenkins'
   c longOpt: 'BUILD_NUMBER', args: 1, 'BUILD_NUMBER from job Jenkins'
 }
def options = cli.parse(args)
if (!options) {
  return
}

def  ARTIFACT_NAME= options.a
def  ARTIFACT_SUFFIX= options.b
def BUILD_NUMBER= options.c
def PULLPUSH = options.p

def cred = "nexus-service-user:jenkins"
def repo = "project-releases"
def way = "http://192.168.50.11:8081"

if("$PULLPUSH"=="pull"){

println "pull ${ARTIFACT_NAME}"
def File = new File ("scripts/${ARTIFACT_SUFFIX}-${BUILD_NUMBER}.tar.gz").getBytes()
def connection = new URL( "${way}/repository/${repo}/${ARTIFACT_SUFFIX}/${ARTIFACT_SUFFIX}/${BUILD_NUMBER}/${ARTIFACT_SUFFIX}-${BUILD_NUMBER}.tar.gz" )
        .openConnection() as HttpURLConnection
def auth = "${cred}".getBytes().encodeBase64().toString()
connection.setRequestMethod("PUT")
connection.doOutput = true
connection.setRequestProperty("Authorization" , "Basic ${auth}")
connection.setRequestProperty( "Content-Type", "application/octet-stream" )
connection.setRequestProperty( "Accept", "*/*" )
def writer = new DataOutputStream(connection.outputStream)
writer.write (File)
writer.flush()
writer.close()
println connection.responseCode
}
else {
      println "push ${ARTIFACT_NAME}"
      def ARTIFACT_SUFFIX = ARTIFACT_NAME.substring(0, ARTIFACT_NAME.lastIndexOf("-"))     
      def BUILD_NUMBER = ARTIFACT_NAME.replaceAll("\\D+","")
new File("$ARTIFACT_NAME").withOutputStream { out ->
    def url = new URL("${way}/repository/${repo}/${ARTIFACT_SUFFIX}/${ARTIFACT_SUFFIX}/${BUILD_NUMBER}/${ARTIFACT_NAME}").openConnection()
    def remoteAuth = "Basic " + "${cred}".bytes.encodeBase64()
    url.setRequestProperty("Authorization", remoteAuth);
    out << url.inputStream
}
}
