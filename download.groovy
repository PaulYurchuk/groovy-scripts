CliBuilder cli = new CliBuilder(
    usage: 'groovy upload.groovy -a {ARTIFACT_NAME}')
cli.with {
  a longOpt: 'ARTIFACT_NAME', args: 1, required: true, 'ARTIFACT_NAME from job Jenkins'
  }
def options = cli.parse(args)
if (!options) {
  return
}

def  ARTIFACT_NAME= options.a

def cred = "nexus-service-user:jenkins"
def repo = "project-releases"
def way = "http://192.168.50.11:8081"

new File("artifact.tar.gz").withOutputStream { out ->
    def url = new URL("${way}/repository/${repo}/${ARTIFACT_NAME}").openConnection()
    def remoteAuth = "Basic " + "${cred}".bytes.encodeBase64()
    url.setRequestProperty("Authorization", remoteAuth);
    out << url.inputStream
}
