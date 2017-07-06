def cred = "admin:admin123"
def artifact = "hello-58-2.0-release.tar.gz"
def local = "/home/student/"
def repo = "task8"
def way = "http://nexus/repository/${repo}/helloworld/hello-58/2.0/"
new File("${local}${artifact}").withOutputStream { out ->
    def url = new URL("${way}${artifact}").openConnection()
    def remoteAuth = "Basic " + "${cred}".bytes.encodeBase64()
    url.setRequestProperty("Authorization", remoteAuth);
    out << url.inputStream
}
