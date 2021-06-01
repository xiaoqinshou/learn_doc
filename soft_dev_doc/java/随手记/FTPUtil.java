package cn.utstarcom.syncdr.utils;

import cn.utstarcom.syncdr.config.FTPConfig;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.SocketException;
import java.time.Duration;
import java.time.Instant;

@Component
public class FTPUtil {

    private static Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    //private FTPClient ftpClient;

    private final FTPConfig ftpConfig;
    //从本地文件获取各种属性
    private String sourceFtpIP;
    private Integer sourceFtpPort;
    private String sourceFtpUserName;
    private String sourceFtpPassword;
    private String destFtpIP;
    private Integer destFtpPort;
    private String destFtpUserName;
    private String destFtpPassword;
    private String ftpEncode;
    private String localPath;


    public FTPUtil(FTPConfig ftpConfig) {

        this.ftpConfig = ftpConfig;
        this.sourceFtpIP = ftpConfig.getSourdeFtpIP();
        this.sourceFtpPort = ftpConfig.getSourdeFtpPort();
        this.sourceFtpUserName = ftpConfig.getSourdeFtpUserName();
        this.sourceFtpPassword = ftpConfig.getSourdeFtpPassword();
        this.destFtpIP = ftpConfig.getDestFtpIP();
        this.destFtpPort = ftpConfig.getDestFtpPort();
        this.destFtpUserName = ftpConfig.getDestFtpUserName();
        this.destFtpPassword = ftpConfig.getDestFtpPassword();
        this.ftpEncode = ftpConfig.getFtpEncode();

    }

    public synchronized boolean connectSourceServer(FTPClient ftpClient) {
        long begin = System.currentTimeMillis();
        System.out.println("connectSourceServer: start " + begin);
        boolean isOK = connectServer(ftpClient, sourceFtpIP, sourceFtpPort, sourceFtpUserName, sourceFtpPassword);
        System.out.println("connectSourceServer: isOK: "+isOK+" now: " + (System.currentTimeMillis()-begin));
        return isOK;
    }

    public synchronized boolean connectYNMobileServer(FTPClient ftpClient) {
        long begin = System.currentTimeMillis();
        System.out.println("connectYNMobileServer: start " + begin);
        boolean isOK = connectServer(ftpClient, ftpConfig.getYnMobileFtpIP(), ftpConfig.getYnMobileFtpPort(),
                ftpConfig.getYnMobileFtpUserName(), ftpConfig.getYnMobileFtpPassword());
        System.out.println("connectYNMobileServer: isOK: "+isOK+" now: " + (System.currentTimeMillis()-begin));
        return isOK;
    }

    public synchronized boolean connectCommonDestServer(FTPClient ftpClient) {
        long begin = System.currentTimeMillis();
        System.out.println("connectCommonDestServer: start " + begin);
        boolean isOK = connectServer(ftpClient, ftpConfig.getCommonDestFtpIP(), ftpConfig.getCommonDestFtpPort(),
                ftpConfig.getCommonDestFtpUserName(), ftpConfig.getCommonDestFtpPassword());
        System.out.println("connectCommonDestServer: isOK: "+isOK+" now: " + (System.currentTimeMillis()-begin));
        return isOK;
    }

    public synchronized boolean connectDestServer(FTPClient ftpClient) {
        long begin = System.currentTimeMillis();
        System.out.println("connectDestServer: start " + begin);
        boolean isOk = connectServer(ftpClient, destFtpIP, destFtpPort, destFtpUserName, destFtpPassword);
        System.out.println("connectDestServer: isOK: "+isOk+" now: " + (System.currentTimeMillis()-begin));
        logger.info("ftpClient is : {}, connecting server : {},connected is : {},now server is : {}.", ftpClient, destFtpIP,
                isOk, ftpClient.getRemoteAddress());
        return isOk;
    }

    public synchronized boolean connectTelecomServer(FTPClient ftpClient) {
        long begin = System.currentTimeMillis();
        System.out.println("connectTelecomServer: start " + begin);
        boolean isOk =connectServer(ftpClient, ftpConfig.getTelecomFtpIP(), ftpConfig.getTelecomFtpPort(),
                ftpConfig.getTelecomFtpUserName(), ftpConfig.getTelecomFtpPassword());
        System.out.println("connectTelecomServer: isOK: "+isOk+" now: " + (System.currentTimeMillis()-begin));
        return isOk;
    }
    public synchronized boolean connectMobileServer(FTPClient ftpClient) {
        long begin = System.currentTimeMillis();
        System.out.println("connectMobileServer: start " + begin);
        boolean isOk = connectServer(ftpClient, ftpConfig.getMobileFtpIP(), ftpConfig.getMobileFtpPort(),
                ftpConfig.getMobileFtpUserName(), ftpConfig.getMobileFtpPassword());
        System.out.println("connectMobileServer: isOK: "+isOk+" now: " + (System.currentTimeMillis()-begin));
        return isOk;
    }
    public synchronized boolean connectUnicomServer(FTPClient ftpClient) {
        long begin = System.currentTimeMillis();
        System.out.println("connectUnicomServer: start " + begin);
        boolean isOk = connectServer(ftpClient, ftpConfig.getUnicomFtpIP(), ftpConfig.getUnicomFtpPort(),
                ftpConfig.getUnicomFtpUserName(), ftpConfig.getUnicomFtpPassword());
        System.out.println("connectUnicomServer: isOK: "+isOk+" now: " + (System.currentTimeMillis()-begin));
        return isOk;
    }
    public synchronized boolean connectIptvServer(FTPClient ftpClient) {
        long begin = System.currentTimeMillis();
        System.out.println("connectIptvServer: start " + begin);
        boolean isOk =  connectServer(ftpClient, ftpConfig.getIptvFtpIP(), ftpConfig.getIptvFtpPort(),
                ftpConfig.getIptvFtpUserName(), ftpConfig.getIptvFtpPassword());
        System.out.println("connectIptvServer: isOK: "+isOk+" now: " + (System.currentTimeMillis()-begin));
        return isOk;
    }

    public synchronized boolean connectAdvertTelecomServer(FTPClient ftpClient) {
        long begin = System.currentTimeMillis();
        System.out.println("connectAdvertTelecomServer: start " + begin);
        boolean isOk =   connectServer(ftpClient, ftpConfig.getAdvertTelecomIp(), ftpConfig.getAdvertTelecomPort(),
                ftpConfig.getAdvertTelecomUsername(), ftpConfig.getAdvertTelecomPassword());
        System.out.println("connectAdvertTelecomServer: isOK: "+isOk+" now: " + (System.currentTimeMillis()-begin));
        return isOk;
    }
    public synchronized boolean connectAdvertMobileServer(FTPClient ftpClient) {
        long begin = System.currentTimeMillis();
        System.out.println("connectAdvertMobileServer: start " + begin);
        boolean isOk =   connectServer(ftpClient, ftpConfig.getAdvertMobileIp(), ftpConfig.getAdvertMobilePort(),
                ftpConfig.getAdvertMobileUsername(), ftpConfig.getAdvertMobilePassword());
        System.out.println("connectAdvertMobileServer: isOK: "+isOk+" now: " + (System.currentTimeMillis()-begin));
        return isOk;
    }
    public synchronized boolean connectAdvertUnicomServer(FTPClient ftpClient) {
        long begin = System.currentTimeMillis();
        System.out.println("connectAdvertUnicomServer: start " + begin);
        boolean isOk =   connectServer(ftpClient, ftpConfig.getAdvertUnicomIp(), ftpConfig.getAdvertUnicomPort(),
                ftpConfig.getAdvertUnicomUsername(), ftpConfig.getAdvertUnicomPassword());
        System.out.println("connectAdvertUnicomServer: isOK: "+isOk+" now: " + (System.currentTimeMillis()-begin));
        return isOk;
    }

    public  boolean connectServer(FTPClient ftpClient, String ftpIP, int ftpPort, String ftpUserName, String ftpPassword) {
        ftpClient.setControlEncoding(ftpEncode);//解决上传文件时文件名乱码
        int reply = 0;
        try {
            Thread.sleep(1000);
            // 连接至服务器
            ftpClient.connect(ftpIP, ftpPort);
            // 登录服务器
            ftpClient.login(ftpUserName, ftpPassword);
            //登陆成功，返回码是230
            reply = ftpClient.getReplyCode();
            // 判断返回码是否合法
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                return false;
            }
        } catch (SocketException e) {
//            e.printStackTrace();
        } catch (IOException e) {
//            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return true;
    }

    //判断ftp服务器文件是否存在
    public boolean existFile(FTPClient ftpClient, String path) {
        if (ftpClient == null || !ftpClient.isConnected()) {
            logger.info("ftpClient is null or ftpClient is not connected");
            return false;
        }
        boolean flag = false;
        FTPFile[] ftpFileArr;
        try {
            ftpFileArr = ftpClient.listFiles(path);
            if (ftpFileArr.length > 0) {
                flag = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    //删除ftp文件
    public synchronized boolean deleteFile(FTPClient ftpClient, String pathname, String filename) {
        if (ftpClient == null || !ftpClient.isConnected()) {
            logger.info("ftpClient is null or ftpClient is not connected");
            return false;
        }
        boolean flag = false;
        try {
            System.out.println("开始删除文件");
            //切换FTP目录
            ftpClient.changeWorkingDirectory(pathname);
            //flag = ftpClient.deleteFile(pathname + "/" + filename);
            ftpClient.dele(filename);
            ftpClient.logout();
            System.out.println("删除文件成功");
        } catch (Exception e) {
            System.out.println("删除文件失败");
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    //从FTP server下载到本地文件夹
    public synchronized boolean download(FTPClient ftpClient, String sourcepath, String localPath) {
        if (ftpClient == null || !ftpClient.isConnected()) {
            logger.info("ftpClient is null or ftpClient is not connected");
            return false;
        }
        boolean flag = false;
        FTPFile[] fs = null;
        File localPathDir = new File(localPath);
        if  (!localPathDir .exists()  && !localPathDir .isDirectory()){
            localPathDir .mkdirs();
        }
        try {
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            if (!ftpClient.changeWorkingDirectory(sourcepath)) {
                String fileName = sourcepath.substring(sourcepath.lastIndexOf("/"), sourcepath.length());
                File localFile = new File(localPath + File.separator + fileName);
                //2、保存到本地
                OutputStream os = new FileOutputStream(localFile);
                //retrieveFile(FTP服务端的源路径),这个路径要和listFiles的路径一致
                ftpClient.retrieveFile(sourcepath, os);
                //3、删除FTP中的上面保存的文件
                //循环外关闭，读一个关闭一次挺慢的
                logger.info("download file : {} success", fileName);
                os.flush();
                os.close();
            } else {
                fs = ftpClient.listFiles(sourcepath);
                if (fs.length < 0) {
                    return flag;
                }
                //1、遍历FTP路径下所有文件
                for (FTPFile file : fs) {
                    if (file.isDirectory()) {
                        continue;
                    }
                    File localFile = new File(localPath + File.separator + file.getName());
                    //2、保存到本地
                    OutputStream os = new FileOutputStream(localFile);
                    //retrieveFile(FTP服务端的源路径),这个路径要和listFiles的路径一致
                    ftpClient.retrieveFile(file.getName(), os);
                    //3、删除FTP中的上面保存的文件
                    //循环外关闭，读一个关闭一次挺慢的
                    logger.info("download file : {} success", file.getName());
                    os.flush();
                    os.close();
                }

            }
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }


    public boolean upload(FTPClient ftpClient, InputStream inputStream, String fileName, String path) {
        try {
            if (ftpClient == null || !ftpClient.isConnected()) {
                logger.info("ftpClient is null or ftpClient is not connected");
                return false;
            }
            // 如果不能进入dir下，说明此目录不存在！
            if (!ftpClient.changeWorkingDirectory(path)) {
                mkdirsNew(ftpClient,path);
            }
            //切换工作路径，设置上传的路径
            ftpClient.changeWorkingDirectory(path);
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
            ftpClient.storeFile(fileName, inputStream);
            inputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean checkSubfolder(FTPClient ftpClient, String path, String subfolderName) {
        try {
            if (ftpClient == null || !ftpClient.isConnected()) {
                logger.info("ftpClient is null or ftpClient is not connected");
                return false;
            }
            //切换到FTP根目录
            ftpClient.changeWorkingDirectory(path);
            //查看根目录下是否存在该文件夹
            InputStream is = ftpClient.retrieveFileStream(new String(subfolderName.getBytes("UTF-8")));
            if (is == null || ftpClient.getReplyCode() == FTPReply.FILE_UNAVAILABLE) {
                //若不存在该文件夹，则创建文件夹
                return createSubfolder(ftpClient, path, subfolderName);
            }
            if (is != null) {
                is.close();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public synchronized boolean createSubfolder(FTPClient ftpClient, String path, String subfolderName) {
        try {
            if (ftpClient == null || !ftpClient.isConnected()) {
                logger.info("ftpClient is null or ftpClient is not connected");
                return false;
            }
            ftpClient.changeWorkingDirectory(path);
            ftpClient.makeDirectory(subfolderName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    /**
     * 断开与远程服务器的连接
     */
    public void closeClient(FTPClient ftpClient) {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public boolean rename(FTPClient ftpClient, String path, String pathname1, String pathname2) {
        try {
            if (ftpClient == null || !ftpClient.isConnected()) {
                logger.info("ftpClient is null or ftpClient is not connected");
                return false;
            }
            //切换到FTP根目录
            ftpClient.changeWorkingDirectory(path);
            return ftpClient.rename(pathname1, pathname2);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除文件夹
     *
     * @param pathname
     * @return
     */
    public boolean rmdir(FTPClient ftpClient, String pathname) {
        try {
            if (ftpClient == null || !ftpClient.isConnected()) {
                logger.info("ftpClient is null or ftpClient is not connected");
                return false;
            }
            return ftpClient.removeDirectory(pathname);
        } catch (Exception e) {
            return false;
        }
    }

    /***
     * 创建多个层级目录
     * @param dir dong/zzz/ddd/ewv
     * @return
     */
    public boolean mkdirs(FTPClient ftpClient, String dir) {
        if (ftpClient == null || !ftpClient.isConnected()) {
            logger.info("ftpClient is null or ftpClient is not connected");
            return false;
        }
        String[] dirs = dir.split("/");
        if (dirs.length == 0) {
            return false;
        }
        boolean stat = false;
        try {
            ftpClient.changeToParentDirectory();
            for (String dirss : dirs) {
                ftpClient.makeDirectory(dirss);
                ftpClient.changeWorkingDirectory(dirss);
            }

            ftpClient.changeToParentDirectory();
            stat = true;
        } catch (IOException e) {
            stat = false;
        }
        return stat;
    }

    /***
     * 创建目录
     * @param dir
     * @return
     */
    public boolean mkdir(FTPClient ftpClient, String dir) {
        if (ftpClient == null || !ftpClient.isConnected()) {
            logger.info("ftpClient is null or ftpClient is not connected");
            return false;
        }
        boolean stat = false;
        try {
            ftpClient.changeToParentDirectory();
            ftpClient.makeDirectory(dir);
            stat = true;
        } catch (IOException e) {
            stat = false;
        }
        return stat;
    }


    public FTPClient getFTPClient() {
        return new FTPClient();
    }

    /**
     * 修改后的ftp创建多级文件夹
     * @param ftpClient
     * @param dir
     * @return
     */
    public boolean mkdirsNew(FTPClient ftpClient, String dir) {
        if (ftpClient == null || !ftpClient.isConnected()) {
            logger.info("ftpClient is null or ftpClient is not connected");
            return false;
        }
        String[] dirs = dir.split("/");
        if (dirs.length == 0) {
            return false;
        }
        boolean stat = false;
        try {
            ftpClient.changeWorkingDirectory("/");
            for (String dirss : dirs) {
                ftpClient.makeDirectory(dirss);
                ftpClient.changeWorkingDirectory(dirss);
            }

            ftpClient.changeToParentDirectory();
            stat = true;
        } catch (IOException e) {
            stat = false;
        }
        return stat;
    }
}
