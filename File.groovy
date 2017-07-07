/**
 * Created by USER on 05.07.2017.
 */
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.2')
import groovyx.net.http.HTTPBuilder

/**
 * download|upload file to Nexus 3.x. raw repo/
 */
import org.apache.http.HttpRequest
import org.apache.http.HttpRequestInterceptor
import org.apache.http.protocol.HttpContext
import org.apache.tools.ant.taskdefs.GZip

import static groovy.io.FileType.FILES
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.Method.PUT
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*


CliBuilder cli = new CliBuilder(
        usage: 'groovy file.groovy -e {pull|push} -u {user} -p {password} -d {path to content} -f {name of file} -n {repoName} [-h {nx3Url}]')
cli.with {
    e longOpt: 'execute', args: 1,  required: true, values: ['pull','push'], 'pull for download | push for upload'
    u longOpt: 'username', args: 1, 'User with permissions to upload to the target repo'
    p longOpt: 'password', args: 1, 'Password for user'
    r longOpt: 'repository', args: 1, 'Name of raw repository to upload to | download from , strict content validation is suggested to be turned off.'
    d longOpt: 'directory', args: 1, required: true, 'Path of directory with artifact to upload or where to download artifact'
    f longOpt: 'filename', args: 1, required: true, 'name of artifact to upload|download'
    h longOpt: 'host', args: 1, 'Nexus Repository Manager 3 host url (including port if necessary). Defaults to http://nexus/'
}
def options = cli.parse(args)
if (!options) {
    return
}

File sourceFolder = new File(options.d ?: '/home/student/Downloads/nexus/')
assert sourceFolder.exists(): "${sourceFolder} does not exist"
def username = (options.u ?: 'admin')
def password = (options.password ?: 'admin123')
def authInterceptor = new HttpRequestInterceptor() {
    void process(HttpRequest httpRequest, HttpContext httpContext) {
        httpRequest.addHeader('Authorization', 'Basic ' + "${username}:${password}".bytes.encodeBase64().toString())
    }
}
def choice = (options.e)
HTTPBuilder http = new HTTPBuilder(options.h ?: 'http://nexus/')
http.client.addRequestInterceptor(authInterceptor)
def resourcePath = "/repository/${options.r ?: 'Artifact_storage2'}/"

def file = (options.f ?: 'helloworld-15.tar.gz')

        if($choice=="push"){
            println "pushing $file"
            http.request(PUT) {
                uri.path = "$resourcePath$file"
                requestContentType = TEXT


                body = file.text
                response.success = { resp ->
                    println "POST response status: ${resp.statusLine}"
                    assert resp.statusLine.statusCode == 201
                }
            }

                return file.getPath()

        }else {
println 'pull'
            def httpreq =  """ { "action": "coreui_Component",    
    "method":"readAssets",    
    "data":[{"page":"1", "start":"0",
    "limit":"300", "sort":[{"property":"name","direction":"ASC"}],    
    "filter":[{"property":"repositoryName","value":"${options.r ?: 'Artifact_storage2'}"}]}],
    "type":"rpc",
    "tid":15
    } """
            def remoteUrl = $resourcePath
            def localUrl = $sourceFolder$file
            def download(String remoteUrl, String localUrl) {
                new File("$localUrl").withOutputStream { out ->
                    new URL($remoteUrl).withInputStream { from ->  out << from }
                }
            }
            http.request(POST, TEXT) { req ->
                uri.path = '/service/extdirect'
                headers."Content-Type"="application/json"
                headers.'Accept'="*/*"
                body = httpreq
                headers.'Authorization' = "Basic ${"${username}:${password}".bytes.encodeBase64().toString()}"
                def cl = response.success = { resp, json ->
                    download($remoteUrl, "./$file")
                }
                cl
            }


        }
