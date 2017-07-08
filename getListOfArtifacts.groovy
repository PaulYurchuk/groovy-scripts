@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.6' )
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

groupId = "$groupId"
artifactId = "$artifactId"
repository = "$repository"
nexusServer = "$nexusServer"
fileType = "$fileType"

def listArtifacts = []

def httpbody =  """ { "action":"coreui_Component",
    "method":"readAssets",
    "data":[{"page":"1", "start":"0",
    "limit":"300", "sort":[{"property":"name","direction":"ASC"}],
    "filter":[{"property":"repositoryName","value":"${repository}"}]}],
    "type":"rpc",
    "tid":15 } """

def http = new HTTPBuilder( nexusServer )
    http.request(POST, TEXT) { req ->
    uri.path = "/service/extdirect"
    headers."Content-Type"="application/json"
    headers.'Authorization' = "Basic ${"jenkins:jenkins".bytes.encodeBase64().toString()}"
    body = httpbody

    response.success = { resp, json ->
        println "Got response: ${resp.statusLine}"
        def slurper = new groovy.json.JsonSlurper()
        def jsonRes = json.text as String
        def parsed = slurper.parseText(jsonRes)
        parsed.result.data.each {
            if (it.name.matches(~/.+.${fileType}/) && it.attributes.maven2.groupId.matches(~/${groupId}/) && it.attributes.maven2.artifactId.matches(~/${artifactId}/)) {
               listArtifacts.add(it.name)
            }
        }
    }

    response.failure = {resp ->
        println "Got response: ${resp.statusLine}"
    }
}
listArtifacts
