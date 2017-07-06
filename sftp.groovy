@Grab(group='com.jcraft', module='jsch', version='0.1.46')
import com.jcraft.jsch.*

java.util.Properties config = new java.util.Properties()
config.put "StrictHostKeyChecking", "no"
JSch ssh = new JSch()
Session sess = ssh.getSession "root", "nexus", 22
sess.with {
    setConfig config
    setPassword "vagrant"
    connect()
    Channel chan = openChannel "sftp"
    chan.connect()
    ChannelSftp sftp = (ChannelSftp) chan;
    def sessionsFile = new File('/home/student/hello-58-2.0-release.tar.gz')
    sessionsFile.withInputStream { istream -> sftp.put(istream, "/home/hello-58-2.0-release.tar.gz") }
    chan.disconnect()
    disconnect()
}
