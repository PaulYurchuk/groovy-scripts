/**
 * Created by student on 7/5/17.
 */



@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1')

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.Method.POST


def user = 'admin'
def password = 'admin123'
def myrepo = "MNT-maven2-hosted-releases"
def baseURL = "http://nexus"
def listArtifacts = []
String basicAuthString = "Basic " + "$user:$password".bytes.encodeBase64().toString()

def params =  """ { "action": "coreui_Component", "method":"readAssets",
                "data":[{"page":"1", "start":"0",    "limit":"300",
                "sort": [{"property":"name","direction":"ASC"}],
                "filter": [{"property":"repositoryName",
                "value":"$myrepo"}]}],
                "type":"rpc", "tid":15 } """


def remote = new HTTPBuilder("$baseURL")

remote.request(POST, TEXT) { req ->
    headers."Authorization" = basicAuthString
    uri.path = "/service/extdirect"
    headers."Content-Type"="application/json"
    headers.'Accept'="*/*"
    headers.Accept="application/json"
    body = params

    response.success = { resp, json ->
        def slurper = new groovy.json.JsonSlurper()
        def jsonT2S = json.text as String
        def jsonParse = slurper.parseText(jsonT2S)
        jsonParse.result.data.each {
            if (it.name.matches(~/.+.tar.gz/)) {
                listArtifacts.add(it.name)
            }
        }
    }
}
for ( i in listArtifacts ) {
    println i
}




