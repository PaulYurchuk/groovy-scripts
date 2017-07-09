@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1' )
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*


def http = new HTTPBuilder("http://192.168.56.51:8081")
http.request(POST, TEXT) { req ->
  	uri.path='/service/extdirect'  	 
  	headers.'Content-Type'="application/json" 
    headers.'Accept'="*/*"
    body = """{ "action":"coreui_Component", "method":"readAssets","data":[{"page":"1","start":"0","limit":"300","sort":[{"property":"name","direction":"ASC"}],"filter":[{"property":"repositoryName","value":"artifact"}]}],"type":"rpc","tid":15 } """
    headers.'Authorization'="Basic ${"admin:admin123".bytes.encodeBase64().toString()}"
    
      
  response.success ={resp, json ->
    def listArtifacts=[]
    def slurper = new groovy.json.JsonSlurper()
    def jsonRes = json.text as String
    def parsed = slurper.parseText(jsonRes)
    parsed.result.data.each {
    if (it.name.matches(~/.+.tar.gz/)) {
        listArtifacts.add(it.name)}
    }
    println "Got response"
    println listArtifacts
  }
  
  response.failure ={resp, json ->
    println "Bad response" 
  } 
}  
