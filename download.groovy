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
def ARTIFACT_SUFFIX = ARTIFACT_NAME.substring(0, ARTIFACT_NAME.lastIndexOf("-"))     
def BUILD_NUMBER = ARTIFACT_NAME.replaceAll("\\D+","")
def cred = "jenkins:123"
def repo = "project-releases"
def way = "http://192.168.50.222:8081"

new File("$ARTIFACT_NAME").withOutputStream { out ->
    def url = new URL("${way}/repository/${repo}/${ARTIFACT_SUFFIX}/${ARTIFACT_SUFFIX}/${BUILD_NUMBER}/${ARTIFACT_NAME}").openConnection()
    def remoteAuth = "Basic " + "${cred}".bytes.encodeBase64()
    url.setRequestProperty("Authorization", remoteAuth);
    out << url.inputStream
}
