@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.2')

import groovyx.net.http.HTTPBuilder
import org.apache.http.HttpRequest
import org.apache.http.HttpRequestInterceptor
import org.apache.http.protocol.HttpContext
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

def repName = "test_artifacts"
def nexusURL = "nexus"
def nexusLogin = "admin"
def nexusPass = "admin123"
def artifact = "test_hello-82.tar.gz"
def action = "pull"

def artifactName = artifact.substring(0, artifact.lastIndexOf("-"))
def buildNumber = artifact.replaceAll("\\D+","")


File sourceFile = new File(artifact)
def http = new HTTPBuilder( "http://${nexusURL}" )
def authInterceptor = new HttpRequestInterceptor() {
    void process(HttpRequest httpRequest, HttpContext httpContext) {
        httpRequest.addHeader('Authorization', 'Basic ' + "${nexusLogin}:${nexusPass}".bytes.encodeBase64().toString())
    }
}

switch (action) {
    case "push":
        if (sourceFile.exists()) {
            println "Pushing ${artifact}"
            http.client.addRequestInterceptor(authInterceptor)
            http.request(PUT, 'application/octet-stream') { req ->
                uri.path = "/repository/${repName}/${artifactName}/${artifactName}/${buildNumber}/${artifactName}-${buildNumber}.tar.gz"
                headers."Content-Type" = "application/octet-stream"
                headers."Accept" = "*/*"
                body = sourceFile.bytes
                response.success = { resp ->
                    println "POST response status: ${resp.statusLine}"
                    assert resp.statusLine.statusCode == 201
                }

            }
        }
        else { println "${artifact} does not exist"}

        break
    case "pull":
        println "Pulling ${artifact}"
        http.auth.basic nexusLogin, nexusPass
        http.request( POST, TEXT ) { req ->
            uri.path = '/service/extdirect'
            headers."Content-Type"="application/json"
            headers."Accept"="*/*"
            body = """ {"action":"coreui_Component", "method":"readAssets", 
            "data":[{"page":1,"start":0,"filter":[{"property":"repositoryName","value":"${repName}"}]}], 
                "type":"rpc","tid":15}  """

            response.success = { resp, json ->
                def slurper = new groovy.json.JsonSlurper()
                def jsonRes = json.text as String
                def parsed = slurper.parseText(jsonRes)
                parsed.result.data.each {
                    if (it.name.matches("${artifactName}/${artifactName}/${buildNumber}/${artifactName}-${buildNumber}.tar.gz")) {
                        sourceFile.withOutputStream { file ->
                            new URL("http://${nexusURL}/repository/${repName}/${it.name}").withInputStream { download -> file << download }
                        }
                    }
                }
            }
        }
        break
    default:
        println "Wrong action parameter"
        break
}

