@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1')

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

def user = "admin"
def password = "admin123"
def GROUPID = "Task-8"
def VERSIONID = "1.0"
def baseURL = "http://10.6.102.18:8081"
def REPOSITORYID = "MNT-maven2-hosted-releases"
def artifacts = []
String basicAuthString = "Basic " + "$user:$password".bytes.encodeBase64().toString()

CliBuilder cli = new CliBuilder(
   usage: 'groovy nexus_files.groovy -t {TASK ("pull/push")} -a -b -c')
 cli.with {
   t longOpt: 'TASK', args: 1, required: true, values: ['pull','push'], 'What to do with artifact?'
   a longOpt: 'ARTIFACTID', args: 1, 'ARTIFACTID'
   b longOpt: 'ARTIFACT_SUFFIX', args: 1, 'ARTIFACT_SUFFIX'
   c longOpt: 'BUILD_NUMBER', args: 1, 'BUILD_NUMBER'
 }
def options = cli.parse(args)
if (!options) {
  return
}

def TASK = options.t
def ARTIFACTID = options.a
def ARTIFACT_SUFFIX = options.b
def BUILD_NUMBER = options.c



if ("$TASK" == "pull") {
        println "task is $TASK"
        new File("${ARTIFACTID}-${VERSIONID}.tar.gz").withOutputStream { out ->
            def url = new URL("${baseURL}/repository/${REPOSITORYID}/${GROUPID}/${ARTIFACTID}/${VERSIONID}/${ARTIFACTID}-${VERSIONID}.tar.gz").openConnection()
            url.setRequestProperty("Authorization", basicAuthString)
            out << url.inputStream
        }
}else {
    println "task is $TASK"
	        def upload = new HTTPBuilder("${baseURL}/repository/${REPOSITORYID}/${GROUPID}/${ARTIFACTID}/${VERSIONID}/${ARTIFACTID}-${VERSIONID}.tar.gz")
            upload.setHeaders(Accept: '*/*')
            upload.request(PUT) { post ->
            requestContentType = BINARY
            body = new File("${ARTIFACTID}_${VERSIONID}.tar.gz").bytes
            headers.'Authorization' = basicAuthString
            }
}
