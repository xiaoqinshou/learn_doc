package cn.utstarcom.syncdr.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.ConnectException;

@Slf4j
public class FilesFTPUtils implements Closeable {
    private FTPClient ftpClient = new FTPClient();
    private final FilesFtpBuilder filesFtpBuilder;

    private final int RETRY_NUM = 3;

    private FilesFTPUtils(FilesFtpBuilder FilesFtpBuilder) {
        this.filesFtpBuilder = FilesFtpBuilder;
    }

    public static final class FilesFtpBuilder {
        private String hostIp = "127.0.0.1";
        private int port = 21;
        private String userName = "root";
        private String password;
        private String encode;

        public static FilesFtpBuilder create() {
            return new FilesFtpBuilder();
        }

        public FilesFtpBuilder setHostIp(String hostIp) {
            this.hostIp = hostIp;
            return this;
        }

        public FilesFtpBuilder setPort(int port) {
            this.port = port;
            return this;
        }

        public FilesFtpBuilder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public FilesFtpBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public FilesFtpBuilder setEncode(String encode) {
            this.encode = encode;
            return this;
        }

        public FilesFTPUtils builder() {
            Assert.notNull(hostIp, "hostIp is not null");
            Assert.notNull(userName, "userName is not null");
            if (encode == null || !CharEncoding.isSupported(encode)) {
                throw new IllegalArgumentException("Invalid character encoding");
            }
            return new FilesFTPUtils(this);
        }
    }

    private void connect() {
        //解决上传文件时文件名乱码
        if (!StringUtils.isEmpty(filesFtpBuilder.encode)) {
            ftpClient.setControlEncoding(filesFtpBuilder.encode);
        }

        int reply = 0;
        try {
            ftpClient.connect(filesFtpBuilder.hostIp, filesFtpBuilder.port);
            ftpClient.login(filesFtpBuilder.userName, filesFtpBuilder.password);
            reply = ftpClient.getReplyCode();
        } catch (Exception e) {
            log.error("connect {} is error. error code: {}", filesFtpBuilder.hostIp, reply, e);
        }
        log.info("connect {} is success. code: {}", filesFtpBuilder.hostIp, reply);
    }

    private void connectServer() throws ConnectException {
        int retry = 0;
        while (!isConnected() && retry < RETRY_NUM) {
            connect();
            if (!isConnected()) {
                // 连接不上时 休眠30秒 重试3次
                try {
                    log.error("connect FTP fail... fail code: {}, ip: {}, frequency: {}",
                            ftpClient.getReplyCode(),
                            ftpClient.getRemoteAddress(), retry);
                    retry++;
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    log.info("connect FTP sleep fail: {}", e);
                }
            }
        }
        if (!isConnected()) {
            throw new ConnectException("Connection failed. ");
        }
    }


    private boolean exist(String path) {
        FTPFile[] ftpFileArr;
        try {
            ftpFileArr = ftpClient.listFiles(path);
            if (ftpFileArr.length > 0) {
                return true;
            }
        } catch (IOException e) {
            log.error("file {} is not find.", path, e);
        }
        return false;
    }

    private void checkAndCreateFile(String ftpDirPath) {
        if (!exist(ftpDirPath)) {
            mkdirs(ftpDirPath);
            log.info("initDir method create dir : {}.", ftpDirPath);
        }
    }

    private void mkdirs(String dirPath) {
        String[] dirs = dirPath.split("/");
        if (dirs.length == 0) {
            return;
        }
        try {
            ftpClient.changeWorkingDirectory("/");
            for (String dir : dirs) {
                if (!exist(dir)) {
                    ftpClient.makeDirectory(dir);
                }
                ftpClient.changeWorkingDirectory(dir);
            }

            ftpClient.changeToParentDirectory();
        } catch (IOException e) {
            log.error("mkdirs {} fails.", dirPath, e);
        }
    }

    /**
     * 文件上传
     *
     * @param upLoadPath 上传的路径
     * @param localFiles 本地文件
     * @return 是否成功
     */
    public synchronized void upLoad(String upLoadPath, String... localFiles) {
        if (!isConnected()) {
            try {
                connectServer();
            } catch (ConnectException e) {
                log.error("", e);
                return;
            }
        }
        checkAndCreateFile(upLoadPath);
        upload(upLoadPath, localFiles);
    }

    private void upload(String upLoadPath, String... localFiles) {
        try {
            //切换工作路径，设置上传的路径
            ftpClient.changeWorkingDirectory(upLoadPath);
            //设置1M缓冲
            ftpClient.setBufferSize(1024);
            // 设置被动模式
            ftpClient.enterLocalPassiveMode();
            // 设置以二进制方式传输
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            /*
             * 第一个参数：服务器端文档名
             * 第二个参数：上传文档的inputStream
             * 在前面设置好路径，缓冲，编码，文件类型后，开始上传
             */
            for (String localFile : localFiles) {
                File file = new File(localFile);
                try (FileInputStream inputStream = new FileInputStream(file)) {
                    String ftpFile = upLoadPath + "/" + file.getName();
                    ftpClient.storeFile(ftpFile, inputStream);
                    log.info("upload ftp file: {} local file: {}", ftpFile, localFile);
                } catch (IOException e) {
                    log.error("upload File is fails. fileName: {}, path: {}", file.getName(),
                            localFile, e);
                }
            }
        } catch (Exception e) {
            log.error("An exception occurs when changing the working directory or setting the " +
                    "file type", e);
        }
    }

    public void downLoad(String localPath, String... ftpFiles) {
        // todo 以后再写
    }

    @Override
    public void close() throws IOException {
        if (isConnected()) {
            ftpClient.disconnect();
        }
    }

    boolean isConnected() {
        return ftpClient.isConnected();
    }

    public static void main(String[] args) {
        FilesFtpBuilder filesFtpBuilder =
                FilesFtpBuilder.create()
                        .setHostIp("10.48.114.12")
                        .setPort(21)
                        .setUserName("root")
                        .setPassword("rss123")
                        .setEncode("UTF-8");
        try (FilesFTPUtils ftpUtils = filesFtpBuilder.builder()) {
            ftpUtils.upLoad("/opt/hadoop/lance-test", "d:\\test.txt");
        } catch (IOException e) {
            log.error("close error", e);
        }

    }
}
