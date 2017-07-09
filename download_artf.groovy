def user = "admin"
def password = "admin123"
def ARTIFACTID = "zvirinsky"
def BUILD_NUMBER = "22"
def GROUPID = "Task-8"
def VERSIONID = "1.0"
def baseURL = "http://192.168.1.223:8081"
def repositoryid = "MNT-maven2-hosted-releases"
String basicAuthString = "Basic " + "$user:$password".bytes.encodeBase64().toString()


new File("${ARTIFACTID}_${BUILD_NUMBER}-${VERSIONID}.tar.gz").withOutputStream { out ->
    def url = new URL("${baseURL}/repository/${repositoryid}/${GROUPID}/${ARTIFACTID}_${BUILD_NUMBER}/${VERSIONID}/${ARTIFACTID}_${BUILD_NUMBER}-${VERSIONID}.tar.gz").openConnection()
    url.setRequestProperty("Authorization", basicAuthString)
    out << url.inputStream
}