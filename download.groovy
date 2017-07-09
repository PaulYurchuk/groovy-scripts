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
    def artifactname="helloworld-34.tar.gz"
    def slurper = new groovy.json.JsonSlurper()
    def jsonRes = json.text as String
    def parsed = slurper.parseText(jsonRes)
    parsed.result.data.each {
      if (it.name.matches("$artifactname")) {
      new File("/opt/jenkins/master/workspace/list/${artifactname}").withOutputStream {outfile ->
        new URL ("http://192.168.56.51:8081/repository/artifact/${artifactname}").withInputStream { download -> outfile << download}  
      }
    println "File $artifactname downloaded"
    }
    }
  }
  
  response.failure ={resp, json ->
    println "File $artifactname not downloaded"
  } 
} 
