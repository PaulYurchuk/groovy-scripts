@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1')

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

def user = "admin"
def password = "admin123"
def ARTIFACTID = $ARTIFACTID
def BUILD_NUMBER = $BUILD_NUMBER
def GROUPID = $GROUPID
def VERSIONID = "1.0"
def baseURL = "http://${NEXUSIP}:8081"
def repositoryid = $REPOSITORYID
def artifacts = []
String basicAuthString = "Basic " + "$user:$password".bytes.encodeBase64().toString()
def task = $TASK
def FILENAME = "${ARTIFACTID}_${BUILD_NUMBER}-${VERSIONID}.tar.gz"
def FULLPATH = "${baseURL}/repository/${repositoryid}/${GROUPID}/${ARTIFACTID}_${BUILD_NUMBER}/${VERSIONID}/${ARTIFACTID}_${BUILD_NUMBER}-${VERSIONID}.tar.gz"

if (task == "pull") {
        new File("${FILENAME}").withOutputStream { out ->
            def url = new URL("${FULLPATH}").openConnection()
            url.setRequestProperty("Authorization", basicAuthString)
            out << url.inputStream
        }
}else {
         def upload = new HTTPBuilder("${FULLPATH}")
            upload.setHeaders(Accept: '*/*')
            upload.request(PUT) { post ->
            requestContentType = BINARY
            body = new File("${FILENAME}").bytes
            headers.'Authorization' = basicAuthString
            }
}