package hello.springtx.apply;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class internalCallV2Test {

    @Autowired ExternalCallService externalCallService;

    @Test
    void externalCallV2() {
        externalCallService.external();
    }


    @TestConfiguration
    static class Config {
        @Bean
        InternalCallService internalCallService() {
            return new InternalCallService();
        }

        @Bean
        ExternalCallService externalCallService() {
            return new ExternalCallService(internalCallService());
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    static class ExternalCallService {

        private final InternalCallService internalCallService; //프록시 빈이 주입된다.

        public void external() {
            log.info("call external");
            printTxInfo();
            internalCallService.internal();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
        }
    }

    @Slf4j
    static class InternalCallService {
        @Transactional
        public void internal() {
            log.info("call internal");
            printTxInfo();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
        }
    }
}
