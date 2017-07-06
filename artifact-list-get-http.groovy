@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7' )
import groovyx.net.http.HTTPBuilder
import groovy.json.*
import static groovyx.net.http.Method.*
import static groovyx.net.http.ContentType.*

def httpRequestProperties = '''{ 
"action": "coreui_Component",    
"method":"readAssets",    
"data":[{"page":"1", "start":"0",    
"limit":"300", "sort":[{"property":"name","direction":"ASC"}],    
"filter":[{"property":"repositoryName","value":"artifacts"}]}],    
"type":"rpc",    
"tid":15 
}'''

def listofArtifacts = []

def http = new HTTPBuilder( 'http://nexus' )
http.request( POST, TEXT ) {
    uri.path =  "/service/extdirect"    
    headers."Content-Type"="application/json"    
  	headers.'Accept'="*/*"
    //uri.path = '/repository/artifacts/'
    body = httpRequestProperties
  
  	//headers.'Authorization' = "Basic ${"admin:admin123".bytes.encodeBase64().toString()}"
  
    response.success = { resp, json ->
        assert resp.status == 200
        println "My response handler got response: ${resp.statusLine}"
        println "Response length: ${resp.headers.'Content-Length'}"
      	def slurper = new JsonSlurper()        
      	def jsonRes = json.text as String        
      	def parsed = slurper.parseText(jsonRes)        
        parsed.result.data.each {         
          if (it.name) 
          {             
            listofArtifacts.add(it.name)         
          }
        }
      
      println(listofArtifacts)
      
    }

    // called only for a 404 (not found) status code:
    response.'404' = { resp,reader ->
        println 'Not found'
    }
}
