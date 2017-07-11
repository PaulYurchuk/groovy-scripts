@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.2')
import groovyx.net.http.HTTPBuilder
import org.apache.http.HttpRequest
import org.apache.http.HttpRequestInterceptor
import org.apache.http.protocol.HttpContext

import static groovy.io.FileType.FILES
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
         
def BUILD_NUMBER=args[1]
def filePath = 'hello-'+BUILD_NUMBER+'.tar.gz'
def artifact = "hello"
         
switch (args[0]) {
         case "upload":
File sourceFile = new File(filePath)
assert sourceFile.exists(): "${sourceFile} does not exist"
def authInterceptor = new HttpRequestInterceptor() {
    void process(HttpRequest httpRequest, HttpContext httpContext) {
        httpRequest.addHeader('Authorization', 'Basic ' + "nexus:nexus".bytes.encodeBase64().toString())
    }
}
println "pushing ${sourceFile.name}"
def http = new HTTPBuilder( "http://192.168.56.51:8081" )
http.client.addRequestInterceptor(authInterceptor)
http.request( PUT, 'application/octet-stream' ) { req ->
    uri.path = "/repository/artifact/${artifact}/${artifact}/${BUILD_NUMBER}/${artifact}-${BUILD_NUMBER}.tar.gz"
    headers."Content-Type"="application/octet-stream"
    headers."Accept"="*/*"
    body = sourceFile.bytes
    response.success = { resp ->
        println "POST response status: ${resp.statusLine}"
        assert resp.statusLine.statusCode == 201
    }
}

break

         case "download": 

def http = new HTTPBuilder("http://192.168.56.51:8081")
http.request(POST, TEXT) { req ->
  	uri.path='/service/extdirect'  	 
  	headers.'Content-Type'="application/json" 
    headers.'Accept'="*/*"
    body = """{ "action":"coreui_Component", "method":"readAssets","data":[{"page":"1","start":"0","limit":"300","sort":[{"property":"name","direction":"ASC"}],"filter":[{"property":"repositoryName","value":"artifact"}]}],"type":"rpc","tid":15 } """
    headers.'Authorization'="Basic ${"nexus:nexus".bytes.encodeBase64().toString()}"
       
   response.success ={resp, json ->
    def slurper = new groovy.json.JsonSlurper()
    def jsonRes = json.text as String
    def parsed = slurper.parseText(jsonRes)
            println parsed
    parsed.result.data.each {
             println it.name
             print it.name.matches("${artifact}/${artifact}/${BUILD_NUMBER}/${artifact}-${BUILD_NUMBER}.tar.gz")
      if (it.name.matches("${artifact}/${artifact}/${BUILD_NUMBER}/${artifact}-${BUILD_NUMBER}.tar.gz")) {
                        sourceFile.withOutputStream { file ->
                            new URL("http://192.168.56.51:8081/repository/artifact/${it.name}").withInputStream { download -> file << download }
}
    println "File $artifactname downloaded"
    }
    }
  }
  
  response.failure ={resp, json ->
    println "File $artifactname not downloaded"
  } 
} 
break

default: println "choose upload or download option"
break
}
