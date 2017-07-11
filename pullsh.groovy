@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.2')

import groovyx.net.http.HTTPBuilder
import org.apache.http.HttpRequest
import org.apache.http.HttpRequestInterceptor
import org.apache.http.protocol.HttpContext
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*


CliBuilder cli = new CliBuilder(
        usage: 'groovy pullsh.groovy -e {pull|push} -u {user} -p {password} -f {ARTIFACT_NAME} -n {repoName} [-h {nx3Url}]')
cli.with {
    e longOpt: 'execute', args: 1,  required: true, values: ['pull','push'], 'pull for download | push for upload'
    u longOpt: 'username', args: 1, 'User with permissions to upload to the target repo'
    p longOpt: 'password', args: 1, 'Password for user'
    r longOpt: 'repository', args: 1, 'Name of repository to upload to | download from , strict content validation is suggested to be turned off.'
//    d longOpt: 'directory', args: 1, required: true, 'Path of directory with artifact to upload or where to download artifact'
    f longOpt: 'ARTIFACT_NAME', args: 1, required: true, 'name of artifact to upload|download'
    h longOpt: 'host', args: 1, 'Nexus Repository Manager 3 host url (including port if necessary). Defaults to http://nexus/'
}
def options = cli.parse(args)
if (!options) {
    return
}
def ARTIFACT_NAME = (options.f)
def username = (options.u ?: "nexus-service-user")
def password = (options.password ?: "admin123")
def nexus = (options.h ?: "http://192.168.56.25:8081")
def choice = (options.e)
def repo = (options.r ?: "project-releases")
def cred = "${username}:${password}"


def http = new HTTPBuilder("$nexus")
def authInterceptor = new HttpRequestInterceptor() {
   void process(HttpRequest httpRequest, HttpContext httpContext) {
       httpRequest.addHeader('Authorization', 'Basic ' + "${username}:${password}".bytes.encodeBase64().toString())
}
}

        if("$choice"=="push"){
def ARTIFACT_ID = ARTIFACT_NAME.substring(0, ARTIFACT_NAME.lastIndexOf("-"))
def Vers2 = ARTIFACT_NAME.replaceAll("\\D+","")
def File = new File ("${ARTIFACT_ID}-${Vers2}.tar.gz")                
        http.client.addRequestInterceptor(authInterceptor)
          http.request(PUT, 'application/octet-stream') { req ->
              uri.path = "/repository/${repo}/${ARTIFACT_ID}/${ARTIFACT_ID}/${Vers2}/${ARTIFACT_ID}-${Vers2}.tar.gz"
              headers."Content-Type"="application/octet-stream"
              headers."Accept"="*/*"
              body = File.bytes
             response.success = { resp ->
                  println "POST response status: ${resp.statusLine}"
                  assert resp.statusLine.statusCode == 201
                  }  
             }
        }
else {

        def ARTIFACT_ID = ARTIFACT_NAME.substring(0, ARTIFACT_NAME.lastIndexOf("-"))
        def Vers2 = ARTIFACT_NAME.replaceAll("\\D+","")
        
            println 'pull'
            def httpreq = """ { "action": "coreui_Component",    
    "method":"readAssets",    
    "data":[{"page":"1", "start":"0",
    "limit":"300", "sort":[{"property":"name","direction":"ASC"}],    
    "filter":[{"property":"repositoryName","value":"${repo}"}]}],
    "type":"rpc",
    "tid":15
    } """

               http.client.addRequestInterceptor(authInterceptor)
               http.request(POST, TEXT) { req ->
               uri.path = '/service/extdirect'
               headers."Content-Type" = "application/json"
               headers.'Accept' = "*/*"
               body = httpreq
               headers.'Authorization' = "Basic ${"${username}:${password}".bytes.encodeBase64().toString()}"
              response.success = { resp, json ->
                  new File(ARTIFACT_NAME).withOutputStream { file ->
                     new URL("${nexus}/repository/${repo}/${ARTIFACT_ID}/${ARTIFACT_ID}/${Vers2}/${ARTIFACT_NAME}").withInputStream { download -> file << download }
                  }
             }
          }
              
//println "pull ${ARTIFACT_NAME}"
//def ARTIFACT_ID = ARTIFACT_NAME.substring(0, ARTIFACT_NAME.lastIndexOf("-"))
//def Vers2 = ARTIFACT_NAME.replaceAll("\\D+","")
//     new File("$ARTIFACT_NAME").withOutputStream { out ->
//    def url = new URL("${nexus}/repository/${repo}/${ARTIFACT_ID}/${ARTIFACT_ID}/${Vers2}/${ARTIFACT_NAME}").openConnection()
//    def remoteAuth = "Basic " + "${cred}".bytes.encodeBase64()
//    url.setRequestProperty("Authorization", remoteAuth);
//    out << url.inputStream
 //       }

   }
