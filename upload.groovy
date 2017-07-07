
CliBuilder cli = new CliBuilder(
    usage: 'groovy upload.groovy -a {ARTIFACT_SUFFIX} -b {BUILD_NUMBER}')
cli.with {
  a longOpt: 'ARTIFACT_SUFFIX', args: 1, required: true, 'User with permissions to deploy to the target repo'
  b longOpt: 'BUILD_NUMBER', args: 1, required: true, 'Password for user'
 }
def options = cli.parse(args)
if (!options) {
  return
}

def  ARTIFACT_SUFFIX= options.a
def BUILD_NUMBER= options.b

def cred = "nexus-service-user:jenkins"
def artifact = "hello-59-2.0-release.tar.gz"
def repo = "project-releases"
def way = "http://192.168.50.11:8081/"
def File = new File ("${ARTIFACT_SUFFIX}-${BUILD_NUMBER}.tar.gz").getBytes()

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
