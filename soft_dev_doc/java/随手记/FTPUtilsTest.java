package cn.utstarcom.syncdr.controller;

import cn.utstarcom.syncdr.SyncdrApplication;
import cn.utstarcom.syncdr.utils.FTPUtil;
import cn.utstarcom.syncdr.utils.TimeUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.time.Instant;

/**
 * @author ut1403
 * @date 2021/5/18 13:27
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SyncdrApplication.class)
@TestPropertySource(locations = "file:src/test/java/test.properties")
@SpringBootTest
@AutoConfigureMockMvc
public class FTPUtilsTest {

    @Autowired
    private FTPUtil ftpUtil;

    @Test
    public void test() {
        Instant begin = Instant.now();
        System.out.println("test.start(): now: " + begin.getEpochSecond());
        new Thread(() -> ftpUtil.connectSourceServer(ftpUtil.getFTPClient())).start();
        new Thread(() -> ftpUtil.connectYNMobileServer(ftpUtil.getFTPClient())).start();
        new Thread(() -> ftpUtil.connectCommonDestServer(ftpUtil.getFTPClient())).start();
        new Thread(() -> ftpUtil.connectDestServer(ftpUtil.getFTPClient())).start();
        new Thread(() -> ftpUtil.connectTelecomServer(ftpUtil.getFTPClient())).start();
        new Thread(() -> ftpUtil.connectMobileServer(ftpUtil.getFTPClient())).start();
        new Thread(() -> ftpUtil.connectUnicomServer(ftpUtil.getFTPClient())).start();
        new Thread(() -> ftpUtil.connectIptvServer(ftpUtil.getFTPClient())).start();
        new Thread(() -> ftpUtil.connectAdvertTelecomServer(ftpUtil.getFTPClient())).start();
        new Thread(() -> ftpUtil.connectAdvertMobileServer(ftpUtil.getFTPClient())).start();
        new Thread(() -> ftpUtil.connectAdvertUnicomServer(ftpUtil.getFTPClient())).start();
        try{
            Thread.sleep(200000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("ftpUtil.all: useTime: " + Duration.between(begin, Instant.now()).toMillis());
    }
}
