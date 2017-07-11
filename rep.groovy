#!groovy
CliBuilder cli = new CliBuilder( 
	usage: 'groovy rep.groovy -p {PULLPUSH}  -a {ARTIFACT_NAME} '
)

cli.with {
	p longOpt: 'PULLPUSH', 		args: 1, required: true, values: ['pull','push'], 'Choose pull or push artifact'
	a longOpt: 'ARTIFACT_NAME',	args: 1, 'ARTIFACT_NAME from job Jenkins'
}

def options = cli.parse(args)

if (!options) {
	return
}

def ARTIFACT_NAME= options.a
def PULLPUSH = options.p

def cred = "nexus-service-user:nXs"
def repo = "project-releases"
def way = "http://nexus"

if("$PULLPUSH"=="push"){
	println "push ${ARTIFACT_NAME}"
	def ARTIFACT_SUFFIX = ARTIFACT_NAME.substring(0, ARTIFACT_NAME.lastIndexOf("-"))     
	def BUILD_NUMBER = ARTIFACT_NAME.replaceAll("\\D+","")
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
}
else {
	println "pull ${ARTIFACT_NAME}"
	def ARTIFACT_SUFFIX = ARTIFACT_NAME.substring(0, ARTIFACT_NAME.lastIndexOf("-"))     
	def BUILD_NUMBER = ARTIFACT_NAME.replaceAll("\\D+","")
	new File("$ARTIFACT_NAME").withOutputStream { out ->
		def url = new URL("${way}/repository/${repo}/${ARTIFACT_SUFFIX}/${ARTIFACT_SUFFIX}/${BUILD_NUMBER}/${ARTIFACT_NAME}").openConnection()
		def remoteAuth = "Basic " + "${cred}".bytes.encodeBase64()
		url.setRequestProperty("Authorization", remoteAuth);
		out << url.inputStream
	}
}
