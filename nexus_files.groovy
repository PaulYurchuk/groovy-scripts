@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1')

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

def user = "admin"
def password = "admin123"
def ARTIFACTID = $ARTIFACTID
def GROUPID = "Task-8"
def VERSIONID = "1.0"
def baseURL = "http://10.6.102.119:8081"
def REPOSITORYID = "MNT-maven2-hosted-releases"
def artifacts = []
String basicAuthString = "Basic " + "$user:$password".bytes.encodeBase64().toString()
def TASK = $TASK

if (TASK == "pull") {
        new File("${FILENAME}").withOutputStream { out ->
            def url = new URL("${baseURL}/repository/${repositoryid}/${GROUPID}/${ARTIFACTID}/${VERSIONID}/${FILENAME}").openConnection()
            url.setRequestProperty("Authorization", basicAuthString)
            out << url.inputStream
        }
}else {
	 def FILENAME = "${ARTIFACTID}-${VERSIONID}.tar.gz"
	 def FULLPATH = "${baseURL}/repository/${repositoryid}/${GROUPID}/${ARTIFACTID}/${VERSIONID}/${ARTIFACTID}-${VERSIONID}.tar.gz"
         def upload = new HTTPBuilder("${FULLPATH}")
            upload.setHeaders(Accept: '*/*')
            upload.request(PUT) { post ->
            requestContentType = BINARY
            body = new File("${FILENAME}").bytes
            headers.'Authorization' = basicAuthString
            }
}
