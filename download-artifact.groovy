/**
 * Created by student on 7/6/17.
 */
@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7' )
@Grab(group='org.apache.httpcomponents', module='httpmime', version='4.1.2')
@Grab(group='org.apache.httpcomponents', module='httpcore', version='4.2.3')
@Grab(group='org.apache.httpcomponents', module='httpclient', version='4.2.3')
@Grab(group='org.apache.commons', module='commons-io', version='1.3.2')
@Grab(group='org.apache.httpcomponents', module='httpclient', version='4.2.1')
@Grab(group='org.apache.httpcomponents', module='httpmime', version='4.2.1')

import org.apache.http.*
import static groovyx.net.http.Method.*
import static groovyx.net.http.ContentType.*
import org.apache.http.client.*
import org.apache.http.client.methods.*
import org.apache.http.entity.mime.*
import org.apache.http.entity.mime.content.*

def remoteurl = "http://nexus/repository/artifacts/40-clusterjsp.tar.gz"
def localurl = "/home/student/40-clusterjsp.tar.gz"

def downloadArtifact(String remoteUrl, String localUrl) {
    new File("$localUrl").withOutputStream { out ->
        new URL(remoteUrl).withInputStream { from ->  out << from }
    }
}
this.downloadArtifact(remoteurl,localurl)
