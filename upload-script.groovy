@Grab(group='org.apache.httpcomponents', module='httpclient', version='4.2.1')
@Grab(group='org.apache.httpcomponents', module='httpmime', version='4.2.1')
@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7' )
@Grab(group='org.apache.httpcomponents', module='httpmime', version='4.1.2')
@Grab(group='org.apache.httpcomponents', module='httpcore', version='4.2.3')
@Grab(group='org.apache.httpcomponents', module='httpclient', version='4.2.3')
@Grab(group='org.apache.commons', module='commons-io', version='1.3.2')
@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7' )
@Grab(group = 'org.apache.httpcomponents', module = 'httpclient', version = '4.5.1')
@Grab(group = 'org.apache.httpcomponents', module = 'httpmime', version = '4.5.1')
@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.2')
@Grab(group='org.apache.httpcomponents', module='httpclient', version='4.3.5')
@Grab(group='org.apache.httpcomponents', module='httpmime', version='4.3.5')

import org.apache.http.entity.mime.MultipartEntityBuilder
import groovyx.net.http.HTTPBuilder
import org.apache.http.entity.mime.content.FileBody
import static groovyx.net.http.Method.*
import static groovyx.net.http.ContentType.*
import org.apache.http.client.*
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.*
import org.apache.http.entity.mime.*


/**
 * Base URL and auth-data
 */
def baseUrl = "http://10.6.102.254:8081/"

def username = "admin"
def password = "admin123"

/**
 * Login
 */
HTTPBuilder httpPost = new HTTPBuilder(baseUrl)
HttpClient httpClientLogin = new DefaultHttpClient()
String basicAuthString = "Basic ${"$username:$password".bytes.encodeBase64().toString()}"
HttpGet httpGet = new HttpGet(baseUrl)
httpGet.addHeader('Authorization', basicAuthString)


/**
 * login to server
 */
def loginResponse = httpClientLogin.execute(httpGet)
println loginResponse

/**
 * Create a file and parse it's way to file name
 */
File filepath = new File("/home/student/newfile")
def stub = (filepath.path).toString()
def myFileName = stub[-7..-1]

def remotepath = "repository/artifacts"
//def remotepath = "repository/artifacts/$myFileName"
//def remotepath = "/repository/artifacts/$filepath"


/**
 * Send files (bad version)
 */

println """curl -v --user '$username:$password' --upload-file $filepath http://nexus/$remotepath/newfile""".execute().text

/**
 * Send files (normal version, but doesn't works)
 */
/*
try {
    httpPost.request(POST, ANY) { req ->
        uri.path = remotepath.toURI()
        println uri.path




        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create()
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
            FileBody fileBody = new FileBody(filepath)
            builder.addPart(myFileName, fileBody)
        }
        catch (GroovyRuntimeException e) {
            e.printStackTrace()
        }

        response.success = { resp, reader ->
            if (resp.status == 201) {
                println "success!"
            }
        }
        response.'404' = { resp, reader ->
            println 'Not found'
        }
        println response
    }
}
catch (HttpResponseException e) {
    e.printStackTrace()
}*/
