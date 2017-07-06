@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1' )
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*

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

}
