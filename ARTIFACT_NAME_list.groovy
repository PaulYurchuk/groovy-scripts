/**
 * Created by student on 7/5/17.
 */
@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1' )
import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
def artifacts = []
HTTPBuilder http = new HTTPBuilder('http://nexus/')
def reqv11 =  ''' { "action":"coreui_Component",
"method":"readAssets",
"data":[{"page":"1",
"start":"0",
"limit":"300",
"sort":[{"property":"name","direction":"ASC"}],
"filter":[{"property":"repositoryName","value":"Artifact_storage"}]}],
"type":"rpc",
"tid":15 } '''
http.request(POST, TEXT) { req ->
    uri.path = '/service/extdirect'
    headers.'Accept'="*/*"
    headers."Content-Type"="application/json"
    body = reqv11
    headers.'Authorization'="Basic ${"admin:admin123".bytes.encodeBase64().toString()}"
    response.success = { resp, json ->
       // assert resp.status == 200
        println("Success code status: " + resp.status )
        def slurper = new groovy.json.JsonSlurper()
        def jsonOUT = json.text as String
        def parsing = slurper.parseText(jsonOUT)
        parsing.result.data.each {
            if (it.name.matches(~/helloworld.+.tar.gz/)) {
                artifacts.add(it.name)
            }
        }
    }

    response.failure = { resp ->
        println("Failure : ${resp.statusLine}")
    }
}
println artifacts
