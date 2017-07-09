def ARTIFACTID = "zvirinsky"
def BUILD_NUMBER = "23"
def GROUPID = "Task-8"
def VERSIONID = "1.0"
def baseURL = "http://192.168.1.223:8081"
def repositoryid = "MNT-maven2-hosted-releases"
def asd = "tar.gz"



def url = "${baseURL}/repository/${repositoryid}/${GROUPID}/${ARTIFACTID}_${BUILD_NUMBER}/${VERSIONID}/"
def file = new File("${ARTIFACTID}_${BUILD_NUMBER}-${VERSIONID}.${asd}").newOutputStream()
file << new URL(url).openStream()
file.close()