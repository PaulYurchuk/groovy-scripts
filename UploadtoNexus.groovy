@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.2')
import groovyx.net.http.HTTPBuilder

import org.apache.http.HttpRequest
import org.apache.http.HttpRequestInterceptor
import org.apache.http.protocol.HttpContext

import static groovy.io.FileType.FILES
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.Method.PUT

def filePath = "/Users/dart/Downloads/artifact-maven-73-1.0.tar.gz"
def repName = "my-maven-storage"
def nexusURL = "10.6.102.93/"
def nexusLogin = "admin"
def nexusPass = "admin123"
def group = "/artifacts/artifact-maven-73/1.0"
def artifact = "artifact-maven-73-1.0.tar.gz"
def vesion = "1.0"


File sourceFile = new File(filePath)
assert sourceFile.exists(): "${sourceFile} does not exist"
def authInterceptor = new HttpRequestInterceptor() {
    void process(HttpRequest httpRequest, HttpContext httpContext) {
        httpRequest.addHeader('Authorization', 'Basic ' + "${nexusLogin}:${nexusPass}".bytes.encodeBase64().toString())
    }
}
println "pushing ${sourceFile.name}"
def http = new HTTPBuilder( "http://${nexusURL}" )
http.client.addRequestInterceptor(authInterceptor)
http.request( PUT, 'application/octet-stream' ) { req ->
    uri.path = "/repository/${repName}/${group}/${artifact}/${vesion}/${artifact}-${vesion}.tar.gz"
    headers."Content-Type"="application/octet-stream"
    headers."Accept"="*/*"
    body = sourceFile.bytes
    response.success = { resp ->
        println "POST response status: ${resp.statusLine}"
        assert resp.statusLine.statusCode == 201
    }

}