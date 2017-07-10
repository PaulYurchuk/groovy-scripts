@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.6' )
@Grab(group='commons-codec', module='commons-codec', version='1.9')
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovyx.net.http.RESTClient

def jsonName = "createRepo"
def jsonContent = "repository.createMavenHosted('test-repo')"

myBody = """ { "name" : "${jsonName}",
               "type" : "groovy",
               "content" : "${jsonContent}"
             } """

println(myBody)

// Put Json
def httpput = new RESTClient("http://nexus/service/siesta/rest/v1/script")
    httpput.setHeaders(Accept: 'application/json')
    httpput.request(POST, JSON) { req ->
    requestContentType = JSON
    body = myBody
    headers.'Authorization' = "Basic ${"admin:admin123".bytes.encodeBase64().toString()}"
    response.success = { resp, json ->
        println "SUCCESS! ${resp.statusLine}"
    }
    response.failure = { resp ->
        println "FAILURE! ${resp.properties}"
    }
}

// Run Json
def httprun = new RESTClient("http://nexus/service/siesta/rest/v1/script/${jsonName}/run")
    httprun.setHeaders(Accept: 'text/plain')
    httprun.request(POST) { req ->
    headers.Accept="*/*"
    headers."Content-Type"="text/plain"
    headers.'Authorization' = "Basic ${"admin:admin123".bytes.encodeBase64().toString()}"
    response.success = { resp, json ->
        println "SUCCESS! ${resp.statusLine}"
    }
    response.failure = { resp ->
        println "FAILURE! ${resp.properties}"
    }
}
