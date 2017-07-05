/**
 * Created by student on 7/5/17.
 */
println "Hello World"
@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.6')

import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*


groupId = "$groupId"
artifactId = "$artifactId"
repository = "$repository"

println "Repository: $repository GroupId: $groupId ArtifactId: $artifactId"

nexusServer = "http://192.168.56.25:8081"
def http = new HTTPBuilder( nexusServer )

http.headers[ 'Autorization' ] = "Basic " + "jenkinsnexus:jenkins".getBytes('iso-8859-1').encodeBase64()

def artifacts = [" "]

http.request( GET, JSON ) {
    uri.path = "/nexus/service/local/lucene/search"
    uri.query = [ repositoryId: repository, g: groupId, a: artifactId]

    response.success = { resp, json ->

        json.data.each {
            artifacts.add("${it.artifactId}-${it.version}.tar.gz")
        }
    }

    // handler for any failure status code:
    responce.failure = { resp ->
        println "Unexpected error: ${resp.statusLine.status.Code} : ${resp.statusLine.reasonPhrase}"
    }
}
return artifacts.sort()