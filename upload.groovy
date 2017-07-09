import groovyx.net.http.Method
import groovyx.net.http.ContentType;
import org.apache.http.HttpRequest
import org.apache.http.HttpRequestInterceptor
import org.apache.http.entity.mime.MultipartEntity
import org.apache.http.entity.mime.content.FileBody
import org.apache.http.entity.mime.content.StringBody
import org.apache.http.protocol.HttpContext
import groovyx.net.http.HttpResponseException;


def groupid="34"
def buildversion="34"
def artifactname="helloworld-34"

http://192.168.56.51:8081/repository/artifact/helloworld-34.tar.gz



class NexusUpload {
  def uploadArtifact(Map artifact, File fileToUpload, String user, String password) {
    def path = "/repository/artifact/${groupid}/${buildversion}/${artifactname}/${buildversion}-${artifactname}.tar.gz"
    HTTPBuilder http = new HTTPBuilder("http://192.168.56.51:8081")
    String basicAuthString = "Basic " + "admin:admin123".bytes.encodeBase64().toString()

    http.client.addRequestInterceptor(new HttpRequestInterceptor() {
      void process(HttpRequest httpRequest, HttpContext httpContext) {
        httpRequest.addHeader('Authorization', basicAuthString)
      }
    })
    try {
      http.request(Method.POST, ContentType.ANY) { req ->
        uri.path = path

      MultipartEntity entity = new MultipartEntity()
      entity.addPart("hasPom", new StringBody("false"))
      entity.addPart("file", new FileBody(fileToUpload))
      entity.addPart("a", new StringBody("my-artifact-id"))
      entity.addPart("g", new StringBody("my-group-id"))
      entity.addPart("r", new StringBody("my-repository"))
      entity.addPart("v", new StringBody("my-version"))
      req.entity = entity

      response.success = { resp, reader ->
        if(resp.status == 201) {
          println "success!"
        }
      }
    }

    } catch (HttpResponseException e) {
      e.printStackTrace()
    }
  }
}
