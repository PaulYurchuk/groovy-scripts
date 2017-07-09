@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.6' )
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

CliBuilder cli = new CliBuilder(
    usage: 'groovy test.groovy -a {ARTIFACT_SUFFIX} -b {BUILD_NUMBER}')
cli.with {
  a longOpt: 'ARTIFACT_SUFFIX', args: 1, required: true, 'ARTIFACT_SUFFIX from job Jenkins'
  b longOpt: 'BUILD_NUMBER', args: 1, required: true, 'BUILD_NUMBER from job Jenkins'
 }
def options = cli.parse(args)
if (!options) {
  return
}

def ARTIFACT_SUFFIX = options.a
def BUILD_NUMBER = options.b
def repository = "artifact-storage"
def nexusServer = "http://192.168.56.30:8081/"

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
