import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
 
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
 
 
/**
* SFTP 프로토콜을 접속 모듈
* 파일 업로드, 다운로드 기능 제공.
*/
public class SFTPService {
    private Session session = null;
    private Channel channel = null;
    private ChannelSftp channelSftp = null;
    
    /**
     * 서버와 연결에 필요한 값들을 가져와 초기화 시킴
     *
     * @param host 서버 주소
     * @param userName 접속에 사용될 아이디
     * @param password 비밀번호
     * @param port 포트번호
     */
    public void init(String host, String userName, String password, int port) {
        JSch jsch = new JSch();

        try {
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            
            session = jsch.getSession(userName, host, port);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();
 
            channel = session.openChannel("sftp");
            channel.connect();
        } catch(JSchException e) {
            e.printStackTrace();
        }
 
        channelSftp = (ChannelSftp) channel;
    }
 
    /**
     * 단일 파일을 업로드
     *
     * @param dir 저장시킬 주소(서버)
     * @param file 저장할 파일 경로
     */
    public boolean upload(String dir, String filePath) {
        boolean result = true;
        FileInputStream in = null;

        try {
            File file = new File(filePath);
            String fileName = file.getName();
            //fileName = URLEncoder.encode(fileName,"EUC-KR");
            
            in = new FileInputStream(file);

            channelSftp.cd(dir);
            channelSftp.put(in, fileName);
        } catch(Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return result;
    }
 
    /**
     * 단일 파일 다운로드
     *
     * @param dir 저장할 경로(서버)
     * @param downloadFileName 다운로드할 파일
     * @param path 저장될 공간
     */
    public void download(String dir, String downloadFileName, String path) {
        InputStream in = null;
        FileOutputStream out = null;
        try {
            channelSftp.cd(dir);

            in = channelSftp.get(downloadFileName);
 
            out = new FileOutputStream(new File(path));

            while((int i = in.read()) != -1) {
                out.write(i);
            }
        } catch (SftpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
 
    /**
     * 서버와의 연결을 끊는다.
     */
    public void disconnection() {
        channelSftp.quit();
    }
    
    
    /**
     * 단일 파일 즉시 업로드
     * 
     * @param sftpHost SFTP 접속 주소(host:IP)
     * @param sftpUser SFTP 접속 USER
     * @param sftpPass SFTP 접속 패스워드
     * @param sftpPort SFTP 접속 포트
     * @param sftpWorkingDir SFTP 작업 경로
     * @param fileFullPath 업로드할 파일 경로
     */
    public static boolean directUpload(
            String sftpHost, String sftpUser, String sftpPass, 
            int sftpPort, String sftpWorkingDir, String fileFullPath) {
        
        boolean result = true;
        
        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;

        System.out.println("preparing the host information for sftp.");
        
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(sftpUser, sftpHost, sftpPort);
            session.setPassword(sftpPass);
            
            // Host 연결.
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            
            // sftp 채널 연결.
            channel = session.openChannel("sftp");
            channel.connect();
            
            // 파일 업로드 처리.
            channelSftp = (ChannelSftp) channel;
            channelSftp.cd(sftpWorkingDir);
            File f = new File(fileFullPath);
            String fileName = f.getName();
            //fileName = URLEncoder.encode(f.getName(),"UTF-8");
            channelSftp.put(new FileInputStream(f), fileName);
        } catch (Exception ex) {
             System.out.println(ex.toString());
             System.out.println("Exception found while tranfer the response.");
             result = false;
        } finally {
            // sftp 채널을 닫음.
            channelSftp.exit();
            
            // 채널 연결 해제.
            channel.disconnect();
            
            // 호스트 세션 종료.
            session.disconnect();
        }
        
        return result;
    }
