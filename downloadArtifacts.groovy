groupId = "$groupId"
artifactId = "$artifactId"
repository = "$repository"
nexusServer = "$nexusServer"
fileType = "$fileType"
version = "$version"

new File("/home/student/Downloads/testtest.tar.gz").withOutputStream { out ->
    def url = new URL("${nexusServer}/repository/${repository}/${groupId}/${artifactId}/${version}/${ARTIFACT_NAME}").openConnection()
    def remoteAuth = "Basic " + "admin:admin123".bytes.encodeBase64()
    url.setRequestProperty("Authorization", remoteAuth);
    out << url.inputStream
}
