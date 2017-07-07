@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1' )

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

def repName = "deploy_artifacts"
def nexusURL = "nexus"
def nexusLogin = "admin"
def nexusPass = "admin123"

def listArtifacts = []
def http = new HTTPBuilder( "http://${nexusURL}" )
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
         if (it.name.matches(~/.+.tar.gz/)) {
             listArtifacts.add(it.name)
         }
     }
    }

}
println listArtifacts

