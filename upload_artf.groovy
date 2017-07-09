@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1')

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

def user = "admin"
def password = "admin123"
def ARTIFACTID = "zvirinsky"
def BUILD_NUMBER = "33"
def GROUPID = "Task-8"
def VERSIONID = "1.0"
def baseURL = "http://192.168.1.223:8081"
def repositoryid = "MNT-maven2-hosted-releases"
String basicAuthString = "Basic " + "$user:$password".bytes.encodeBase64().toString()

def upload = new HTTPBuilder("${baseURL}/repository/${repositoryid}/${GROUPID}/${ARTIFACTID}_${BUILD_NUMBER}/${VERSIONID}/${ARTIFACTID}_${BUILD_NUMBER}-${VERSIONID}.tar.gz")
upload.setHeaders(Accept: '*/*')
upload.request(PUT) { post ->
    requestContentType = BINARY
    body = new File("${ARTIFACTID}_${BUILD_NUMBER}-${VERSIONID}.tar.gz").bytes
    headers.'Authorization' = basicAuthString
   
}