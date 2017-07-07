@Grab(group='com.jcraft', module='jsch', version='0.1.46')
import com.jcraft.jsch.*

java.util.Properties config = new java.util.Properties()
config.put "StrictHostKeyChecking", "no"
JSch ssh = new JSch()
Session sess = ssh.getSession "root", "192.168.50.12", 22
sess.with {
    setConfig config
    setPassword "vagrant"
    connect()
    Channel chan = openChannel "sftp"
    chan.connect()
    ChannelSftp sftp = (ChannelSftp) chan;
    def sessionsFile = new File('/opt/jenkins/master/workspace/Task8test/artifact.tar.gz')
    sessionsFile.withInputStream { istream -> sftp.put(istream, "/home/artifact.tar.gz") }
    chan.disconnect()
    disconnect()
}
