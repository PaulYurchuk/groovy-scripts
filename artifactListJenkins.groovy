@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1' )
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

def listArtifacts = []
def httpBuilder = new HTTPBuilder("http://nexus")
def httpRequest =  """ { "action": 
"coreui_Component",    
"method":"readAssets",    
"data":[{"page":"1", 
"start":"0",    
"limit":"300", 
"sort":[{"property":"name","direction":"ASC"}],    
"filter":[{"property":"repositoryName",
"value":"Artifact-storage"}]}],    
"type":"rpc",    
"tid":15	
}"""

    httpBuilder.request(POST, TEXT) { req ->
    uri.path = "/service/extdirect"
    headers."Content-Type" = "application/json"
    headers.'Accept' = "*/*"
    headers.Accept = "application/json"
    body = httpRequest
    headers.'Authorization' = "Basic ${"admin:123".bytes.encodeBase64().toString()}"

    response.success = { resp, json ->
        println "Status: OK" + "\n" + "Status code: ${resp.status}"

        def jsonSlurper = new groovy.json.JsonSlurper()
        def jsonResponse = json.text as String
        def parseResponse = jsonSlurper.parseText(jsonResponse)

        parseResponse.result.data.each {
            if (it.name.matches(~/.+.tar.gz/)) {
                listArtifacts.add(it.name)
            }
        }
    }

    response.failure = { resp ->
        println "Status: Failure. Status code:  " + "\n" + "${resp.status}"
    }
}
    listArtifacts