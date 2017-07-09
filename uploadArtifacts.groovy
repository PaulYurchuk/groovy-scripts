@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.6' )
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

def httpput = new HTTPBuilder("http://192.168.56.30:8081/repository/artifact-storage/group/artifact/2/test3.tar.gz")
httpput.setHeaders(Accept: '*/*')
httpput.request(PUT) { post ->
    requestContentType = BINARY
    body = new File('/home/student/Downloads/testtest.tar.gz').bytes
    headers.'Authorization' = "Basic ${"admin:admin123".bytes.encodeBase64().toString()}"

    response.success = { resp ->
        println "SUCCESS! ${resp.statusLine}"
    }

    response.failure = { resp ->
        println "FAILURE! ${resp.properties}"
    }
}
