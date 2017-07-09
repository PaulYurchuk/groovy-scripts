CliBuilder cli = new CliBuilder(
    usage: 'groovy test.groovy -a {ARTIFACT_NAME}')
cli.with {
  a longOpt: 'ARTIFACT_NAME', args: 1, required: true, 'ARTIFACT_NAME from job Jenkins'
 }
def options = cli.parse(args)
if (!options) {
  return
}

def ARTIFACT_NAME = options.a
def repository = "artifact-storage"
def nexusServer = "http://192.168.56.30:8081"
def ARTIFACT_SUFFIX = ARTIFACT_NAME.substring(0, ARTIFACT_NAME.lastIndexOf("-"))
def BUILD_NUMBER = ARTIFACT_NAME.replaceAll("\\D+","")

new File("${ARTIFACT_NAME}.tar.gz").withOutputStream { out ->
    def url = new URL("${nexusServer}/repository/${repository}/${ARTIFACT_SUFFIX}/${ARTIFACT_SUFFIX}/${BUILD_NUMBER}/${ARTIFACT_NAME}.tar.gz").openConnection()
    def remoteAuth = "Basic " + "jenkins:jenkins".bytes.encodeBase64()
    url.setRequestProperty("Authorization", remoteAuth);
    out << url.inputStream
}
