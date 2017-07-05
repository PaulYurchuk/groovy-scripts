/**
 * Created by student on 7/5/17.
 */
@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.6')

import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

//groupId = "$groupId"
//artifactId = "$artifactId"
//repository = "$repository"
//println "Repository: $repository GroupId: $groupId ArtifactId: $artifactId"

nexusServer = "http://192.168.56.25:8081"
def http = new HTTPBuilder( nexusServer )

http.headers[ 'Autorization' ] = "Basic " + "jenkinsnexus:jenkins".getBytes('iso-8859-1').encodeBase64()
//http.path = "/repository/Artifact_storage/"

def artifacts = [" "]

http.request( GET, JSON ) {
//    uri.path = "/nexus/service/local/lucene/search"
      uri.path = "/repository/Artifact_storage/"
//    uri.query = [ repositoryId: repository, g: groupId, a: artifactId]

    response.success = { resp, json ->

        json.data.each {
            if (it is "*.tar.gz") {
                artifacts.add(it)
            }
        }
    }

    // handler for any failure status code:
    response.failure = { resp ->
        println "Unexpected error: ${resp.statusLine.reasonPhrase}"
    }
}
return artifacts.sort()

//NEXUS_URL=http://nexusREPO=myCoolRepoAUTH=login:passwordLIST_OF_ARTIFACTS=`curl -s -X POST -H "Content-Type: application/json" \                -u $AUTH $NEXUS_URL/service/extdirect -H 'Accept: */*' \                --data-binary "{"action":"coreui_Component", \                                "method":"readAssets", \                                "data":[{"page":1,"start":0,\                                        "limit":300,"sort":[{"property":"name","direction":"ASC"}], \                                        "filter":[{"property":"repositoryName","value":$REPO}]}], \                                        "type":"rpc","tid":15}" --compressed \                                        | jq -r '.result|.data[].name' | sed 's|.*/||g'`echo "${LIST_OF_ARTIFACTS}"
