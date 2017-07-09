new File("/home/student/Downloads/testtest.tar.gz").withOutputStream { out ->
    def url = new URL("http://192.168.56.30:8081/repository/artifact-storage/com/epam/akonchyts/helloworld/34/helloworld-34.tar.gz").openConnection()
    def remoteAuth = "Basic " + "admin:admin123".bytes.encodeBase64()
    url.setRequestProperty("Authorization", remoteAuth);
    out << url.inputStream
}
