CliBuilder cli = new CliBuilder(
        usage: 'groovy upload.groovy -a {ARTIFACT_SUFFIX} -b {BUILD_NUMBER}')
cli.with {
    a longOpt: 'ARTIFACT_SUFFIX', args: 1, required: true, 'Artifact_suffix from job Jenkins'
    b longOpt: 'BUILD_NUMBER', args: 1, required: true, 'Build_number from job Jenkins'
}
def options = cli.parse(args)
if (!options) {
    return
}

def  ARTIFACT_SUFFIX= options.a
def BUILD_NUMBER= options.b

def cred = "nexus-service-user:jenkins"
def repo = "project-releases"
def way = "http://nexus"
def File = new File ("${ARTIFACT_SUFFIX}-${BUILD_NUMBER}.tar.gz").getBytes()

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
