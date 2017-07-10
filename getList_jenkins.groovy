@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1')

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.Method.POST


def user = 'admin'
def password = 'admin123'
def REPOSITORYID = "MNT-maven2-hosted-releases"
def baseURL = "http://10.6.102.119:8081"
def artifacts = []
String basicAuthString = "Basic " + "$user:$password".bytes.encodeBase64().toString()

def reqmap =  """ { "action": "coreui_Component", "method":"readAssets",
                "data":[{"page":"1", "start":"0",    "limit":"300",
                "sort": [{"property":"name","direction":"ASC"}],
                "filter": [{"property":"repositoryName",
                "value":"$REPOSITORYID"}]}],
                "type":"rpc", "tid":15 } """


def reqartf = new HTTPBuilder("$baseURL")

reqartf.request(POST, TEXT) { req ->
    headers."Authorization" = basicAuthString
    uri.path = "/service/extdirect"
    headers."Content-Type"="application/json"
    body = reqmap

    response.success = { resp, json ->
        def slurper = new groovy.json.JsonSlurper()
        def jsonT2S = json.text as String
        def jsonParse = slurper.parseText(jsonT2S)
        jsonParse.result.data.each {
            if (it.name.matches(~/.+.tar.gz/)) {
                def parsed = it.name.substring(it.name.lastIndexOf("/")+1 , it.name.lastIndexOf("-"))
                artifacts.add(parsed)
            }
        }
    }
}

artifacts

