import groovy.json.JsonSlurper
@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1' )
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
def httpreq =  """ { "action": "coreui_Component",    
    "method":"readAssets",    
    "data":[{"page":"1", "start":"0",
    "limit":"300", "sort":[{"property":"name","direction":"ASC"}],    
    "filter":[{"property":"repositoryName","value":"Artifact-storage"}]}],
    "type":"rpc",
    "tid":15
    } """
def remote = new HTTPBuilder("http://192.168.56.24:8081")
def download(String remoteUrl, String localUrl) {
    new File("$localUrl").withOutputStream { out ->
        new URL(remoteUrl).withInputStream { from ->  out << from }
    }
}
remote.request(POST, TEXT) { req ->
    uri.path = '/service/extdirect'
    headers."Content-Type"="application/json"
    headers.'Accept'="*/*"
    body = httpreq
    headers.'Authorization' = "Basic ${"admin:admin123".bytes.encodeBase64().toString()}"
    def cl = response.success = { resp, json ->
        def listArtifacts = []
        def slurper = new JsonSlurper()
        def jsonRes = json.text as String
        def parsed = slurper.parseText(jsonRes)
        parsed.result.data.each { if (it.name.matches(~/.+.tar.gz/)) listArtifacts.add(it.name) }
        println(listArtifacts[-1])
        download("http://192.168.56.24:8081/repository/Artifact-storage/"+listArtifacts[-1], "./*.tar.gz")
    }
    cl
}
