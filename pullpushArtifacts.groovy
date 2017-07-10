@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.6' )
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

CliBuilder cli = new CliBuilder(
   usage: 'groovy test.groovy -p {PULLPUSH} -a {ARTIFACT_NAME} -b {ARTIFACT_SUFFIX} -c {BUILD_NUMBER}')
 cli.with {
   p longOpt: 'PULLPUSH', args: 1, required: true, values: ['pull','push'], 'Choose what script has to do with artifact'
   a longOpt: 'ARTIFACT_NAME', args: 1, 'ARTIFACT_NAME from job Jenkins'
   b longOpt: 'ARTIFACT_SUFFIX', args: 1, 'ARTIFACT_SUFFIX from job Jenkins'
   c longOpt: 'BUILD_NUMBER', args: 1, 'BUILD_NUMBER from job Jenkins'
 }
def options = cli.parse(args)
if (!options) {
  return
}

def ARTIFACT_NAME = options.a
def ARTIFACT_SUFFIX = options.b
def BUILD_NUMBER = options.c
def PULLPUSH = options.p
def repository = "artifact-storage"
def nexusServer = "http://192.168.56.30:8081"

if("$PULLPUSH"=="pull"){
  println "pull ${ARTIFACT_NAME}"
  def ARTIFACT_SUFFIX_PULL = ARTIFACT_NAME.substring(0, ARTIFACT_NAME.lastIndexOf("-"))
  def BUILD_NUMBER_PULL = ARTIFACT_NAME.replaceAll("\\D+","")
  new File("${ARTIFACT_NAME}.tar.gz").withOutputStream { out ->
    def url = new URL("${nexusServer}/repository/${repository}/${ARTIFACT_SUFFIX_PULL}/${ARTIFACT_SUFFIX_PULL}/${BUILD_NUMBER_PULL}/${ARTIFACT_NAME}.tar.gz").openConnection()
    def remoteAuth = "Basic " + "jenkins:jenkins".bytes.encodeBase64()
    url.setRequestProperty("Authorization", remoteAuth);
    out << url.inputStream
  }
}
else {
  println "push ${ARTIFACT_SUFFIX}-${BUILD_NUMBER}"
  def httpput = new HTTPBuilder("${nexusServer}/repository/${repository}/${ARTIFACT_SUFFIX}/${ARTIFACT_SUFFIX}/${BUILD_NUMBER}/${ARTIFACT_SUFFIX}-${BUILD_NUMBER}.tar.gz")
  httpput.setHeaders(Accept: '*/*')
  httpput.request(PUT) { post ->
    requestContentType = BINARY
    body = new File("${ARTIFACT_SUFFIX}-${BUILD_NUMBER}.tar.gz").bytes
    headers.'Authorization' = "Basic ${"admin:admin123".bytes.encodeBase64().toString()}"
    response.success = { resp ->
        println "SUCCESS! ${resp.statusLine}"
    }
    response.failure = { resp ->
        println "FAILURE! ${resp.properties}"
    }
  }
}
